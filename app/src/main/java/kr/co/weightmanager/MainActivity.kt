package kr.co.weightmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.weightmanager.data.WeightData
import kr.co.weightmanager.databinding.ActivityMainBinding
import kr.co.weightmanager.util.OgLog
import kr.co.weightmanager.util.UtilDate

import java.util.*

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

        insertWeight()
    }

    fun initData(){
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    fun initView(){

    }

    fun insertWeight(){

        var weight = WeightData()
        weight.uid = auth.uid
        weight.weight = 84.3f
        weight.dateTime = UtilDate.getCurrentDate()


        firestore.collection("weight").document()
            .set(weight)
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