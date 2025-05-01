package com.tlsu.opluscamerapro

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.hook.camera.FilterGroup
import com.tlsu.opluscamerapro.hook.camera.OplusCameraConfig
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val TAG = "OplusPro"
private val PACKAGE_NAME_HOOKED = setOf(
    "com.oplus.camera"
)

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName in PACKAGE_NAME_HOOKED) {
            // Init EzXHelper
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
            EzXHelper.setToastTag(TAG)
            // Init hooks
            when (lpparam.packageName) {
                "com.oplus.camera" -> {
                    initHooks(FilterGroup)
                    initHooks(OplusCameraConfig)
                }
            }
        }
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            runCatching {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                Log.i("Inited hook: ${it.javaClass.simpleName}")
            }.logexIfThrow("Failed init hook: ${it.javaClass.simpleName}")
        }
    }
}
