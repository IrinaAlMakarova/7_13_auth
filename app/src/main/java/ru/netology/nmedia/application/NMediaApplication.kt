package ru.netology.nmedia.application

import android.app.Application
import ru.netology.nmedia.auth.AppAuth

class NMediaApplication : Application() {
    //метод выполняющийся до создания активити
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}