package kr.co.weightmanager

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kr.co.weightmanager.databinding.ActivityIntroBinding
import kr.co.weightmanager.databinding.ActivityMainBinding
import kr.co.weightmanager.util.UtilSystem

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
            auth = FirebaseAuth.getInstance()

            if(auth.currentUser != null){
                gotoMain()
            }else{
                initGoogleLogin()
            }
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

                //Toast.makeText(this, "Failed Google Login", Toast.LENGTH_SHORT).show()
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
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // 인증에 성공한 후, 현재 로그인된 유저의 정보를 가져올 수 있습니다.
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