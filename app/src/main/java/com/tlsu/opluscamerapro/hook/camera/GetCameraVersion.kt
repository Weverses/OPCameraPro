package com.tlsu.opluscamerapro.hook.camera

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DeviceCheck.setOPCameraVersion
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object GetCameraVersion: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    override fun init() {
        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "onCreate",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val appContext = param.thisObject as Context

                    try {
                        // 使用 appContext 获取 PackageManager
                        val pm = appContext.packageManager

                        // 获取 PackageInfo 对象
                        val packageInfo = pm.getPackageInfo("com.oplus.camera", 0)

                        // 从 PackageInfo 中提取版本
                        val sHostVersionName = packageInfo.versionName as String

                        setOPCameraVersion(sHostVersionName)

                        XposedBridge.log("OPCameraPro: get Camera Version Name: $sHostVersionName")
                    } catch (e: PackageManager.NameNotFoundException) {
                        XposedBridge.log("OPCameraPro: Failed to get package info: " + e.message)
                    }
                }
            })
    }

    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }
}

object GetCameraVersionHook: BaseHook() {
    override fun init() {
        GetCameraVersion.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            GetCameraVersion.setLoadPackageParam(lpparam)
            init()
        }
    }
}



