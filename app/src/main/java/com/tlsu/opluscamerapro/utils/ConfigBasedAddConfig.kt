package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import com.github.kyuubiran.ezxhelper.Log
import com.tlsu.opluscamerapro.data.AppConfig
import com.tlsu.opluscamerapro.data.VendorTagSettings
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
import com.tlsu.opluscamerapro.utils.ParseConfig.addPresetTag
import com.topjohnwu.superuser.Shell
import de.robv.android.xposed.XposedBridge
import org.json.JSONObject
import java.io.File

/**
 * 基于配置的AddConfig类
 * 读取用户配置文件，根据配置决定是否添加特定的VendorTag
 */
object ConfigBasedAddConfig {
    private const val TAG = "ConfigBasedAddConfig"
    @SuppressLint("SdCardPath")
    private const val CONFIG_FILE = "/sdcard/Android/OplusCameraPro/config.json"
    
    // 存储配置信息
    private var config: AppConfig? = null
    
    // 初始化标志，避免重复初始化Shell
    private var isInitialized = false
    
    init {
        try {
            if (!isInitialized) {
                // 只在第一次初始化时设置Shell
                try {
                    Shell.enableVerboseLogging = false
                    Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER))
                    XposedBridge.log("ConfigBasedAddConfig: Shell initialized successfully")
                } catch (e: Exception) {
                    // 如果Shell已经初始化，会抛出异常，我们可以忽略这个错误
                    XposedBridge.log("ConfigBasedAddConfig: Shell already initialized: ${e.message}")
                }
                isInitialized = true
            }
            
