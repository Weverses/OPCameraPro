package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import com.github.kyuubiran.ezxhelper.Log
import com.tlsu.opluscamerapro.data.AppConfig
import com.tlsu.opluscamerapro.data.VendorTagSettings
import com.tlsu.opluscamerapro.utils.DeviceCheck.DEBUG
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV16
import com.tlsu.opluscamerapro.utils.ParseConfig.addPresetTag
import com.topjohnwu.superuser.Shell
import de.robv.android.xposed.XposedBridge
import org.json.JSONObject

/**
 * 基于配置的AddConfig类
 * 读取用户配置文件，根据配置决定是否添加特定的VendorTag
 */
object ConfigBasedAddConfig {
    private const val TAG = "ConfigBasedAddConfig"
    @SuppressLint("SdCardPath")
    private const val CONFIG_FILE = "/sdcard/Android/OplusCameraPro/config.json"
    
    // 存储配置信息
    var config: AppConfig? = null
    
    // 初始化标志，避免重复初始化Shell
    private var isInitialized = false
    
    init {
        try {
            if (!isInitialized) {
                // 只在第一次初始化时设置Shell
                try {
                    Shell.enableVerboseLogging = false
                    Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER))
                    XposedBridge.log("OPCameraPro: Shell initialized successfully")
                } catch (e: Exception) {
                    // 如果Shell已经初始化，会抛出异常，可以忽略这个错误
                    XposedBridge.log("OPCameraPro: Shell already initialized: ${e.message}")
                }
                isInitialized = true
            }

