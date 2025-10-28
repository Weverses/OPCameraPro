package com.tlsu.opluscamerapro.utils

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        // 修改键名，用于存储上一次运行的版本号
        private const val KEY_LAST_RUN_VERSION_CODE = "last_run_version_code"
    }

    override fun onCreate() {
        super.onCreate()

        // 将检查和复制逻辑放入一个单独的函数，使 onCreate 更清晰
        checkVersionAndCopyAssets()
    }

    private fun checkVersionAndCopyAssets() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // 获取之前保存的版本号，如果不存在，默认为 -1
        val lastRunVersionCode = prefs.getInt(KEY_LAST_RUN_VERSION_CODE, -1)

        // 获取当前应用的版本号
        val currentVersionCode = try {
            packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
        } catch (e: PackageManager.NameNotFoundException) {
            // 正常情况下不会发生，但作为保护
            -1
        }

        // 如果当前版本号与上次运行的版本号不一致，则执行复制操作
        if (currentVersionCode != lastRunVersionCode) {
            CoroutineScope(Dispatchers.IO).launch {
                val success = AssetUtils.copyAssetsToInternalStorage(
                    context = this@App,
                    assetPath = "meishe_lut",
                    destinationDir = filesDir
                )

                if (success) {
                    // 只有当文件成功复制后，才更新 SharedPreferences 中的版本号
                    prefs.edit().putInt(KEY_LAST_RUN_VERSION_CODE, currentVersionCode).apply()
                }
            }
        }
    }
}