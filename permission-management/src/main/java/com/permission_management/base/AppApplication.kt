package com.permission_management.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.permission_management.BuildConfig
import timber.log.Timber

class AppApplication : Application() {

    companion object {
        var context: Context? = null
        lateinit var pref: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        pref = context!!.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}