package com.lelestacia.tkmanagement

import android.app.Application
import com.lelestacia.tkmanagement.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application utama; memulai dependency injection Koin saat proses aplikasi dibuat.
 */
class TkManagementApp : Application() {
    /** Menginisialisasi Koin dengan logger, konteks aplikasi, dan modul [appModule]. */
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TkManagementApp)
            modules(appModule)
        }
    }
}
