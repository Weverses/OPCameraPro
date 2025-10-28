package com.tlsu.opluscamerapro.hook.camera // 或者其他合适的包名

import com.github.kyuubiran.ezxhelper.Log
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object PreviewHDR : BaseHook() {

    private var TARGET_CLASS = ""
    private const val TARGET_METHOD = "h"
    private const val IS_FRONT_METHOD_NAME = "isFrontCamera"
    private val logTag = "OPCameraPro[PreviewHDRHook]"

    private var lpparam: XC_LoadPackage.LoadPackageParam? = null

    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }

    override fun init() {
        val currentLpparam = lpparam ?: run {
            Log.e("$logTag: lpparam is null, skipping init.")
            return
        }

        val bridge = dexKitBridge
        val previewHDRClass = bridge.findClass {
            matcher {
                methods {
                    add {
                        usingStrings("DATASPACE_DISPLAY_P3_HLG")
                    }
                }
            }
        }.single()

        TARGET_CLASS = previewHDRClass.name
        XposedBridge.log("$logTag: Initializing hook for $TARGET_CLASS.$TARGET_METHOD in ${currentLpparam.packageName}")

        try {
            XposedHelpers.findAndHookMethod(
                previewHDRClass.name,
                currentLpparam.classLoader,
                TARGET_METHOD,
                String::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            val c0Instance = param.thisObject ?: return

                            val cameraInstance: Any? = try {
                                XposedHelpers.getObjectField(c0Instance, "b")
                            } catch (e: Throwable) {
                                Log.e("$logTag: Failed to get field 'b' instance.", e)
                                null // 获取失败
                            }

                            if (cameraInstance == null) {
                                Log.w("$logTag: Could not get camera instance (field 'b'). Cannot check camera facing.")
                                return
                            }
                            val isFront = XposedHelpers.callMethod(cameraInstance, IS_FRONT_METHOD_NAME) as? Boolean

                            if (isFront == true) {
                                Log.i("$logTag: Front camera detected via interface. Forcing h() to return false. Original was: ${param.result}")
                                param.result = false // 强制返回 false
                            }

                        } catch (t: Throwable) {
                            Log.e("$logTag: Error inside afterHookedMethod for $TARGET_CLASS.$TARGET_METHOD", t)
                        }
                    }
                }
            )
            Log.i("$logTag: Successfully hooked $TARGET_CLASS.$TARGET_METHOD")
        } catch (e: Throwable) {
            Log.e("$logTag: Failed to hook $TARGET_CLASS.$TARGET_METHOD", e)
        }
    }
}

object PreviewHDRHook: BaseHook() {
    override fun init() {
        PreviewHDR.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            PreviewHDR.setLoadPackageParam(lpparam)
            init()
        }
    }
}
