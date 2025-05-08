package com.tlsu.opluscamerapro.hook.gallery

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.data.ConfigManager
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig.getGallerySettings
import de.robv.android.xposed.XposedBridge
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.result.ClassData
import de.robv.android.xposed.callbacks.XC_LoadPackage

object FeatureHook : BaseHook() {
    
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    
    override fun init() {
        try {
            XposedBridge.log("OplusGalleryPro: finding Config method")
            System.loadLibrary("dexkit")
            
            // 加载配置


            ConfigManager.reloadConfig()
            val config = ConfigManager.getConfig()
            val enableAI = config.gallerySettings.enableAIComposition
            val enableHasselblad = config.vendorTags.enableHasselbladWatermark

            XposedBridge.log("OplusGalleryPro: GallerySettings loaded, enableAIComposition = ${enableAI}")

            var className: ClassData
            DexKitBridge.create(lpparam.appInfo.sourceDir).use { bridge ->
                className = bridge.findClass {
                    searchPackages("com.oplus.aiunit.vision")
                    matcher {
                        methods {
                            add {
                                usingStrings("ConfigAbilityWrapper")
                                usingStrings("getInt configAbility is null, configId:")
                            }
                        }
                    }
                }.single()
            }
            
            loadClass(className.name)
                .methodFinder()
                .filterByParamCount(4)
                .filterByParamTypes(String::class.java, Boolean::class.java, Boolean::class.java, Int::class.java)
                .filterStatic()
                .filterByReturnType(Boolean::class.java)
                .single()
                .createHook {
                    before {
                        XposedBridge.log("OplusGalleryPro: Hook triggered, enableAIComposition = $enableAI")
                        if (it.args[0] == "feature_is_support_ai_composition") {
                            if (enableAI) {
                                // 根据设置决定是否启用AI构图功能
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OplusGalleryPro: Hook ${className.name} feature_is_support_ai_composition success")
                            } else {
                                XposedBridge.log("OplusGalleryPro: AI Composition disabled by user settings (current value = $enableAI)")
                            }
                        }
                        
                        if (it.args[0] == "feature_is_support_hassel_watermark") {
                            if (enableHasselblad) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OplusGalleryPro: Hook ${className.name} feature_is_support_hassel_watermark success")
                            }
                        }
                    }
                }
            
            XposedBridge.log("OplusGalleryPro: Hook ${className.name} success")
        } catch (e: Throwable) {
            XposedBridge.log("OplusGalleryPro: Hook error! ${e.message}")
            e.printStackTrace()
        }
    }
    
    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }
}

object GalleryHook : BaseHook() {
    override fun init() {
        // 初始化AI构图功能Hook
        FeatureHook.init()
    }
    
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.coloros.gallery3d") {
            FeatureHook.setLoadPackageParam(lpparam)
            init()
        }
    }
}




