package kr.co.weightmanager.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RmWeightData : RealmObject(){

    @PrimaryKey
    var pk: String? = null
    var dateTime: Date? = null
    var uid: String? = null
    var weight: String = "0.0"

    override fun toString(): String {
        return "$dateTime  _ $uid _ $weight"
    }
}