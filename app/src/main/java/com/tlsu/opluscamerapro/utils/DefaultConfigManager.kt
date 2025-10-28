package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import android.content.Context
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.data.DefaultTagInfo
import com.tlsu.opluscamerapro.data.DefaultValueConfig
import de.robv.android.xposed.XposedBridge
import org.json.JSONArray
import org.json.JSONObject
import com.topjohnwu.superuser.Shell
import java.io.File

/**
 * 默认配置管理器
 * 负责解析原始相机配置、保存默认配置文件、加载默认配置
 */
object DefaultConfigManager {
    private const val TAG = "OPCameraPro"
    
    @SuppressLint("SdCardPath")
    private const val ORIGIN_CONFIG_FILE = "/sdcard/Android/OplusCameraPro/oplus_camera_config_origin.json"

    private const val ORIGIN_CONFIG_PATH = "/sdcard/Android/OplusCameraPro"

    @SuppressLint("SdCardPath")
    private const val MODIFY_CONFIG_FILE = "/sdcard/Android/OplusCameraPro/oplus_camera_config_modify.json"
    
    @SuppressLint("SdCardPath")
    private const val DEFAULT_CONFIG_FILE = "/sdcard/Android/OplusCameraPro/configDefault.json"
    
    // 用于缓存加载的默认配置
    private var cachedConfig: DefaultValueConfig? = null
    
