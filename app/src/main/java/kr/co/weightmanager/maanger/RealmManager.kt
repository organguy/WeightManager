package kr.co.weightmanager.maanger

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kr.co.weightmanager.realm.RmWeightData
import kr.co.weightmanager.util.UtilDate

object RealmManager {

    fun getWeightResults(): RealmResults<RmWeightData>{

        var weightList = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime")
            .findAll()

        return weightList
    }

    fun isTodayDataExist() : Boolean{

        var todayDate = UtilDate.getCurrentDate()

        var weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .equalTo("dateTime", todayDate)
            .findFirst()

        return weightData != null
    }

    fun getCurrentData(): RmWeightData? {
        var weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime", Sort.DESCENDING)
            .findFirst()

        return weightData
    }

    fun insertWeightData(weightData: RmWeightData){
        Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().insertOrUpdate(weightData)
        Realm.getDefaultInstance().commitTransaction()
    }
}