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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        initData()
        initView()
    }

    fun initData(){

    }

    fun initView(){

    }
}