package kr.co.weightmanager.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class RmWeightData : RealmObject(){

    @PrimaryKey
    var dateTime:String? = null
    var uid: String? = null
    var weight: Float = 0.0f
}