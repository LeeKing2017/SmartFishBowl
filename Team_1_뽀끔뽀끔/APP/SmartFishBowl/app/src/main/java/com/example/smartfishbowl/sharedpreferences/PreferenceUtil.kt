package com.example.smartfishbowl.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesUtil(context:Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pref_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String{
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String){
        prefs.edit().putString(key, str).apply()
    }

    fun getInt(key: String, defvalue: Int): Int{
        return prefs.getInt(key, defvalue)
    }

    fun setInt(key: String, defvalue: Int){
        prefs.edit().putInt(key, defvalue).apply()
    }
}