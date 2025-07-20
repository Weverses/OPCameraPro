package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object Video120FPS: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    override fun init() {
        // 获取配置
        val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()

        if (vendorTags.enable1080p120fpsVideo || vendorTags.enable4K120fpsVideo) {
            try {

                XposedHelpers.findAndHookMethod("com.oplus.ocs.camera.configure.ConfigFeatureImpl",
                    safeClassLoader,
                    "isFeatureValueLegal",
                    String::class.java,
                    Any::class.java,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val tagName = param.args[0]
                            val tagValue = param.args[1]
                            if (tagName == "com.oplus.configure.video.fps" && tagValue == "video_120fps") {
                                param.setResult(true)
                            }
                        }
                    })

            } catch (e: Throwable) {
                XposedBridge.log("OPCameraPro: hook 120FPS error! ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }
}

object Video120FPSHook: BaseHook() {
    override fun init() {
        Video120FPS.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            Video120FPS.setLoadPackageParam(lpparam)
            init()
        }
    }
}