            // 尝试读取配置文件
            loadConfig()
        } catch (e: Exception) {
            XposedBridge.log("OPCameraPro init error: ${e.message}")
        }
    }
    
    /**
     * 获取当前的VendorTag设置
     * @return 当前的VendorTag设置，如果配置未加载则返回默认设置
     */
    fun getVendorTagSettings(): VendorTagSettings {
        // 确保配置已加载
        if (config == null) {
            reloadConfig()
        }
        return config?.vendorTags ?: VendorTagSettings()
    }

    /**
     * 加载配置文件
     */
    private fun loadConfig() {
        try {
            // 检查文件是否存在
            val fileExistsResult = Shell.cmd("[ -f $CONFIG_FILE ] && echo \"true\" || echo \"false\"").exec()
            val fileExists = fileExistsResult.out.joinToString("").trim() == "true"
            
            if (fileExists) {
                // 使用Shell读取文件
                    val result = Shell.cmd("cat $CONFIG_FILE").exec()
                    if (result.isSuccess) {
                        val jsonStr = result.out.joinToString("\n")
                        if (jsonStr.isNotBlank()) {
                            try {
                                XposedBridge.log(
                                    "OplusCameraPro: Read config content: ${
                                        jsonStr.take(
                                            50
                                        )
                                    }..."
                                )
                                config = parseConfig(jsonStr)
                                XposedBridge.log("OPCameraPro: Config loaded successfully")
                            } catch (e: Exception) {
                                XposedBridge.log("OPCameraPro: Failed to parse config: ${e.message}")
                                config = AppConfig() // 解析错误时使用默认配置
                            }
                        } else {
                            XposedBridge.log("OPCameraPro: Empty config file, using defaults")
                            config = AppConfig()
                        }
                    } else {
                        XposedBridge.log("OPCameraPro: Failed to read config file: ${result.out}")
                        config = AppConfig()
                    }

            } else {
                XposedBridge.log("OPCameraPro: Config file not found, using defaults")
                config = AppConfig() // 文件不存在时使用默认配置
            }
        } catch (e: Exception) {
            XposedBridge.log("OPCameraPro: Error in loadConfig: ${e.message}")
            e.printStackTrace()
            config = AppConfig() // 出错时使用默认配置
        }
    }
    
    /**
     * 重新加载配置文件
     * 在每次添加配置前调用，确保使用最新的配置
     */
    fun reloadConfig() {
        loadConfig()
        XposedBridge.log("OPCameraPro: Config reloaded successfully")
    }
    
    /**
     * 解析配置JSON
     */
    private fun parseConfig(jsonStr: String): AppConfig {
        return try {
            val json = JSONObject(jsonStr)
            
            // 解析VendorTag设置
            val vendorTagsObj = json.optJSONObject("vendorTags") ?: JSONObject()
            val vendorTags = VendorTagSettings(
                enable25MP = vendorTagsObj.optBoolean("enable25MP", false),
                enableMasterMode = vendorTagsObj.optBoolean("enableMasterMode", false),
                enableMasterRawMax = vendorTagsObj.optBoolean("enableMasterRawMax", false),
                enablePortraitZoom = vendorTagsObj.optBoolean("enablePortraitZoom", false),
                enable720p60fps = vendorTagsObj.optBoolean("enable720p60fps", false),
                enableSlowVideo480fps = vendorTagsObj.optBoolean("enableSlowVideo480fps", false),
                enableNewMacroMode = vendorTagsObj.optBoolean("enableNewMacroMode", false),
                enableMacroTele = vendorTagsObj.optBoolean("enableMacroTele", false),
                enableMacroDepthFusion = vendorTagsObj.optBoolean("enableMacroDepthFusion", false),
                enableHeifBlurEdit = vendorTagsObj.optBoolean("enableHeifBlurEdit", false),
                enableStyleEffect = vendorTagsObj.optBoolean("enableStyleEffect", false),
                enableScaleFocus = vendorTagsObj.optBoolean("enableScaleFocus", false),
                enableLivePhotoFovOptimize = vendorTagsObj.optBoolean("enableLivePhotoFovOptimize", false),
                enable10bitPhoto = vendorTagsObj.optBoolean("enable10bitPhoto", false),
                enableHeifLivePhoto = vendorTagsObj.optBoolean("enableHeifLivePhoto", false),
                enable10bitLivePhoto = vendorTagsObj.optBoolean("enable10bitLivePhoto", false),
                enableTolStyleFilter = vendorTagsObj.optBoolean("enableTolStyleFilter", false),
                enableMasterFilter = vendorTagsObj.optBoolean("enableMasterFilter", false),
                enableGrandTourFilter = vendorTagsObj.optBoolean("enableGrandTourFilter", false),
                enableDesertFilter = vendorTagsObj.optBoolean("enableDesertFilter", false),
                enableVignetteGrainFilter = vendorTagsObj.optBoolean("enableVignetteGrainFilter", false),
                enableJzkMovieFilter = vendorTagsObj.optBoolean("enableJzkMovieFilter", false),
                enableNewBeautyMenu = vendorTagsObj.optBoolean("enableNewBeautyMenu", false),
                enableSuperTextScanner = vendorTagsObj.optBoolean("enableSuperTextScanner", false),
                enableSoftLightPhotoMode = vendorTagsObj.optBoolean("enableSoftLightPhotoMode", false),
                enableSoftLightNightMode = vendorTagsObj.optBoolean("enableSoftLightNightMode", false),
                enableSoftLightProMode = vendorTagsObj.optBoolean("enableSoftLightProMode", false),
                enableMeisheFilter = vendorTagsObj.optBoolean("enableMeisheFilter", false),
                enablePreviewHdr = vendorTagsObj.optBoolean("enablePreviewHdr", false),
                enableVideoAutoFps = vendorTagsObj.optBoolean("enableVideoAutoFps", false),
                enableQuickLaunch = vendorTagsObj.optBoolean("enableQuickLaunch", false),
                enableLivePhotoHighBitrate = vendorTagsObj.optBoolean("enableLivePhotoHighBitrate", false),
                livePhotoBitrate = vendorTagsObj.optInt("livePhotoBitrate", 45),
                livePhotoMaxDuration = vendorTagsObj.optInt("livePhotoMaxDuration", 3200),
                livePhotoMinDuration = vendorTagsObj.optInt("livePhotoMinDuration", 500),
                enableVideoStopSoundImmediate = vendorTagsObj.optBoolean("enableVideoStopSoundImmediate", false),
                enableForcePortraitForThirdParty = vendorTagsObj.optBoolean("enableForcePortraitForThirdParty", false),
                enableFrontCameraZoom = vendorTagsObj.optBoolean("enableFrontCameraZoom", false),
                enablePortraitRearFlash = vendorTagsObj.optBoolean("enablePortraitRearFlash", false),
                enableAiHdSwitch = vendorTagsObj.optBoolean("enableAiHdSwitch", false),
                aiHdZoomValue = vendorTagsObj.optInt("aiHdZoomValue", 60),
                enableTeleSdsr = vendorTagsObj.optBoolean("enableTeleSdsr", false),
                teleSdsrZoomValue = vendorTagsObj.optInt("teleSdsrZoomValue", 20),
                enableDolbyVideo = vendorTagsObj.optBoolean("enableDolbyVideo", false),
                enableDolbyVideo60fps = vendorTagsObj.optBoolean("enableDolbyVideo60fps", false),
                enableDolbyVideoSat = vendorTagsObj.optBoolean("enableDolbyVideoSat", false),
                enableFrontDolbyVideo = vendorTagsObj.optBoolean("enableFrontDolbyVideo", false),
                enableVideoLockLens = vendorTagsObj.optBoolean("enableVideoLockLens", false),
                enableVideoLockWb = vendorTagsObj.optBoolean("enableVideoLockWb", false),
                enableMicStatusCheck = vendorTagsObj.optBoolean("enableMicStatusCheck", false),
                enableJiangWenFilter = vendorTagsObj.optBoolean("enableJiangWenFilter", false),
                enableHasselbladWatermarkGuide = vendorTagsObj.optBoolean("enableHasselbladWatermarkGuide", false),
                enableHasselbladWatermark = vendorTagsObj.optBoolean("enableHasselbladWatermark", false),
                enableHasselbladWatermarkDefault = vendorTagsObj.optBoolean("enableHasselbladWatermarkDefault", false),
                enableGlobalEv = vendorTagsObj.optBoolean("enableGlobalEv", false),
                enableOs15NewFilter = vendorTagsObj.optBoolean("enableOs15NewFilter", false),
                enableSwitchLensFocalLength = vendorTagsObj.optBoolean("enableSwitchLensFocalLength", false),
                enableMotionCapture = vendorTagsObj.optBoolean("enableMotionCapture", false),
                enable4K120fpsVideo = vendorTagsObj.optBoolean("enable4K120fpsVideo", false),
                enable1080p120fpsVideo = vendorTagsObj.optBoolean("enable1080p120fpsVideo", false),
                enableDolbyVideo120fps = vendorTagsObj.optBoolean("enableDolbyVideo120fps", false),
                enableMultiFrameBurstShot = vendorTagsObj.optBoolean("enableMultiFrameBurstShot", false),
                enableVideoSoundFocus = vendorTagsObj.optBoolean("enableVideoSoundFocus", false),
                enableFront4KVideo = vendorTagsObj.optBoolean("enableFront4KVideo", false),
                enableAiScenePreset = vendorTagsObj.optBoolean("enableAiScenePreset", false),
                enableISOExtension = vendorTagsObj.optBoolean("enableISOExtension", false),
                enableLivePhoto = vendorTagsObj.optBoolean("enableLivePhoto", false),
                enableMasterModeLivePhoto = vendorTagsObj.optBoolean("enableMasterModeLivePhoto", false),
                enableSoftLightFilter = vendorTagsObj.optBoolean("enableSoftLightFilter", false),
                enableFlashFilter = vendorTagsObj.optBoolean("enableFlashFilter", false),
                enableXPAN = vendorTagsObj.optBoolean("enableXPAN", false),
                enableGRFilter = vendorTagsObj.optBoolean("enableGRFilter", false),
                unlockFilterInMasterMode = vendorTagsObj.optBoolean("unlockFilterInMasterMode", false),
                enableGRWatermark = vendorTagsObj.optBoolean("enableGRWatermark", false),
                enableLUMO = vendorTagsObj.optBoolean("enableLUMO", false),
                enableHasselbladHighPixel = vendorTagsObj.optBoolean("enableHasselbladHighPixel", false)
            )
            
            AppConfig(
                vendorTags = vendorTags,
                otherSettings = com.tlsu.opluscamerapro.data.OtherSettings(),
                appSettings = com.tlsu.opluscamerapro.data.AppSettings()
            )
        } catch (e: Exception) {
            Log.e("$TAG: Error parsing config", e)
            AppConfig() // 返回默认配置
        }
    }
    
    /**
     * 在每个addPresetTag前先检查对应功能是否启用
     */
    fun addConfig() {
        try {
            // 每次添加配置前先重新加载，确保使用最新配置
            reloadConfig()
            
            val vendorTags = config?.vendorTags ?: return
            
            // 25MP Turbo RAW Resolution Enhance
            if (vendorTags.enable25MP) {
                XposedBridge.log("OPCameraPro: enable 25MP")
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.turboraw.re.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // Master Mode (Version 2)
            if (vendorTags.enableMasterMode) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.master.mode.version",
                        "Float",
                        "1",
                        "2.0"
                    ),
                    MergeStrategy.OVERRIDE
                )
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.feature.super_text_two",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.feature.super_text",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.master.video.type",
//                        "int32",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.feature.none.sat.rear.mode",
//                        "String",
//                        "1",
//                        "underWater"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.feature.under.water.mode.support",
//                        "String",
//                        "8",
//                        "1,120,187,259,1,0,-1,515"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
            }

            // 大师模式 RAW MAX 格式
            if (vendorTags.enableMasterRawMax) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.master.hq.raw.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 人像模式变焦
            if (vendorTags.enablePortraitZoom) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.rear.portrait.zoom.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                // addPresetTag(
                //     VendorTagInfo(
                //         "com.oplus.portrait.photo.ratio.support",
                //         "Byte",
                //         "1",
                //         "1"
                //     ),
                //     MergeStrategy.OVERRIDE
                // )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.save.portrait.zoom.value",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.portrait.preview.rear.sizes",
                        "Int32",
                        "8",
                        "1280x960x960x960x1664x936x2112x960"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.rear.sub.portrait.previewsize",
                        "Int32",
                        "8",
                        "320x240x240x240x416x234x528x240"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 720P 60FPS Video
            if (vendorTags.enable720p60fps) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.720p.60fps.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 慢动作视频超广角480FPS
            if (vendorTags.enableSlowVideo480fps) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.slowvideo.ultra.wide.480fps.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 新版微距模式
            if (vendorTags.enableNewMacroMode) {
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.feature.macro.closeup.max.zoom.value",
//                        "Float",
//                        "1",
//                        "30.0" // 提高变焦倍率至30x
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.tilt.shift.macro.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 微距模式调用长焦
            if (vendorTags.enableMacroTele) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.macro.closeup.none.sat.tele.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 微距景深融合
            if (vendorTags.enableMacroDepthFusion) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.macro.depth.of.field.fusion.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // HEIF模式下相册支持编辑背景虚化
            if (vendorTags.enableHeifBlurEdit) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.heif.blur.edit.in.gallery.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 后置人像模式预览大小


            // 大师模式-滤镜参数预设
            if (vendorTags.enableStyleEffect) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.effect.style.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 大师模式-Pro-放大对焦
            if (vendorTags.enableScaleFocus) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.feature.scale.focus",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 实况照片fov优化
            if (vendorTags.enableLivePhotoFovOptimize) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.support.fov.optimize",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 10bit照片
            if (vendorTags.enable10bitPhoto) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.10bits.heic.encode.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 实况HEIF照片
            if (vendorTags.enableHeifLivePhoto) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.heif.support.livephoto",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            } else {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.heif.support.livephoto",
                        "Byte",
                        "1",
                        "0"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 实况10bit照片
            if (vendorTags.enable10bitLivePhoto) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.livephoto.support.10bit",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            } else {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.livephoto.support.10bit",
                        "Byte",
                        "1",
                        "0"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 光影有声滤镜
            if (vendorTags.enableTolStyleFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.tol.style.filter.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // grand tour系列滤镜
            if (vendorTags.enableGrandTourFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.support.grand.tour.filter",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

//            // 沙漠系列滤镜
//            if (vendorTags.enableDesertFilter) {
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.desert.filter.type.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//            }

//            // vignette grain 滤镜
//            if (vendorTags.enableVignetteGrainFilter) {
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.vignette.grain.filter.type.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//            }

            // 姜文滤镜
            if (vendorTags.enableJiangWenFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.director.filter.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.director.filter.rus",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 贾樟柯滤镜
            if (vendorTags.enableJzkMovieFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.support.jzk.movie.filter",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.support.filter.watermark.list",
                        "String",
                        "1",
                        "jzk-movie.cube.rgb.bin"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 新版美颜菜单
            if (vendorTags.enableNewBeautyMenu) {
                addPresetTag(
                    VendorTagInfo(
                        "com.ocs.camera.ipu.face.beauty.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.facebeauty.version",
                        "Int32",
                        "1",
                        "7"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.face.beauty.custom.menu.version",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.custom.beauty.back.camera.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.face.beauty.custom.menu.refinement.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

//            // 超级文本扫描
//            if (vendorTags.enableSuperTextScanner) {
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.feature.super.text.scanner.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//            }

            // 照片模式柔光滤镜
            if (vendorTags.enableSoftLightPhotoMode) {
                addPresetTag(
                    VendorTagInfo(
                        "com.ocs.camera.ipu.soft.light.photo.mode.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 夜景模式柔光滤镜
            if (vendorTags.enableSoftLightNightMode) {
                addPresetTag(
                    VendorTagInfo(
                        "com.ocs.camera.ipu.soft.light.night.mode.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 大师模式柔光滤镜
            if (vendorTags.enableSoftLightProMode) {
                addPresetTag(
                    VendorTagInfo(
                        "com.ocs.camera.ipu.soft.light.professional.mode.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

//            // meishe 系列滤镜
//            if (vendorTags.enableMeisheFilter) {
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.ocs.camera.ipu.meishe.filter.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//            }

            // 视频自动帧率
            if (vendorTags.enableVideoAutoFps) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.auto.fps.setting.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 双击音量键快捷启动相机
            if (vendorTags.enableQuickLaunch) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.quick.launch.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 实况视频码率
            if (vendorTags.enableLivePhotoHighBitrate) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.video.bitrate",
                        "Int32",
                        "1",
                        vendorTags.livePhotoBitrate.toString()
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 实况照片时长
            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.livephoto.video.max.duration",
                    "Int32",
                    "1",
                    vendorTags.livePhotoMaxDuration.toString()
                ),
                MergeStrategy.OVERRIDE
            )
            
            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.livephoto.video.min.duration",
                    "Int32",
                    "1",
                    vendorTags.livePhotoMinDuration.toString()
                ),
                MergeStrategy.OVERRIDE
            )

            // 停止录制立即播放提示音
            if (vendorTags.enableVideoStopSoundImmediate) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.stop.record.sound.play.immediate",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

//            // 第三方app调用官方相机时可以选择人像模式
//            if (vendorTags.enableForcePortraitForThirdParty) {
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.force.portrait.when.parse.intent",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//            }

            // 前置拍照变焦
            if (vendorTags.enableFrontCameraZoom) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.front.camera.wide.zoom.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 人像模式后置闪光灯
            if (vendorTags.enablePortraitRearFlash) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.portrait.rear.flash.supp`ort",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // AI 超清望远算法相关功能
            if (vendorTags.enableAiHdSwitch) {
                // AI 超清望远算法介入倍率
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.ai.hd.zoom.value.default",
                        "Float",
                        "1",
                        vendorTags.aiHdZoomValue.toString()
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // AI 超清望远算法
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.ai.hd.switch.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 超清长焦算法相关功能
            if (vendorTags.enableTeleSdsr) {
                // 超清长焦算法介入倍率
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.ai.hd.gan.zoom.value",
                        "float",
                        "1",
                        vendorTags.teleSdsrZoomValue.toString()
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 超清长焦算法
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.tele.sdsr.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 杜比视频60fps
            if (vendorTags.enableDolbyVideo60fps) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.dv.60fps.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 长焦杜比视频
            if (vendorTags.enableDolbyVideoSat) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.dv.sat.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 前置杜比视频
            if (vendorTags.enableFrontDolbyVideo) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.front.dv.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 杜比视频支持
            if (vendorTags.enableDolbyVideo) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.dv.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 视频录制支持锁定镜头
            if (vendorTags.enableVideoLockLens) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.lock.lens.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 锁定镜头变焦范围配置
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.lock.lens.zoom.range",
                        "Float",
                        "8",
                        "0.6, 4, 1, 6, 3, 18, 6, 18"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 视频录制锁定白平衡
            if (vendorTags.enableVideoLockWb) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.lock.wb.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 视频录制检测麦克风状态
            if (vendorTags.enableMicStatusCheck) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.mic.status.check.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 哈苏水印相关功能
            if (vendorTags.enableHasselbladWatermark) {
                // 哈苏水印指导支持
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.hasselblad.watermark.guide.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 启用哈苏风格
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.use.hasselblad.style.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 自定义哈苏水印
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.support.custom.hasselblad.watermark",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 哈苏水印默认开启
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.support.custom.hasselblad.watermark.sellmode.default.open",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // EV调节功能
            if (vendorTags.enableGlobalEv) {
                // 照片EV调节
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.global.ev.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 视频EV调节
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.global.ev.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 人像EV调节
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.portrait.global.ev.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // ColorOS15新机滤镜
            if (vendorTags.enableOs15NewFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.os15.new.filter.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 点击变焦倍率切换焦距
            if (vendorTags.enableSwitchLensFocalLength) {
                // 启用点击变焦倍率切换焦距
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.switch.lens.focal.length.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 通用焦距定义
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.available.more.lens.focal.length",
                        "String",
                        "5",
                        "1.22(28),1.52(35),3.7(85),13.43(300),30(668)"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // SAT模式焦距定义
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.available.sat.more.lens.focal.length",
                        "String",
                        "5",
                        "1.22(28),1.52(35),3.7(85),13.43(300),30(668)"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 非SAT模式焦距定义
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.available.none.sat.more.lens.focal.length",
                        "String",
                        "5",
                        "1.22(28),1.52(35),3.7(85),13.43(300),30(668)"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 人像模式非SAT焦距定义
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.available.none.sat.portrait.lens.focal.length",
                        "String",
                        "2",
                        "1.52(35),3.7(85)"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 人像模式35mm焦距点
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.portrait.zoom.point.focal.length.35mm.value",
                        "Float",
                        "1",
                        "1.52"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 运动模式
            if (vendorTags.enableMotionCapture) {
                // 启用运动模式
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.motion.capture.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 启用运动模式SAT
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.motion.capture.sat.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
                
                // 运动模式最大变焦值
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.motion.capture.max.zoom.value",
                        "Float",
                        "1",
                        "30.0"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 4K 120FPS视频
            if (vendorTags.enable4K120fpsVideo) {
                // 启用4K 120FPS视频
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.4k.120fps.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 启用120FPS指导
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.120fps.guide.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 4K 120FPS视频变焦范围
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.4k.120fps.zoom.range",
                        "Float",
                        "2",
                        "1,10"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 4K 120FPS视频最大变焦列表
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.4k120fps.max.zoom.list",
                        "Float",
                        "3",
                        "1,2.9,10"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 1080P 120FPS视频
            if (vendorTags.enable1080p120fpsVideo) {
                // 启用1080P 120FPS视频
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.1080p.120fps.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 1080P 120FPS视频变焦范围
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.1080p.120fps.zoom.range",
                        "Float",
                        "2",
                        "1,10"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 1080P 120FPS视频最大变焦列表
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.1080p120fps.max.zoom.list",
                        "Float",
                        "3",
                        "1,2.9,10"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 杜比视界120FPS视频
            if (vendorTags.enableDolbyVideo120fps) {
                // 启用杜比视界120FPS视频
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.video.dv.120fps.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 无影抓拍
            if (vendorTags.enableMultiFrameBurstShot) {
                // 启用无影抓拍
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.support.multi.frame.burst.shot",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                // 启用无影抓拍集群
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.support.multi.frame.burst.shot.cluster",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 声音聚焦
            if (vendorTags.enableVideoSoundFocus) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.sound.focus.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // 前置4K视频
            if (vendorTags.enableFront4KVideo) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.front.video.4k.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }
            
            // AI场景预设
            if (vendorTags.enableAiScenePreset) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.ai.scene.preset.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preset.scene.detect.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // Live Photo
            if (vendorTags.enableLivePhoto) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.color.dataspace.value",
                        "String",
                        "3",
                        "1,2,2"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.gyro.threshould.vector",
                        "Float",
                        "2",
                        "2.6, 2.6"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.clear.video",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.video.bitrate",
                        "Int32",
                        "1",
                        vendorTags.livePhotoBitrate.toString()
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.video.max.duration",
                        "Int32",
                        "1",
                        vendorTags.livePhotoMaxDuration.toString()
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.video.min.duration",
                        "Int32",
                        "1",
                        vendorTags.livePhotoMinDuration.toString()
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            if (vendorTags.enableMasterModeLivePhoto) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.livephoto.mastermode.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                if (!isV16()) {
                    addPresetTag(
                        VendorTagInfo(
                            "com.oplus.camera.livephoto.enable.eis",
                            "Byte",
                            "1",
                            "0"
                        ),
                        MergeStrategy.OVERRIDE
                    )
                    addPresetTag(
                        VendorTagInfo(
                            "com.oplus.camera.livephoto.enable.frc",
                            "Byte",
                            "1",
                            "0"
                        ),
                        MergeStrategy.OVERRIDE
                    )
                }
            }

            if (vendorTags.enableSoftLightFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.soft.light.filter.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            if (vendorTags.enableFlashFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.flash.filter.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            if (isV16() && vendorTags.enableXPAN) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.xpan.mode.version",
                        "Int32",
                        "1",
                        "3"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            if (!isV16() && vendorTags.enableStyleEffect) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.master.mode.vignette.process.in.lsc.and.soft",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.master.mode.vignette.process.in.lsc",
                        "Byte",
                        "1",
                        "0"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            if (isV16() && vendorTags.enablePreviewHdr) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.ai.perception.detect.support",
                        "Byte",
                        "1",
                        "0"
                    ),
                    MergeStrategy.OVERRIDE
                )
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.brightness.ratio",
                        "Float",
                        "1",
                        "5"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.video.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.video.brightness.ratio",
                        "Float",
                        "1",
                        "5"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.capture.hdr.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.transform.support",
                        "Byte",
                        "1",
                        "0"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.merge.hdr.transform.support",
                        "Byte",
                        "1",
                        "0"
                    ),
                    MergeStrategy.OVERRIDE
                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.preview.hdr.transform.lut.video.support",
//                        "Byte",
//                        "1",
//                        "0"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.cap.mode.value",
                        "String",
                        "5",
                        "common,night,highPixel,sticker,idPhoto"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.capture.hdr.cap.mode.value",
                        "String",
                        "6",
                        "common,portrait,night,highPixel,sticker,idPhoto"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.livephoto.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )

            }

            if (isV1501() && vendorTags.enableLUMO) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.lumo.setting.guide.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.video.guide.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            if (DEBUG) {
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.available.gr.mode.zoomvalues",
//                        "String",
//                        "4",
//                        "1.26(28),1.575(35),2.0(40),2.50(50)"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.gr.mode.marked.zoomvalues",
//                        "String",
//                        "2",
//                        "1.26(28),2.0(40)"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.master.video.feature.list",
//                        "Int32",
//                        "5",
//                        "1,1,1,1,1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.master.video.type",
//                        "int32",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.feature.master.video.focus.peaking.histogram.oplus.r.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.feature.fine.food.mode.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
//
//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.gr.mode.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )

//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.mode.data.db.version",
//                        "Byte",
//                        "1",
//                        "5"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )

//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.video.bitrate.increase.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )

//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.video.livephoto.support",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )

//                if (isV16() && vendorTags.enableHasselbladHighPixel) {
//                    addPresetTag(
//                        VendorTagInfo(
//                            "com.oplus.camera.hasselblad.super.definition.support",
//                            "Byte",
//                            "1",
//                            "1"
//                        ),
//                        MergeStrategy.OVERRIDE
//                    )
                //}

//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.feature.professional.dng.encode.inAps",
//                        "Byte",
//                        "1",
//                        "1"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
            }


        } catch (e: Exception) {
            XposedBridge.log("OPCameraPro: Error in addConfig: ${e.message}")
        }
    }
} 