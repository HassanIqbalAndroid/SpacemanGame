package com.example.saveship

import android.app.Application
import com.onesignal.OneSignal

class MainApplication : Application() {
    private val onesignal = "45e6a5eb-a5f7-4803-a9ad-84b290d395b2"
    override fun onCreate() {
        super.onCreate()

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(onesignal)

    }
}