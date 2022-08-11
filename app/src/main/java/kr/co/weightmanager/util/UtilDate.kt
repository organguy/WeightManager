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
        fun getDateToString(date: Date): String? {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val todayDateStr = format.format(date)
            return todayDateStr
        }


        @SuppressLint("SimpleDateFormat")
        fun getTodayDate(): Date? {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val date = cal.get(Calendar.DATE)

            val todayDate = getDate(year, month, date)

            return todayDate
        }

        @SuppressLint("SimpleDateFormat")
        fun getTodayDateString(): String? {
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd")
            val todayDateStr = format.format(cal.time)
            return todayDateStr
        }

        fun getYesterdayDate(): Date{
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)

            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val date = cal.get(Calendar.DATE)

            OgLog.d("yesterday : $year,$month,$date" )


            val yesterdayDate = getDate(year, month, date)

            return yesterdayDate
        }

        fun getAWeekAgoDate(): Date{
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -8)
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val date = cal.get(Calendar.DATE)

            val weekAgoDate = getDate(year, month, date)
            return weekAgoDate
        }

        fun getAMonthAgoDate(): Date{
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)

            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val date = cal.get(Calendar.DATE)

            val weekAgoDate = getDate(year, month, date)
            return weekAgoDate
        }
    }
}