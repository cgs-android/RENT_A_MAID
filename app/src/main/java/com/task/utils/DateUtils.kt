package com.task.utils

import android.os.Build
import com.task.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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
            SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT, Locale.getDefault())
        var convertedDate: Date? = Date()
        try {
            convertedDate = dateFormat.parse(date)
            return SimpleDateFormat(FORMATTED_DATE, Locale.getDefault()).format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun returnCurrentTime(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern(FORMATTED_TIME).format(LocalTime.now()).toString()
            } else {
                val date = Date()
                val formatter = SimpleDateFormat(FORMATTED_TIME, Locale.getDefault())
                formatter.format(date)
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun getCurrentDateTime(): String {
        val dateFormat: DateFormat = SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT, Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_DEFAULT, Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }


    fun returnCurrentTime(date: String): String {
        when (date.contains("T")) {
            true -> {
                val dateFormat =
                    SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT_FROM_SERVER, Locale.getDefault())
                var convertedDate: Date? = Date()
                try {
                    convertedDate = dateFormat.parse(date)
                    return SimpleDateFormat(FORMATTED_TIME, Locale.getDefault()).format(
                        convertedDate
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            false -> {
                val dateFormat =
                    SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT, Locale.getDefault())
                var convertedDate: Date? = Date()
                try {
                    convertedDate = dateFormat.parse(date)
                    return SimpleDateFormat(FORMATTED_TIME, Locale.getDefault()).format(
                        convertedDate
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }

        return ""
    }

}