            // 尝试读取配置文件
            loadConfig()
        } catch (e: Exception) {
            XposedBridge.log("ConfigBasedAddConfig init error: ${e.message}")
        }
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
                                XposedBridge.log("OplusCameraPro: Config loaded successfully")
                            } catch (e: Exception) {
                                XposedBridge.log("OplusCameraPro: Failed to parse config: ${e.message}")
                                config = AppConfig() // 解析错误时使用默认配置
                            }
                        } else {
                            XposedBridge.log("OplusCameraPro: Empty config file, using defaults")
                            config = AppConfig()
                        }
                    } else {
                        XposedBridge.log("OplusCameraPro: Failed to read config file: ${result.out}")
                        config = AppConfig()
                    }

            } else {
                XposedBridge.log("OplusCameraPro: Config file not found, using defaults")
                config = AppConfig() // 文件不存在时使用默认配置
            }
        } catch (e: Exception) {
            XposedBridge.log("OplusCameraPro: Error in loadConfig: ${e.message}")
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
        XposedBridge.log("OplusCameraPro: Config reloaded successfully")
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
                enable25MP = vendorTagsObj.optBoolean("enable25MP", true),
                enableMasterMode = vendorTagsObj.optBoolean("enableMasterMode", true),
                enableMasterRawMax = vendorTagsObj.optBoolean("enableMasterRawMax", true),
                enablePortraitZoom = vendorTagsObj.optBoolean("enablePortraitZoom", true),
                enable720p60fps = vendorTagsObj.optBoolean("enable720p60fps", true),
                enableSlowVideo480fps = vendorTagsObj.optBoolean("enableSlowVideo480fps", true),
                enableNewMacroMode = vendorTagsObj.optBoolean("enableNewMacroMode", true),
                enableMacroTele = vendorTagsObj.optBoolean("enableMacroTele", true),
                enableMacroDepthFusion = vendorTagsObj.optBoolean("enableMacroDepthFusion", true),
                enableHeifBlurEdit = vendorTagsObj.optBoolean("enableHeifBlurEdit", true),
                enableStyleEffect = vendorTagsObj.optBoolean("enableStyleEffect", true),
                enableScaleFocus = vendorTagsObj.optBoolean("enableScaleFocus", true),
                enableLivePhotoFovOptimize = vendorTagsObj.optBoolean("enableLivePhotoFovOptimize", true),
                enable10bitPhoto = vendorTagsObj.optBoolean("enable10bitPhoto", true),
                enableHeifLivePhoto = vendorTagsObj.optBoolean("enableHeifLivePhoto", false),
                enable10bitLivePhoto = vendorTagsObj.optBoolean("enable10bitLivePhoto", false),
                enableTolStyleFilter = vendorTagsObj.optBoolean("enableTolStyleFilter", true),
                enableGrandTourFilter = vendorTagsObj.optBoolean("enableGrandTourFilter", true),
                enableDesertFilter = vendorTagsObj.optBoolean("enableDesertFilter", true),
                enableVignetteGrainFilter = vendorTagsObj.optBoolean("enableVignetteGrainFilter", true),
                enableDirectorFilter = vendorTagsObj.optBoolean("enableDirectorFilter", true),
                enableJzkMovieFilter = vendorTagsObj.optBoolean("enableJzkMovieFilter", true),
                enableNewBeautyMenu = vendorTagsObj.optBoolean("enableNewBeautyMenu", true),
                enableSuperTextScanner = vendorTagsObj.optBoolean("enableSuperTextScanner", true),
                enableSoftLightPhotoMode = vendorTagsObj.optBoolean("enableSoftLightPhotoMode", true),
                enableSoftLightNightMode = vendorTagsObj.optBoolean("enableSoftLightNightMode", true),
                enableSoftLightProMode = vendorTagsObj.optBoolean("enableSoftLightProMode", true),
                enableMeisheFilter = vendorTagsObj.optBoolean("enableMeisheFilter", true),
                enablePreviewHdr = vendorTagsObj.optBoolean("enablePreviewHdr", true),
                enableVideoAutoFps = vendorTagsObj.optBoolean("enableVideoAutoFps", true),
                enableQuickLaunch = vendorTagsObj.optBoolean("enableQuickLaunch", true),
                enableLivePhotoHighBitrate = vendorTagsObj.optBoolean("enableLivePhotoHighBitrate", true),
                livePhotoBitrate = vendorTagsObj.optInt("livePhotoBitrate", 45),
                enableVideoStopSoundImmediate = vendorTagsObj.optBoolean("enableVideoStopSoundImmediate", true),
                enableForcePortraitForThirdParty = vendorTagsObj.optBoolean("enableForcePortraitForThirdParty", true),
                enableFrontCameraZoom = vendorTagsObj.optBoolean("enableFrontCameraZoom", true),
                enablePortraitRearFlash = vendorTagsObj.optBoolean("enablePortraitRearFlash", true),
                enableAiHdSwitch = vendorTagsObj.optBoolean("enableAiHdSwitch", true),
                aiHdZoomValue = vendorTagsObj.optInt("aiHdZoomValue", 60),
                enableTeleSdsr = vendorTagsObj.optBoolean("enableTeleSdsr", true),
                teleSdsrZoomValue = vendorTagsObj.optInt("teleSdsrZoomValue", 20),
                enableDolbyVideo = vendorTagsObj.optBoolean("enableDolbyVideo", true),
                enableDolbyVideo60fps = vendorTagsObj.optBoolean("enableDolbyVideo60fps", true),
                enableDolbyVideoSat = vendorTagsObj.optBoolean("enableDolbyVideoSat", true),
                enableFrontDolbyVideo = vendorTagsObj.optBoolean("enableFrontDolbyVideo", true),
                enableVideoLockLens = vendorTagsObj.optBoolean("enableVideoLockLens", true),
                enableVideoLockWb = vendorTagsObj.optBoolean("enableVideoLockWb", true),
                enableMicStatusCheck = vendorTagsObj.optBoolean("enableMicStatusCheck", true)
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
                XposedBridge.log("OplusCameraPro: enable 25MP")
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
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.macro.closeup.max.zoom.value",
                        "Float",
                        "1",
                        "30.0" // 提高变焦倍率至30x
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

            // 沙漠系列滤镜
            if (vendorTags.enableDesertFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.desert.filter.type.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // vignette grain 滤镜
            if (vendorTags.enableVignetteGrainFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.vignette.grain.filter.type.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // director 滤镜
            if (vendorTags.enableDirectorFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.director.filter.upgrade.support",
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
                        "com.oplus.feature.face.beauty.custom.menu.version",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // 超级文本扫描
            if (vendorTags.enableSuperTextScanner) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.feature.super.text.scanner.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

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

            // meishe 系列滤镜
            if (vendorTags.enableMeisheFilter) {
                addPresetTag(
                    VendorTagInfo(
                        "com.ocs.camera.ipu.meishe.filter.support",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

            // Preview HDR
            // Require OplusRom Version >= V15.0.1
            if (isV1501() && vendorTags.enablePreviewHdr) {
                XposedBridge.log("OplusTest: V15.0.1 Device, enable Preview HDR")
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

//                addPresetTag(
//                    VendorTagInfo(
//                        "com.oplus.camera.preview.hdr.transform.support",
//                        "Byte",
//                        "1",
//                        "0"
//                    ),
//                    MergeStrategy.OVERRIDE
//                )
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
                        "6",
                        "common,night,highPixel,xpan,sticker,idPhoto"
                    ),
                    MergeStrategy.OVERRIDE
                )

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.capture.hdr.cap.mode.value",
                        "String",
                        "7",
                        "common,portrait,night,highPixel,xpan,sticker,idPhoto"
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

                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.camera.preview.hdr.front.portrait.support",
                        "Byte",
                        "1",
                        "0"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

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

            // 第三方app调用官方相机时可以选择人像模式
            if (vendorTags.enableForcePortraitForThirdParty) {
                addPresetTag(
                    VendorTagInfo(
                        "com.oplus.force.portrait.when.parse.intent",
                        "Byte",
                        "1",
                        "1"
                    ),
                    MergeStrategy.OVERRIDE
                )
            }

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
                        "com.oplus.portrait.rear.flash.support",
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
        } catch (e: Exception) {
            XposedBridge.log("OplusCameraPro: Error in addConfig: ${e.message}")
        }
    }
} 