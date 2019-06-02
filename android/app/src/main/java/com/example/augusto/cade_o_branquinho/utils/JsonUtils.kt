package com.example.augusto.cade_o_branquinho.utils

import android.content.Context
import java.io.InputStream

class JsonUtils {

    private var context: Context? = null

    constructor(context: Context) {
        this.context = context
    }

    fun readFile(filename: String) : String? {
        return try {
            val inputStream: InputStream = context!!.assets.open(filename)
            val inputString = inputStream.bufferedReader().use{it.readText()}
            inputString
        } catch (e:Exception){
            null
        }
    }

}