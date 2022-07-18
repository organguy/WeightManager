package kr.co.weightmanager.maanger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kr.co.weightmanager.data.WeightData
import kr.co.weightmanager.interfaces.OnResultListener
import kr.co.weightmanager.util.UtilDate

object FirestoreManager {


    fun getWeightList(dateTime: String, resultListener: OnResultListener<ArrayList<WeightData>>){


        var weightList = ArrayList<WeightData>()

        FirebaseFirestore.getInstance().collection("weight")
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .whereGreaterThan("dateTime", dateTime)
            .get()
            .addOnCompleteListener { it ->

                for(document in it.result){
                    var weightData = WeightData()
                    weightData.dateTime = document["dateTime"] as String
                    weightData.uid = document["uid"] as String
                    weightData.weight = document["weight"] as Float

                    weightList.add(weightData)
                }

                resultListener.onSuccess(weightList)
            }
            .addOnFailureListener {
                resultListener.onFail()
            }
            .addOnCanceledListener {
                resultListener.onFail()
            }
    }

    fun insertWeightData(weight: Float, resultListener: OnResultListener<WeightData>){

        var weightData = WeightData()
        weightData.uid = FirebaseAuth.getInstance().uid
        weightData.weight = weight
        weightData.dateTime = UtilDate.getCurrentDate()


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

}