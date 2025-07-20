package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DeviceCheck.isNewCameraVer
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

object LivePhotoEIS: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    override fun init() {

        try {
            if (isNewCameraVer()) {
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

                XposedBridge.log("OPCameraPro: find LivePhotoEIS Class: ${livePhotoClass}")

                loadClass(livePhotoClass.name)
                    .methodFinder()
                    .filterStatic()
                    .filterByReturnType(Boolean::class.java)
                    .filterByName("g")
                    .single()
                    .createHook {
                        returnConstant(true)
                    }
            }
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: find LivePhotoEIS error! ${e.message}")
            e.printStackTrace()
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