    // VendorTag到ConfigBasedAddConfig中功能的映射关系
    private val vendorTagToFeature = mapOf(
        "com.oplus.turboraw.re.support" to "enable25MP",
        "com.oplus.feature.effect.style.support" to "enableMasterMode",
        "com.oplus.feature.master.hq.raw.support" to "enableMasterRawMax",
        "com.oplus.rear.portrait.zoom.support" to "enablePortraitZoom",
        "com.oplus.feature.video.720p.60fps.support" to "enable720p60fps",
        "com.oplus.feature.slowvideo.ultra.wide.480fps.support" to "enableSlowVideo480fps",
        "com.oplus.feature.macro.closeup.max.zoom.value" to "enableNewMacroMode",
        "com.oplus.feature.macro.closeup.none.sat.tele.support" to "enableMacroTele",
        "com.oplus.feature.macro.depth.of.field.fusion.support" to "enableMacroDepthFusion",
        "com.oplus.heif.blur.edit.in.gallery.support" to "enableHeifBlurEdit",
        "com.oplus.feature.effect.style.support" to "enableStyleEffect",
        "com.oplus.camera.feature.scale.focus" to "enableScaleFocus",
        "com.oplus.camera.livephoto.support.fov.optimize" to "enableLivePhotoFovOptimize",
        "com.oplus.10bits.heic.encode.support" to "enable10bitPhoto",
        "com.oplus.camera.heif.support.livephoto" to "enableHeifLivePhoto",
        "com.oplus.livephoto.support.10bit" to "enable10bitLivePhoto",
        "com.oplus.tol.style.filter.support" to "enableTolStyleFilter",
        "com.oplus.support.grand.tour.filter" to "enableGrandTourFilter",
        "com.oplus.desert.filter.type.support" to "enableDesertFilter",
        "com.oplus.vignette.grain.filter.type.support" to "enableVignetteGrainFilter",
        "com.oplus.director.filter.upgrade.support" to "enableDirectorFilter",
        "com.oplus.director.filter.support" to "enableJiangWenFilter",
        "com.oplus.support.jzk.movie.filter" to "enableJzkMovieFilter",
        "com.oplus.feature.face.beauty.custom.menu.version" to "enableNewBeautyMenu",
        "com.oplus.feature.super.text.scanner.support" to "enableSuperTextScanner",
        "com.ocs.camera.ipu.soft.light.photo.mode.support" to "enableSoftLightPhotoMode",
        "com.ocs.camera.ipu.soft.light.night.mode.support" to "enableSoftLightNightMode",
        "com.ocs.camera.ipu.soft.light.professional.mode.support" to "enableSoftLightProMode",
        "com.ocs.camera.ipu.meishe.filter.support" to "enableMeisheFilter",
        "com.oplus.camera.preview.hdr.support" to "enablePreviewHdr",
        "com.oplus.video.auto.fps.setting.support" to "enableVideoAutoFps",
        "com.oplus.feature.quick.launch.support" to "enableQuickLaunch",
        "com.oplus.camera.livephoto.video.bitrate" to "enableLivePhotoHighBitrate",
        "com.oplus.video.stop.record.sound.play.immediate" to "enableVideoStopSoundImmediate",
        "com.oplus.force.portrait.when.parse.intent" to "enableForcePortraitForThirdParty",
        "com.oplus.feature.front.camera.wide.zoom.support" to "enableFrontCameraZoom",
        "com.oplus.portrait.rear.flash.support" to "enablePortraitRearFlash",
        "com.oplus.ai.hd.switch.support" to "enableAiHdSwitch",
        "com.oplus.tele.sdsr.support" to "enableTeleSdsr",
        "com.oplus.feature.video.dv.support" to "enableDolbyVideo",
        "com.oplus.feature.video.dv.60fps.support" to "enableDolbyVideo60fps",
        "com.oplus.feature.video.dv.sat.support" to "enableDolbyVideoSat",
        "com.oplus.feature.video.front.dv.support" to "enableFrontDolbyVideo",
        "com.oplus.video.lock.lens.support" to "enableVideoLockLens",
        "com.oplus.video.lock.wb.support" to "enableVideoLockWb",
        "com.oplus.feature.mic.status.check.support" to "enableMicStatusCheck",
        "com.oplus.use.hasselblad.style.support" to "enableMasterFilter",
        "com.oplus.hasselblad.watermark.guide.support" to "enableHasselbladWatermarkGuide",
        "com.oplus.camera.support.custom.hasselblad.watermark" to "enableHasselbladWatermark",
        "com.oplus.camera.support.custom.hasselblad.watermark.sellmode.default.open" to "enableHasselbladWatermarkDefault",
        "com.oplus.portrait.global.ev.support" to "enableGlobalEv",
        "com.oplus.feature.os15.new.filter.support" to "enableOs15NewFilter",
        "com.oplus.switch.lens.focal.length.support" to "enableSwitchLensFocalLength",
        "com.oplus.motion.capture.support" to "enableMotionCapture",
        "com.oplus.feature.video.4k.120fps.support" to "enable4K120fpsVideo",
        "com.oplus.feature.video.1080p.120fps.support" to "enable1080p120fpsVideo",
        "com.oplus.feature.video.dv.120fps.support" to "enableDolbyVideo120fps",
        "com.oplus.support.multi.frame.burst.shot" to "enableMultiFrameBurstShot",
        "com.oplus.video.sound.focus.support" to "enableVideoSoundFocus",
        "com.oplus.feature.front.video.4k.support" to "enableFront4KVideo",
        "com.oplus.ai.scene.preset.support" to "enableAiScenePreset",
        "com.oplus.camera.livephoto.support" to "enableLivePhoto",
        "com.oplus.camera.livephoto.mastermode.support" to "enableMasterModeLivePhoto",
        "com.oplus.feature.soft.light.filter.support" to "enableSoftLightFilter",
        "com.oplus.feature.flash.filter.support" to "enableFlashFilter",
        "com.oplus.feature.xpan.mode.support" to "enableXPAN",
        "com.oplus.gr.mode.support" to "enableGRFilter",
        "com.oplus.gr.mode.support" to "enableGRWatermark"
    )
    
    // 保存功能名称到VendorTag的反向映射
    private val featureToVendorTag = vendorTagToFeature.entries.associate { (k, v) -> v to k }

