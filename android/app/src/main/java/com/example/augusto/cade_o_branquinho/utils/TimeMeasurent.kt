package com.example.augusto.cade_o_branquinho.utils

import org.json.JSONObject


class TimeMeasurent(private var jsonUtils: JsonUtils) {

    private val TIME_MEASUREMENT_FILE_NAME = "time_measurement.json"
    private var times: HashMap<String, Int> = HashMap()

    init {
        readJson()
    }

    private fun readJson() {
        val data: String = jsonUtils!!.readFile(TIME_MEASUREMENT_FILE_NAME) ?: return
        val jsonObj = JSONObject(data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1))
        val keys = jsonObj.keys()

        times = HashMap()

        while (keys.hasNext()) {
            val key = keys.next()
            val time= jsonObj.getInt(key)

            times.set(key, time)
        }

    }

    fun getNextTime(busStopId: String) : Int {
        return if (times[busStopId] != null) {
            times[busStopId]!!
        } else {
            0
        }
    }

}