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

    fun getTodayWeightData(): RmWeightData{
        var weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime", Sort.DESCENDING)
            .findFirst()

        return weightData!!
    }

    fun getYesterdayWeightData(): RmWeightData{
        var weightList = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime", Sort.DESCENDING)
            .findAll()

        return weightList[1]!!
    }

    fun getDailyDiff(): Double{
        var todayData = getTodayWeightData()
        var yesterdayData = getYesterdayWeightData()

        var todayWeight = todayData.weight.toDouble()
        var yesterdayWeight = yesterdayData.weight.toDouble()

        var diff = todayWeight - yesterdayWeight

        return diff
    }

    fun getMaxWeight(): String{
        var weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("weight", Sort.DESCENDING)
            .findFirst()

        return weightData!!.weight
    }

    fun getMinWeight(): String{
        var weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("weight", Sort.ASCENDING)
            .findFirst()

        return weightData!!.weight
    }

    fun deleteAll(){
        Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().delete(RmWeightData::class.java)
        Realm.getDefaultInstance().commitTransaction()
    }
}