    /**
     * 从原始相机配置文件加载默认配置
     * @return 默认配置对象
     */
    fun loadDefaultConfig(): DefaultValueConfig {
        // 如果已有缓存，直接返回缓存的配置
        cachedConfig?.let { return it }
        
        if (!File(DEFAULT_CONFIG_FILE).exists()) {
            return DefaultValueConfig()
        }
        
        try {
            val result = Shell.cmd("cat $DEFAULT_CONFIG_FILE").exec()
            if (result.isSuccess) {
                val jsonStr = result.out.joinToString("\n")
                if (jsonStr.isNotBlank()) {
                    val json = JSONObject(jsonStr)
                    val vendorTagsObj = json.optJSONObject("vendorTags") ?: JSONObject()
                    
                    val vendorTags = mutableMapOf<String, DefaultTagInfo>()
                    for (key in vendorTagsObj.keys()) {
                        val tagObj = vendorTagsObj.getJSONObject(key)
                        vendorTags[key] = DefaultTagInfo(
                            vendorTag = tagObj.getString("vendorTag"),
                            type = tagObj.getString("type"),
                            value = tagObj.getString("value"),
                            isEnabled = tagObj.getBoolean("isEnabled")
                        )
                    }
                    
                    return DefaultValueConfig(vendorTags).also { cachedConfig = it }
                }
            }
        } catch (e: Exception) {
            XposedBridge.log("$TAG: Error loading default config: ${e.message}")
        }
        
        return DefaultValueConfig()
    }
    
