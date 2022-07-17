package kr.co.weightmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.weightmanager.data.WeightData
import kr.co.weightmanager.databinding.ActivityMainBinding
import kr.co.weightmanager.dialog.WeightDialog
import kr.co.weightmanager.interfaces.InsertWeightListener
import kr.co.weightmanager.util.OgLog
import kr.co.weightmanager.util.UtilDate

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        initData()
        initView()

        //insertWeight()
        isTodayWeightExist(UtilDate.getCurrentDate());
    }

    fun initData(){
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    fun initView(){

    }

    private fun isTodayWeightExist(dateTime: String){
        firestore.collection("weight")
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .whereEqualTo("dateTime", dateTime)
            .get()
            .addOnCompleteListener {

               /* if(it.result.size() > 0){
                    Toast.makeText(this, "오늘 입력한 몸무게 데이터가 있습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    showWeightDialog()
                }*/

                showWeightDialog()
            }
    }

    private fun showWeightDialog(){
        val weightDialog = WeightDialog()
        weightDialog.isCancelable = false
        weightDialog.setOnInsertWeightListener(object : InsertWeightListener {
            override fun onResult(weight: Float) {
                insertWeight(weight)
            }
        })
        weightDialog.show(supportFragmentManager, "dialog")
    }

    fun insertWeight(weight: Float){

        var weightData = WeightData()
        weightData.uid = auth.uid
        weightData.weight = weight
        weightData.dateTime = UtilDate.getCurrentDate()


        firestore.collection("weight").document()
            .set(weightData)
            .addOnSuccessListener {
                Toast.makeText(this, "Insert Success!!!", Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener {
                Toast.makeText(this, "Insert Canceled!!!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                OgLog.d(it.localizedMessage)
            }
    }


}