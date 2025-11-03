package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

object DeviceName: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    override fun init() {
        // 获取配置
        val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()

        if (vendorTags.enableCustomWatermarkName) {
            try {
                val bridge = dexKitBridge
                val systemProp =
                    bridge.findMethod {
                        matcher {
                            usingStrings("SystemPropertiesAddonUt", "getSystemProperties fail, return default value")
                        }
                    }.singleOrNull()


                XposedBridge.log("OPCameraPro: find systemProp Method: ${systemProp}")
                systemProp?.getMethodInstance(safeClassLoader)?.createHook {
                    before { param ->
                        if (param.args[0] == "ro.vendor.oplus.market.name") {
                            param.result = vendorTags.deviceName
                        }
                        if (param.args[0] == "ro.vendor.oplus.market.enname") {
                            param.result = vendorTags.deviceName
                        }
                    }
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

object DeviceNameHook: BaseHook() {
    override fun init() {
        DeviceName.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            DeviceName.setLoadPackageParam(lpparam)
            init()
        }
    }
}



