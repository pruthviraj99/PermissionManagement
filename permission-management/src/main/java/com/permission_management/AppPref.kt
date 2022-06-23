package com.permission_management

import android.content.SharedPreferences

class AppPref(private var sharedPreferences: SharedPreferences) {

    // stores string value
    fun storePreference(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // get string value from SharedPreference
    fun getPreference(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue)!!
    }

    // stores boolean value
    fun storePreference(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    // stores int value
    fun storePreference(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    // get boolean value from SharedPreference
    fun getPreference(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    // get int value from SharedPreference
    fun getPreference(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    // stores int value
    fun storeFloatPreference(key: String, value: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    // get Float value from SharedPreference
    fun getFloatPreference(key: String, defValue: Float): Float {
        return sharedPreferences.getFloat(key, defValue)
    }

    // stores long value
    fun storeLongPreference(key: String, value: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    // get Long value from SharedPreference
    fun getLongPreference(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }
}