package kr.co.weightmanager.maanger

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kr.co.weightmanager.realm.RmWeightData
import kr.co.weightmanager.util.UtilDate

object RealmManager {

    fun getWeightResults(): RealmResults<RmWeightData>{

        val weightList = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime")
            .findAll()

        return weightList
    }

    fun isTodayDataExist() : Boolean{

        val todayDate = UtilDate.getCurrentDateString()

        val weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .equalTo("pk", todayDate)
            .findFirst()

        return weightData != null
    }

    fun getCurrentData(): RmWeightData? {
        val weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
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
        val weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime", Sort.DESCENDING)
            .findFirst()

        return weightData!!
    }

    fun getYesterdayWeightData(): RmWeightData? {
        val weightList = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime", Sort.DESCENDING)
            .findAll()

        return if(weightList.size > 1){
            weightList[1]!!
        }else{
            null
        }
    }

    fun getDailyDiff(): Double{
        val todayData = getTodayWeightData()
        val yesterdayData = getYesterdayWeightData()

        return if(yesterdayData != null){
            val todayWeight = todayData.weight.toDouble()
            val yesterdayWeight = yesterdayData.weight.toDouble()

            val diff = todayWeight - yesterdayWeight

            diff
        }else{
            0.0
        }
    }

    fun getThisWeekWeight(): Double{
        var yerterdayDate = UtilDate.getYesterdayDate()
        var weekAfterDate = UtilDate.getWeekAfterDate()

       /* var weightList = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .bet("dateTime", weekAfterDate, yerterdayDate)
            .findAll()*/

        return 0.0
    }

    /*fun getWeeklyDiff(): Double{
        var todayData = getTodayWeightData()
        var yesterdayData = getYesterdayWeightData()

        var todayWeight = todayData.weight.toDouble()
        var yesterdayWeight = yesterdayData.weight.toDouble()

        var diff = todayWeight - yesterdayWeight

        return diff
    }*/

    fun getMaxWeight(): String{
        val weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("weight", Sort.DESCENDING)
            .findFirst()

        return weightData!!.weight
    }

    fun getMinWeight(): String{
        val weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
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