package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DefaultConfigManager
import com.tlsu.opluscamerapro.utils.DefaultConfigManager.saveOriginOtherConfig
import com.tlsu.opluscamerapro.utils.DeviceCheck.DEBUG
import com.tlsu.opluscamerapro.utils.ParseConfig.isOplusCameraConfig
import com.tlsu.opluscamerapro.utils.ParseConfig.parseConfig
import de.robv.android.xposed.XposedBridge

object OplusCameraConfig : BaseHook() {
    override fun init() {
        try {
            val clazz = loadClass("com.oplus.ocs.camera.consumer.apsAdapter.update.UpdateHelper")
            var configName = ""
            clazz
                .methodFinder()
                .filterByName("getValidConfigData")
                .single()
                .createHook {
                    before { param ->
                        configName = param.args[1] as String
                        if (DEBUG) {
                            param.args[1] = "/sdcard/Android/OplusCameraPro/origin/oplus_camera_preview_decision_config.json"
                        }
                    }
                    after {
                        val originalConfig = it.result as String
                        if (DEBUG) {
                            saveOriginOtherConfig(configName, originalConfig)
                        } else {
                            if (isOplusCameraConfig(configName)) {
                                XposedBridge.log("OPCameraPro: configName: $configName isOplusCameraConfig!")
                                // 保存原始配置
                                DefaultConfigManager.parseAndSaveDefaultConfig(originalConfig)
                                XposedBridge.log("OPCameraPro: original config saved successfully")

                                val modifyConfig = parseConfig(originalConfig)
                                DefaultConfigManager.saveModifyConfig(modifyConfig)
                                XposedBridge.log("OPCameraPro: get originalConfig successfully")
                                it.result = modifyConfig
                                XposedBridge.log("OPCameraPro: return modifyConfig successfully")
                            } else {
                                saveOriginOtherConfig(configName, originalConfig)
                                XposedBridge.log("OPCameraPro: configName: $configName! isn't OplusCameraConfig")
                            }
                        }
                    }
                }
            XposedBridge.log("OPCameraPro: hook getValidConfigData successfully")

        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: hook getValidConfigData  failed!")
            XposedBridge.log(e)
        }
    }
}