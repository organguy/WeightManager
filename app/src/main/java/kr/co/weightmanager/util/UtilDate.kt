package kr.co.weightmanager.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class UtilDate {
    companion object{
        fun getDate(year: Int, month: Int, date: Int): Date {
            val cal = Calendar.getInstance()
            cal.set(year, month-1, date, 0, 0, 0)
            return Date(cal.timeInMillis)
        }


        @SuppressLint("SimpleDateFormat")
        fun getCurrentDate(): Date? {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val date = cal.get(Calendar.DATE)

            val currentDate = getDate(year, month, date)

            return currentDate
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateString(): String? {
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = format.format(cal.time)
            return currentDate
        }

        fun getYesterdayDate(): String{
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            cal.add(Calendar.DATE, -1)
            val yesterdayDate = format.format(cal.time)
            return yesterdayDate
        }

        fun getWeekAfterDate(): String{
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            cal.add(Calendar.DATE, -7)
            val yesterdayDate = format.format(cal.time)
            return yesterdayDate
        }
    }
}