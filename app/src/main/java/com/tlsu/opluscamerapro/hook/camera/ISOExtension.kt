package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.luckypray.dexkit.result.MethodData

object ISOExtension: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    override fun init() {
        // 获取配置
        val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()

        if (vendorTags.enableISOExtension) {
            var isSupportProExtensionLambda: MethodData?
            var ISO: MethodData?

            try {
                val bridge = dexKitBridge
                isSupportProExtensionLambda =
                    bridge.findMethod {
                        matcher {
                            usingStrings("OplusCameraCharacteristics")
                            usingStrings("getSupportProExtensionISO, fail to get metadata")
                        }
                    }.singleOrNull()

                ISO =
                    bridge.findMethod {
                        matcher {
                            usingStrings("OplusCameraCharacteristics")
                            usingStrings("getProExtensionIsoRange, fail to get metadata")
                        }
                    }.singleOrNull()

                XposedBridge.log("OPCameraPro: find isSupportProExtensionISO Method: ${isSupportProExtensionLambda}")
                XposedBridge.log("OPCameraPro: find ISO Method: ${ISO}")

                isSupportProExtensionLambda?.getMethodInstance(safeClassLoader)?.createHook {
                    returnConstant(false)
                }

                ISO?.getMethodInstance(safeClassLoader)?.createHook {
                    returnConstant(intArrayOf(10, 12800))
                }

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

object ISOHook: BaseHook() {
    override fun init() {
        ISOExtension.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            ISOExtension.setLoadPackageParam(lpparam)
            init()
        }
    }
}



