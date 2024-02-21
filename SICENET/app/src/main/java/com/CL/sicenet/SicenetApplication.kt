package com.CL.sicenet

import android.app.Application
import com.CL.sicenet.data.AppContainer
import com.CL.sicenet.data.DefaultAppContainer

class SicenetApplication : Application(){
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}