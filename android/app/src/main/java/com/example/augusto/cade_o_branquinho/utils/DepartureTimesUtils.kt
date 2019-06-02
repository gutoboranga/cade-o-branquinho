package com.example.augusto.cade_o_branquinho.utils

import android.content.Context
import org.json.JSONObject

class DepartureTimesUtils {

    private var context: Context? = null
    private var jsonUtils: JsonUtils? = null

    private val FILE_NAME = "departures.json"
    private val WEEK_DAYS_TAG = "week_days"
    private val SATURDAY_TAG = "saturday"

    constructor(context: Context) {
        this.context = context
        this.jsonUtils = JsonUtils(context)
    }

    fun getWeekDays() : ArrayList<String>? {
        val data: String = jsonUtils!!.readFile(FILE_NAME) ?: return null

        val jsonObj = JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val weekDaysJsonArray = jsonObj.getJSONArray(WEEK_DAYS_TAG)

        val arrayList = ArrayList<String>()
        for (i in 0..(weekDaysJsonArray.length() - 1)) {
            arrayList.add(weekDaysJsonArray.getString(i))
        }

        return arrayList
    }

    fun getSaturdays() : ArrayList<String>? {
        val data: String = jsonUtils!!.readFile(FILE_NAME) ?: return null

        val jsonObj = JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val weekDaysJsonArray = jsonObj.getJSONArray(SATURDAY_TAG)

        val arrayList = ArrayList<String>()
        for (i in 0..(weekDaysJsonArray.length() - 1)) {
            arrayList.add(weekDaysJsonArray.getString(i))
        }

        return arrayList
    }

}