package kr.co.weightmanager.maanger

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kr.co.weightmanager.maanger.RealmManager.getTodayWeightData
import kr.co.weightmanager.realm.RmWeightData
import kr.co.weightmanager.util.OgLog
import kr.co.weightmanager.util.UtilDate
import kotlin.math.round

object RealmManager {

    fun getWeightResults(): RealmResults<RmWeightData>{

        val weightList = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("dateTime")
            .findAll()

        return weightList
    }

    fun isTodayDataExist() : Boolean{

        val todayDate = UtilDate.getTodayDateString()

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
            val todayWeight = todayData.weight
            val yesterdayWeight = yesterdayData.weight

            val diff = todayWeight - yesterdayWeight
            round(diff*10)/10

        }else{
            0.0
        }
    }

    fun getWeekAvgWeight(): Double{
        val todayDate = UtilDate.getTodayDate()
        val weekAfterDate = UtilDate.getAWeekAgoDate()

        val avgWeight = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .lessThan("dateTime",todayDate)
            .greaterThan("dateTime",weekAfterDate)
            .average("weight")

        val count = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .lessThan("dateTime",todayDate)
            .greaterThan("dateTime",weekAfterDate)
            .count()

        OgLog.d("week count : $count")

        return avgWeight
    }

    fun getWeeklyDiff(): Double{
        val todayData = getTodayWeightData()

        val todayWeight = todayData.weight
        val weekAvgWeight = getWeekAvgWeight()

        val diff = todayWeight - weekAvgWeight

        return diff
    }

    fun getMonthAvgWeight(): Double{
        val todayDate = UtilDate.getTodayDate()
        val monthAfterDate = UtilDate.getAMonthAgoDate()

        val avgWeight = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .lessThan("dateTime",todayDate)
            .greaterThan("dateTime",monthAfterDate)
            .average("weight")

        val count = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .lessThan("dateTime",todayDate)
            .greaterThan("dateTime",monthAfterDate)
            .count()

        OgLog.d("week count : $count")

        return avgWeight
    }

    fun getMonthlyDiff(): Double{
        val todayData = getTodayWeightData()

        val todayWeight = todayData.weight
        val monthAvgWeight = getMonthAvgWeight()

        val diff = todayWeight - monthAvgWeight

        return diff
    }

    fun getMaxWeight(): Double{
        val weightData = Realm.getDefaultInstance().where(RmWeightData::class.java)
            .sort("weight", Sort.DESCENDING)
            .findFirst()

        return weightData!!.weight
    }

    fun getMinWeight(): Double{
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