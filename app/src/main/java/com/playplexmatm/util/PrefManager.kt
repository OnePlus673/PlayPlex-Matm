package com.playplexmatm.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface

class PrefManager {

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var _context: Context? = null

    var PRIVATE_MODE = 0
    private val PREF_NAME = "UserData"
    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    private val LOGIN_ID = "LOGIN"
    var Type = "image"

    var scriptable: Typeface? = null

    val NIGHT_MODE = "NIGHT_MODE"

    constructor(context: Context) {
        _context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref!!.edit()
    }


    fun setvalue(key: String?, value: Boolean) {
        editor!!.putBoolean(key, value)
        editor!!.commit()
    }

    fun getvalue(key: String?, defaultvalue: Boolean?): Boolean {
        return pref!!.getBoolean(key, defaultvalue!!)
    }

    fun setValue(key: String?, value: String?) {
        editor!!.putString(key, value)
        editor!!.commit()
    }

    fun getValue(key: String?): String? {
        return pref!!.getString(key, "0")
    }

    fun setValue(key: String?, value: Int?) {
        editor!!.putInt(key, value!!)
        editor!!.commit()
    }

    fun getvalue(key: String?): Int? {
        return pref!!.getInt(key, 0)
    }


    fun clear() {
        pref!!.edit().clear().commit()
    }
}