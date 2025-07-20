package com.tlsu.opluscamerapro.hook.gallery

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.data.ConfigManager
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

object FeatureHook : BaseHook() {
    
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    
    override fun init() {
        try {
            XposedBridge.log("OPCameraPro: finding Config method")
            System.loadLibrary("dexkit")
            
            // 加载配置
            ConfigManager.reloadConfig()
            val config = ConfigManager.getConfig()
            val enableAIComposition = config.gallerySettings.enableAIComposition
            val enableAIEliminate = config.gallerySettings.enableAIEliminate
            val enableAIDeblur = config.gallerySettings.enableAIDeblur
            val enableAIQualityEnhance = config.gallerySettings.enableAIQualityEnhance
            val enableAIDeReflection = config.gallerySettings.enableAIDeReflection
            val enableAIBestTake = config.gallerySettings.enableAIBestTake
            val enableHasselblad = config.vendorTags.enableHasselbladWatermark
            val enableOliveCoverProXDR = config.gallerySettings.enableOliveCoverProXDR
            val enableLumoWatermark = config.gallerySettings.enableLumoWatermark

            XposedBridge.log("OPCameraPro: GallerySettings loaded, enableAIComposition = ${enableAIComposition}")

            val bridge = dexKitBridge
            val className = bridge.findClass {
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

            
            loadClass(className.name)
                .methodFinder()
                .filterByParamCount(4)
                .filterByParamTypes(String::class.java, Boolean::class.java, Boolean::class.java, Int::class.java)
                .filterStatic()
                .filterByReturnType(Boolean::class.java)
                .single()
                .createHook {
                    before {
                        if (it.args[0] == "feature_is_support_ai_composition") {
                            if (enableAIComposition) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_ai_composition success")
                            }
                        }
                        if (it.args[0] == "feature_is_support_ai_eliminate") {
                            if (enableAIEliminate) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_ai_eliminate success")
                            }
                        }
                        if (it.args[0] == "feature_is_support_ai_deblur") {
                            if (enableAIDeblur) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_ai_deblur success")
                            }
                        }
                        if (it.args[0] == "feature_is_support_image_quality_enhance") {
                            if (enableAIQualityEnhance) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_image_quality_enhance success")
                            }
                        }
                        if (it.args[0] == "feature_is_support_ai_dereflection") {
                            if (enableAIDeReflection) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_ai_dereflection success")
                            }
                        }
                        if (it.args[0] == "feature_is_support_hassel_watermark") {
                            if (enableHasselblad) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_hassel_watermark success")
                            }
                        }
                        if (it.args[0] == "feature_is_support_ai_best_take") {
                            if (enableAIBestTake) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_ai_best_take success")
                            }
                        }

//                        if (it.args[0] == "feature_is_support_ai_filter") {
//                            if (enableAI) {
//                                it.args[0] = "feature_is_support_olive"
//                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_hassel_watermark success")
//                            }
//                        }
//                        if (it.args[0] == "feature_is_support_ai_face_hd") {
//                            if (enableAI) {
//                                it.args[0] = "feature_is_support_olive"
//                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_hassel_watermark success")
//                            }
//                        }

                        if (it.args[0] == "feature_is_support_olive_sdr_to_hdr") {
                            if (enableOliveCoverProXDR) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_olive_sdr_to_hdr success")
                            }
                        }

                        if (it.args[0] == "feature_is_support_olive_extract_uhdr_frame") {
                            if (enableOliveCoverProXDR) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_olive_sdr_to_hdr success")
                            }
                        }

                        if (it.args[0] == "feature_is_support_lumo_watermark") {
                            if (enableLumoWatermark) {
                                it.args[0] = "feature_is_support_olive"
                                XposedBridge.log("OPCameraPro: Hook ${className.name} feature_is_support_olive_sdr_to_hdr success")
                            }
                        }
                    }
                }
            
            XposedBridge.log("OPCameraPro: Hook ${className.name} success")
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: Hook error! ${e.message}")
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




