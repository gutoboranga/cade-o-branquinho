package com.example.augusto.cade_o_branquinho.utils

import android.content.Context
import java.io.InputStream

class JsonUtils(private var mContext: Context) {

    fun readFile(filename: String) : String? {
        return try {
            val inputStream: InputStream = mContext.assets.open(filename)
            val inputString = inputStream.bufferedReader().use{it.readText()}
            inputString
        } catch (e:Exception){
            null
        }
    }

}