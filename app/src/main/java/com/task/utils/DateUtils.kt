package com.task.utils

import com.task.DATE_FORMAT_DEFAULT
import com.task.FORMATTED_DATE
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun isTodayOrTomorrowProject(currentDate: String, serverDate: String): Int {
        val df =
            SimpleDateFormat(FORMATTED_DATE, Locale.getDefault())
        val dateCurrent: Date = df.parse(currentDate)
        val serverCurrent: Date = df.parse(serverDate)
        val dayBetween = ((dateCurrent.time - serverCurrent.time) / (24 * 60 * 60 * 1000)).toInt()
        if (dateCurrent.equals(serverCurrent)) {
            return 1
        } else if (dayBetween == 1 || dayBetween == -1) {
            return 0
        } else {
            return -1
        }
    }

    fun returnCurrentDate(): String {
        val c = Calendar.getInstance().time
        val df =
            SimpleDateFormat(FORMATTED_DATE, Locale.getDefault())
        return df.format(c)
    }

    fun formatDate(date: String): String {
        val dateFormat =
            SimpleDateFormat(DATE_FORMAT_DEFAULT, Locale.getDefault())
        var convertedDate: Date? = Date()
        try {
            convertedDate = dateFormat.parse(date)
            return SimpleDateFormat(FORMATTED_DATE, Locale.getDefault()).format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

}