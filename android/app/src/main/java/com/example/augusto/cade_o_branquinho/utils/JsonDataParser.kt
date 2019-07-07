package com.example.augusto.cade_o_branquinho.utils

import android.content.Context
import com.example.augusto.cade_o_branquinho.model.DepartureTime
import org.json.JSONObject

class JsonDataParser(private var jsonUtils: JsonUtils) {

    private val DEPARTURES_FILE_NAME = "departures.json"
    private val WEEK_DAYS_TAG = "week_days"
    private val SATURDAY_TAG = "saturday"
    private val DELIMITER = ":"

    fun run() {
        DepartureTimes.saturdays = getSaturdays()
        DepartureTimes.weekdays = getWeekDays()
    }

    fun getWeekDays() : ArrayList<DepartureTime> {
        val data: String = jsonUtils!!.readFile(DEPARTURES_FILE_NAME ) ?: return ArrayList<DepartureTime>()

        val jsonObj = JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val weekDaysJsonArray = jsonObj.getJSONArray(WEEK_DAYS_TAG)

        val arrayList = ArrayList<DepartureTime>()
        for (i in 0..(weekDaysJsonArray.length() - 1)) {
            val parts = weekDaysJsonArray.getString(i).split(DELIMITER)
            val time = DepartureTime(parts[0].toInt(), parts[1].toInt())

            arrayList.add(time)
        }

        return arrayList
    }

    fun getSaturdays() : ArrayList<DepartureTime> {
        val data: String = jsonUtils!!.readFile(DEPARTURES_FILE_NAME ) ?: return ArrayList<DepartureTime>()

        val jsonObj = JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val saturdaysJsonArray = jsonObj.getJSONArray(SATURDAY_TAG)

        val arrayList = ArrayList<DepartureTime>()
        for (i in 0..(saturdaysJsonArray .length() - 1)) {
            val parts = saturdaysJsonArray .getString(i).split(DELIMITER)
            val time = DepartureTime(parts[0].toInt(), parts[1].toInt())

            arrayList.add(time)
        }

        return arrayList
    }

}