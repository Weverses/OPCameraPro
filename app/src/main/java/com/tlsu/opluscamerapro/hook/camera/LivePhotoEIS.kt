package com.tlsu.opluscamerapro.hook.camera

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DeviceCheck.checkVersionName
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object LivePhotoEIS: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    private var sVersionName: String = ""

    override fun init() {
        try {
            XposedHelpers.findAndHookMethod(
                Application::class.java,
                "onCreate",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {

                        if (sVersionName.isNotEmpty()) {
                            return
                        }

                        val appContext = param.thisObject as Context

                        try {
                            val pm = appContext.packageManager
                            val packageInfo = pm.getPackageInfo("com.oplus.camera", 0)
                            sVersionName = packageInfo.versionName as String

                            XposedBridge.log("OPCameraPro: get Camera Version Name: $sVersionName")

                            if (checkVersionName(sVersionName)) {
                                XposedBridge.log("OPCameraPro: hook LivePhotoEIS")
                                val bridge = dexKitBridge
                                val livePhotoClass = bridge.findClass {
                                    matcher {
                                        methods {
                                            add {
                                                usingStrings("updateGalleryLivePhotoFovMeta, sGalleryLivePhotoFovMeta:")
                                            }
                                        }
                                    }
                                }.single()

                                XposedBridge.log("OPCameraPro: find LivePhotoEIS Class: ${livePhotoClass.name}")

                                loadClass(livePhotoClass.name)
                                    .methodFinder()
                                    .filterStatic()
                                    .filterByReturnType(Boolean::class.java)
                                    .filterByName("g")
                                    .single()
                                    .createHook {
                                        returnConstant(true)
                                    }
                                XposedBridge.log("OPCameraPro: LivePhotoEIS hooked successfully!")
                            }

                        } catch (e: PackageManager.NameNotFoundException) {
                            XposedBridge.log("OPCameraPro: Failed to get package info: " + e.message)
                        } catch (e: Throwable) {
                            XposedBridge.log("OPCameraPro: find LivePhotoEIS error! ${e.message}")
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: Failed to set up Application.onCreate hook: ${e.message}")
        }
    }

    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }
}

object LivePhotoEISHook: BaseHook() {
    override fun init() {
        LivePhotoEIS.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            LivePhotoEIS.setLoadPackageParam(lpparam)
            init()
        }
    }
}
