package com.tlsu.opluscamerapro.hook.gallery

import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


object Hasselblad: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    override fun init() {
        // 获取配置
        val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()

        if (vendorTags.enableHasselbladWatermark) {
            try {
                val bridge = dexKitBridge
                val clas = bridge.findClass {
                    searchPackages("com.oplus.aiunit.vision")
                    matcher {
                        methods {
                            add {
                                usingStrings(", subWatermarkType=")
                            }
                        }
                    }
                }.single()

                XposedBridge.log("OPCameraPro: find Hasselblad class: ${clas}")

                XposedHelpers.findAndHookMethod(
                    ClassLoader::class.java,
                    "loadClass",
                    String::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val className = param.args[0] as String
                            if (className == clas.name) {
                                XposedBridge.log("OPCameraPro: find Hasselblad ClassLoader: ${param.thisObject}")
                                val clazz = param.result as Class<*>

                                XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
                                    override fun beforeHookedMethod(param: MethodHookParam) {
                                        param.args[0] = true
                                    }
                                })
                            }
                        }
                    }
                )
            } catch (e: Throwable) {
                XposedBridge.log("OPCameraPro: find Method error! ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }
}

object HasselbladWatermarkHook: BaseHook() {
    override fun init() {
        Hasselblad.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.coloros.gallery3d") {
            Hasselblad.setLoadPackageParam(lpparam)
            init()
        }
    }
}

