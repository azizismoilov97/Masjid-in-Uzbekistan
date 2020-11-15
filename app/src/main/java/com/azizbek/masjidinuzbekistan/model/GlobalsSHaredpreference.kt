package com.azizbek.masjidinuzbekistan.model

import android.app.Activity
import android.content.Context

object GlobalsSHaredpreference {

    fun saveState(activity: Activity, key: String?, value: Boolean) {
        val sharedPreferences = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getState(activity: Activity, key: String?): Boolean {
        val sharedPreferences = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }

    fun saveLocation(activity: Activity, key: String?, value: Double) {
        val sharedPreferences = activity.getSharedPreferences("Result", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value.toFloat())
        editor.apply()
    }

    fun getLocation(activity: Activity, key: String?): Float {
        val sharedPreferences = activity.getSharedPreferences("Result", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat(key, 0f)
    }

    fun saveLocationAddress(activity: Activity, key: String?, value: String) {
        val sharedPreferences = activity.getSharedPreferences("Result", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getLocationAddress(activity: Activity, key: String?): String {
        val sharedPreferences = activity.getSharedPreferences("Result", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "Tashkent, Uzbekistan").toString()
    }

}