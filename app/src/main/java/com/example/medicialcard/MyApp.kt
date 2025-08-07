package com.example.medicialcard

import android.app.Application
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.utils.JwtManager

class MyApp : Application() {
    companion object {
        lateinit var instance: MyApp

    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        JwtManager.init(instance)
        RetrofitClient.init()
    }




}
