package com.tlsu.opluscamerapro

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.hook.camera.FilterGroup
import com.tlsu.opluscamerapro.hook.camera.ISOHook
import com.tlsu.opluscamerapro.hook.camera.LivePhotoEISHook
import com.tlsu.opluscamerapro.hook.camera.OplusCameraConfig
import com.tlsu.opluscamerapro.hook.camera.ProtobufFeatureHook
import com.tlsu.opluscamerapro.hook.camera.UnitSDKPatchHook
import com.tlsu.opluscamerapro.hook.camera.Video120FPSHook
import com.tlsu.opluscamerapro.hook.gallery.GalleryHook
import com.tlsu.opluscamerapro.hook.gallery.HasselbladWatermarkHook
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV16
import com.tlsu.opluscamerapro.utils.DexKit
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val TAG = "OplusCameraPro"

private val PACKAGE_NAME_HOOKED = setOf(
    "com.oplus.camera",
    "com.coloros.gallery3d"
)

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName in PACKAGE_NAME_HOOKED) {
            if (lpparam.isFirstApplication) {
                DexKit.initDexKit(lpparam)
                EzXHelper.initHandleLoadPackage(lpparam)
                EzXHelper.setLogTag(TAG)
                EzXHelper.setToastTag(TAG)
            }
            // Init hooks
            when (lpparam.packageName) {
                "com.oplus.camera" -> {
                    // 初始化所有Hook
                    initHooks(FilterGroup)
                    initHooks(OplusCameraConfig)
                    ISOHook.handleLoadPackage(lpparam)
                    Video120FPSHook.handleLoadPackage(lpparam)
                    LivePhotoEISHook.handleLoadPackage(lpparam)
                    ProtobufFeatureHook.handleLoadPackage(lpparam)
                    if (!isV16()) {
                        UnitSDKPatchHook.handleLoadPackage(lpparam)
                    }
                }
                "com.coloros.gallery3d" -> {
                    GalleryHook.handleLoadPackage(lpparam)
                    HasselbladWatermarkHook.handleLoadPackage(lpparam)
                    initHooks(FilterGroup)
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
