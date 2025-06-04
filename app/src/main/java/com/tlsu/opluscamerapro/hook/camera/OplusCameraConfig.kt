package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DefaultConfigManager
import com.tlsu.opluscamerapro.utils.DefaultConfigManager.saveOriginOtherConfig
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
                    }
                    after {
                        val originalConfig = it.result as String
                        if (isOplusCameraConfig(configName)) {
                            XposedBridge.log("OplusTest: configName: $configName isOplusCameraConfig!")
                            // 保存原始配置
                            DefaultConfigManager.parseAndSaveDefaultConfig(originalConfig)
                            XposedBridge.log("OplusTest: original config saved successfully")
                            
                            val modifyConfig = parseConfig(originalConfig)
                            DefaultConfigManager.saveModifyConfig(modifyConfig)
                            XposedBridge.log("OplusTest: get originalConfig successfully")
                            it.result = modifyConfig
                            XposedBridge.log("OplusTest: return modifyConfig successfully")
                        } else {
                            saveOriginOtherConfig(configName, originalConfig)
                            XposedBridge.log("OplusTest: configName: $configName! isn't OplusCameraConfig")
                        }
                    }
                }
            XposedBridge.log("OplusTest: hook getValidConfigData successfully")

        } catch (e: Throwable) {
            XposedBridge.log("OplusTest: hook getValidConfigData  failed!")
            XposedBridge.log(e)
        }
    }
}