package com.example.augusto.cade_o_branquinho.utils

import android.content.Context
import org.json.JSONObject
import org.json.JSONArray
import java.io.InputStream

class DepartureTimesUtils {

    var context: Context? = null

    val FILE_NAME = "departures.json"
    val WEEK_DAYS_TAG = "week_days"
    val SATURDAY_TAG = "saturday"

    constructor(context: Context) {
        this.context = context
    }

    fun getWeekDays() : ArrayList<String>? {
        val data: String = jsonAsString() ?: return null

        val jsonObj = JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val weekDaysJsonArray = jsonObj.getJSONArray(WEEK_DAYS_TAG)

        val arrayList = ArrayList<String>()
        for (i in 0..(weekDaysJsonArray.length() - 1)) {
            arrayList.add(weekDaysJsonArray.getString(i))
        }

        return arrayList
    }

    fun getSaturdays() : ArrayList<String>? {
        val data: String = jsonAsString() ?: return null

        val jsonObj = JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val weekDaysJsonArray = jsonObj.getJSONArray(SATURDAY_TAG)

        val arrayList = ArrayList<String>()
        for (i in 0..(weekDaysJsonArray.length() - 1)) {
            arrayList.add(weekDaysJsonArray.getString(i))
        }

        return arrayList
    }

    private fun jsonAsString(): String? {
        return try {
            val inputStream: InputStream = context!!.assets.open(FILE_NAME)
            val inputString = inputStream.bufferedReader().use{it.readText()}
            inputString
        } catch (e:Exception){
            null
        }

    }

}