package kr.co.weightmanager.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RmWeightData : RealmObject(){

    @PrimaryKey
    var dateTime:String? = null
    var uid: String? = null
    var weight: String = "0.0"

    override fun toString(): String {
        return "$dateTime  _ $uid _ $weight"
    }
}