    /**
     * 解析原始相机配置并保存默认配置
     * @param originalJson 原始相机配置JSON字符串
     */
    fun parseAndSaveDefaultConfig(originalJson: String) {
        try {
            // 保存原始配置
            saveOriginConfig(originalJson)
            
            // 解析原始配置 - 适配两种不同的JSON格式
            val jsonArray = try {
                // 尝试直接解析为JSONArray（格式1: 直接的数组）
                if (originalJson.trim().startsWith("[")) {
                    JSONArray(originalJson)
                } else {
                    // 尝试解析为JSONObject，然后获取file_data字段（格式2: {file_version:x, file_data:[...]}）
                    val jsonObject = JSONObject(originalJson)
                    if (jsonObject.has("file_data")) {
                        jsonObject.getJSONArray("file_data")
                    } else {
                        // 如果既不是数组开头，也没有file_data字段，记录错误并创建空数组
                        XposedBridge.log("$TAG: Unrecognized JSON format, neither direct array nor object with file_data")
                        JSONArray()
                    }
                }
            } catch (e: Exception) {
                XposedBridge.log("$TAG: Error parsing JSON: ${e.message}")
                JSONArray() // 出错时返回空数组
            }
            
            val defaultTags = mutableMapOf<String, DefaultTagInfo>()
            
            // 创建一个集合来跟踪已处理的标签
            val processedTags = mutableSetOf<String>()
            
            // 先遍历原始配置中的标签
            for (i in 0 until jsonArray.length()) {
                val tagObj = jsonArray.getJSONObject(i)
                val vendorTag = tagObj.getString("VendorTag")
                
                // 检查是否是我们关心的标签
                vendorTagToFeature[vendorTag]?.let { featureName ->
                    val type = tagObj.getString("Type")
                    val value = tagObj.getString("Value")
                    val isEnabled = when (type) {
                        "Byte" -> value == "1"
                        "Float" -> value.toFloatOrNull()?.let { it > 0 } ?: false
                        "Int32" -> value.toIntOrNull()?.let { it > 0 } ?: false
                        else -> false
                    }
                    
                    defaultTags[featureName] = DefaultTagInfo(
                        vendorTag = vendorTag,
                        type = type,
                        value = value,
                        isEnabled = isEnabled
                    )
                    
                    // 标记为已处理
                    processedTags.add(featureName)
                }
            }
            
            // 现在处理未在原始配置中找到的标签
            for (featureName in featureToVendorTag.keys) {
                if (!processedTags.contains(featureName)) {
                    val vendorTag = featureToVendorTag[featureName]!!
                    
                    // 确定默认类型（大多数情况为"Byte"）
                    val defaultType = when {
                        vendorTag.contains("version", ignoreCase = true) -> "Float"
                        vendorTag.contains("bitrate", ignoreCase = true) -> "Int32"
                        vendorTag.contains("value", ignoreCase = true) && 
                            (vendorTag.contains("zoom", ignoreCase = true) || 
                             vendorTag.contains("ratio", ignoreCase = true)) -> "Float"
                        else -> "Byte"
                    }
                    
                    // 创建一个默认的关闭状态标签
                    defaultTags[featureName] = DefaultTagInfo(
                        vendorTag = vendorTag,
                        type = defaultType,
                        value = "0",
                        isEnabled = false
                    )
                }
            }
            
            // 创建默认配置对象
            val config = DefaultValueConfig(defaultTags)
            
            // 保存配置到文件
            saveDefaultConfig(config)
            
            // 更新缓存
            cachedConfig = config
            
            XposedBridge.log("$TAG: Default config parsed and saved successfully")
        } catch (e: Exception) {
            XposedBridge.log("$TAG: Error parsing and saving default config: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * 保存原始配置文件
     */
    private fun saveOriginConfig(originalJson: String) {
        try {
            // 创建目录
            Shell.cmd("mkdir -p /sdcard/Android/OplusCameraPro").exec()
            
            // 写入文件
            val file = File(ORIGIN_CONFIG_FILE)
            file.writeText(originalJson)
            
            XposedBridge.log("$TAG: Original config saved successfully")
        } catch (e: Exception) {
            XposedBridge.log("$TAG: Error saving original config: ${e.message}")
        }
    }

    fun saveOriginOtherConfig(originalJsonName: String, originalJson: String) {
        try {
            // 创建目录
            Shell.cmd("mkdir -p /sdcard/Android/OplusCameraPro").exec()

            // 写入文件
            val file = File("ORIGIN_CONFIG_PATH/originalJsonName")
            file.writeText(originalJson)

            XposedBridge.log("$TAG: Original config saved successfully")
        } catch (e: Exception) {
            XposedBridge.log("$TAG: Error saving original config: ${e.message}")
        }
    }

    /**
     * 保存原始配置文件
     */
    fun saveModifyConfig(modifyJson: String) {
        try {
            // 创建目录
            Shell.cmd("mkdir -p /sdcard/Android/OplusCameraPro").exec()

            // 写入文件
            val file = File(MODIFY_CONFIG_FILE)
            file.writeText(modifyJson)

            XposedBridge.log("$TAG: Modified config saved successfully")
        } catch (e: Exception) {
            XposedBridge.log("$TAG: Error saving modified config: ${e.message}")
        }
    }

    /**
     * 保存默认配置文件
     */
    private fun saveDefaultConfig(config: DefaultValueConfig) {
        try {
            // 转换为JSON
            val json = JSONObject()
            val vendorTagsObj = JSONObject()
            
            config.vendorTags.forEach { (key, tagInfo) ->
                val tagObj = JSONObject()
                tagObj.put("vendorTag", tagInfo.vendorTag)
                tagObj.put("type", tagInfo.type)
                tagObj.put("value", tagInfo.value)
                tagObj.put("isEnabled", tagInfo.isEnabled)
                vendorTagsObj.put(key, tagObj)
            }
            
            json.put("vendorTags", vendorTagsObj)
            
            // 写入文件
            val file = File(DEFAULT_CONFIG_FILE)
            file.writeText(json.toString(4))
            
            XposedBridge.log("$TAG: Default config saved successfully")
        } catch (e: Exception) {
            XposedBridge.log("$TAG: Error saving default config: ${e.message}")
        }
    }

    /**
     * 获取功能的默认值描述（使用字符串资源）
     * @param context 上下文
     * @param featureName 功能名称
     * @return 默认值描述文本 (例如："默认值：开")
     */
    fun getDefaultValueDescription(context: Context, featureName: String): String {
        val config = loadDefaultConfig()
        val defaultTag = config.vendorTags[featureName]
        
        return if (defaultTag != null) {
            if (defaultTag.isEnabled) {
                context.getString(R.string.default_value_enabled)
            } else {
                context.getString(R.string.default_value_disabled)
            }
        } else {
            context.getString(R.string.default_value_unknown)
        }
    }
    fun isDefaultValueEnableFunction(context: Context, featureName: String): Boolean {
        val config = loadDefaultConfig()
        val defaultTag = config.vendorTags[featureName]

        return if (defaultTag != null) {
            if (defaultTag.isEnabled) {
                true
            } else {
                false
            }
        } else {
            true
        }
    }
} 