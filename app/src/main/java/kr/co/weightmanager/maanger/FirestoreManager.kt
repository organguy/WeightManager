package kr.co.weightmanager.maanger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.type.DateTime
import kr.co.weightmanager.data.WeightData
import kr.co.weightmanager.interfaces.OnResultListener
import kr.co.weightmanager.util.OgLog
import kr.co.weightmanager.util.UtilDate
import java.util.*
import kotlin.collections.ArrayList

object FirestoreManager {


    fun getWeightList(dateTime: Date, resultListener: OnResultListener<ArrayList<WeightData>>){


        val weightList = ArrayList<WeightData>()

        FirebaseFirestore.getInstance().collection("weight")
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .whereGreaterThan("dateTime", dateTime)
            .get()
            .addOnCompleteListener { it ->
                try{
                    for(document in it.result){
                        val weightData = WeightData()
                        weightData.dateTime = document.getDate("dateTime")
                        weightData.uid = document.getString("uid")
                        weightData.weight = document.getDouble("weight")!!

                        weightList.add(weightData)
                    }

                    resultListener.onSuccess(weightList)
                }catch (e: FirebaseFirestoreException) {
                    OgLog.d(e.localizedMessage)
                    resultListener.onSuccess(weightList)
                }
            }
            .addOnFailureListener {
                resultListener.onFail()
            }
            .addOnCanceledListener {
                resultListener.onFail()
            }
    }

    fun insertWeightData(weight: Double, resultListener: OnResultListener<WeightData>){

        val weightData = WeightData()
        weightData.pk = UtilDate.getTodayDateString()
        weightData.uid = FirebaseAuth.getInstance().uid
        weightData.weight = weight
        weightData.dateTime = UtilDate.getTodayDate()


        FirebaseFirestore.getInstance().collection("weight").document()
            .set(weightData)
            .addOnSuccessListener {
                resultListener.onSuccess(weightData)
            }
            .addOnCanceledListener {
                resultListener.onFail()
            }
            .addOnFailureListener {
                resultListener.onFail()
            }
    }

    fun getAppVersion(resultListener: OnResultListener<String>){
        FirebaseFirestore.getInstance().collection("settings")
            .document("EYZ2sXIPAHTlF7US9xou")
            .get()
            .addOnCompleteListener { it ->
                try{
                    val version = it.result.getString("version")
                    if (version != null) {
                        resultListener.onSuccess(version)
                    }
                }catch (e: FirebaseFirestoreException) {
                    OgLog.d(e.localizedMessage)
                    resultListener.onFail()
                }
            }
            .addOnFailureListener {
                resultListener.onFail()
            }
            .addOnCanceledListener {
                resultListener.onFail()
            }

    }

}