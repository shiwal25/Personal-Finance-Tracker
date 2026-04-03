package com.example.personalfinancetracker

import android.app.Application
import com.example.personalfinancetracker.data.AppContainer
import com.example.personalfinancetracker.data.AppDataContainer

class PersonalFinanceTrackerApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}