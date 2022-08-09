package kr.co.weightmanager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kr.co.weightmanager.data.WeightData
import kr.co.weightmanager.databinding.ActivityIntroBinding
import kr.co.weightmanager.dialog.WeightDialog
import kr.co.weightmanager.interfaces.InsertGoalListener
import kr.co.weightmanager.interfaces.OnResultListener
import kr.co.weightmanager.maanger.FirestoreManager
import kr.co.weightmanager.maanger.PropertyManager
import kr.co.weightmanager.maanger.RealmManager
import kr.co.weightmanager.realm.RmWeightData
import kr.co.weightmanager.util.OgLog
import kr.co.weightmanager.util.UtilDate
import kr.co.weightmanager.util.UtilSystem
import java.util.*
import kotlin.collections.ArrayList

class IntroActivity : AppCompatActivity() {


    lateinit var binding : ActivityIntroBinding

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var client: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater);
        setContentView(binding.root)

        checkNetwork()
    }

    private fun checkNetwork(){
        if(UtilSystem.checkNetworkState(this)){
           checkLogin()
        }else{
            showNetworkCheckDialog()
        }
    }


    private fun showNetworkCheckDialog(){
        AlertDialog.Builder(this)
            .setTitle(R.string.network_disconnect)
            .setMessage(R.string.msg_network_disconnect)
            .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialogInterface, i ->
                finish()
            }).show()
    }

    private fun checkLogin(){
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null){
            initGoogleLogin()
        }else{
            OgLog.d(auth.currentUser!!.uid)
            checkVersion()
        }
    }

    private fun initGoogleLogin(){
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        client = GoogleSignIn.getClient(this, options)

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            var account: GoogleSignInAccount? = null
            try {
                account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!.idToken)
            } catch (e: ApiException) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            getResult.launch(client.signInIntent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 인증에 성공한 후, 현재 로그인된 유저의 정보를 가져올 수 있습니다.
                        //checkWeightData()
                        checkVersion()
                    }
                })
    }

    private fun checkVersion(){
        FirestoreManager.getAppVersion(object : OnResultListener<String> {
            override fun onSuccess(version: String) {
                OgLog.d("version : $version")
                PropertyManager.setVersion(version)

                checkWeightData()
            }

            override fun onFail() {
                OgLog.d("version : fail")
                checkWeightData()
            }
        })
    }

    private fun checkWeightData(){
        var weightData = RealmManager.getCurrentData()

        var dateTime = UtilDate.getDate(2000, 1, 1)

        if(weightData != null){
            dateTime = weightData.dateTime!!
        }

        if (dateTime != null) {
            updateWeightData(dateTime)
        }


    }

    private fun updateWeightData(dateTime: Date){
        FirestoreManager.getWeightList(dateTime, object : OnResultListener<ArrayList<WeightData>> {
            override fun onSuccess(result: ArrayList<WeightData>) {
                for(data in result){
                    var weightData = RmWeightData()
                    weightData.weight = data.weight
                    weightData.dateTime = data.dateTime
                    weightData.uid = data.uid

                    RealmManager.insertWeightData(weightData)
                }

               /* if(RealmManager.isTodayDataExist()){
                    gotoMain()
                }else{
                    showWeightDialog()
                }*/
            }

            override fun onFail() {
                //gotoMain()
            }
        })
    }

    private fun showWeightDialog(){
        val weightDialog = WeightDialog()
        weightDialog.isCancelable = false
        weightDialog.setOnInsertWeightListener(object : InsertGoalListener {
            override fun onResult(weight: String) {
                insertTodayWeight(weight)
            }
        })
        weightDialog.show(supportFragmentManager, "dialog")
    }

    private fun insertTodayWeight(weight: String){
        FirestoreManager.insertWeightData(weight, object: OnResultListener<WeightData>{
            override fun onSuccess(data: WeightData) {
                Toast.makeText(this@IntroActivity, R.string.msg_write_weight_success, Toast.LENGTH_SHORT).show()

                var weightData = RmWeightData()
                weightData.pk = data.pk
                weightData.weight = data.weight
                weightData.dateTime = data.dateTime
                weightData.uid = data.uid

                RealmManager.insertWeightData(weightData)

                gotoMain()
            }

            override fun onFail() {
                Toast.makeText(this@IntroActivity, R.string.msg_write_weight_fail, Toast.LENGTH_SHORT).show()
                gotoMain()
            }

        })
    }

    private fun gotoMain(){

        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}