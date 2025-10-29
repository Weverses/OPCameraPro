package com.tlsu.opluscamerapro.utils

import android.app.Application

class App : Application() {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_IS_ASSET = "is_asset"
    }

    override fun onCreate() {
        super.onCreate()

//        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val success = AssetUtils.copyAssetsToInternalStorage(
//                context = this@App,
//                assetPath = "meishe_lut",
//                destinationDir = filesDir
//            )
//
//            if (success) {
//                prefs.edit().putBoolean(KEY_IS_ASSET, false).apply()
//            }
//        }

    }
}