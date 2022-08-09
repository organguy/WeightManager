package kr.co.weightmanager.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class UtilDate {
    companion object{
        fun getDate(year: Int, month: Int, date: Int): Date? {
            val cal = Calendar.getInstance()
            cal.set(year, month-1, date)
            return Date(cal.timeInMillis)
        }


        @SuppressLint("SimpleDateFormat")
        fun getCurrentDate(): Date? {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val date = cal.get(Calendar.DAY_OF_MONTH)

            val currentDate = getDate(year, month, date)

            return currentDate
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateString(): String? {
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            var currentDate = format.format(cal.time)
            return currentDate
        }

        fun getYesterdayDate(): String{
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            cal.add(Calendar.DATE, -1)
            var yesterdayDate = format.format(cal.time)
            return yesterdayDate
        }

        fun getWeekAfterDate(): String{
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            cal.add(Calendar.DATE, -7)
            var yesterdayDate = format.format(cal.time)
            return yesterdayDate
        }
    }
}