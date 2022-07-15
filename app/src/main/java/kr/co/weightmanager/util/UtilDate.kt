package kr.co.weightmanager.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class UtilDate {
    companion object{
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDate(): String{
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            var currentDate = format.format(cal.time)
            return currentDate
        }
    }
}