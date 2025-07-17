package com.tlsu.opluscamerapro.data

/**
 * 应用配置数据模型
 */
data class AppConfig(
    val vendorTags: VendorTagSettings = VendorTagSettings(),
    val otherSettings: OtherSettings = OtherSettings(),
    val appSettings: AppSettings = AppSettings(),
    val metadata: ConfigMetadata = ConfigMetadata(),
    val gallerySettings: GallerySettings = GallerySettings()
)

/**
 * 配置元数据
 */
data class ConfigMetadata(
    val message: String = "", // R.string.config_metadata_message
    val exportTime: Long = System.currentTimeMillis(), // R.string.config_metadata_export_time
    val oplusRomVersion: String = "", // R.string.config_metadata_oplus_rom_version
    val androidVersion: String = "", // R.string.config_metadata_android_version
    val deviceModel: String = "", // R.string.config_metadata_device_model
    val deviceMarketName: String = "" // R.string.config_metadata_device_market_name
)

/**
 * VendorTag设置
 * 基于AddConfig类中的选项
 */
data class VendorTagSettings(
    // R.string.vendor_tag_enable_25mp
    val enable25MP: Boolean = false,
    
    // R.string.vendor_tag_enable_master_mode
    val enableMasterMode: Boolean = false,
    
    // R.string.vendor_tag_enable_master_raw_max
    val enableMasterRawMax: Boolean = false,
    
    // R.string.vendor_tag_enable_portrait_zoom
    val enablePortraitZoom: Boolean = false,
    
    // R.string.vendor_tag_enable_720p_60fps
    val enable720p60fps: Boolean = false,
    
    // R.string.vendor_tag_enable_slow_video_480fps
    val enableSlowVideo480fps: Boolean = false,
    
    // R.string.vendor_tag_enable_new_macro_mode
    val enableNewMacroMode: Boolean = false,
    
    // R.string.vendor_tag_enable_macro_tele
    val enableMacroTele: Boolean = false,
    
    // R.string.vendor_tag_enable_macro_depth_fusion
    val enableMacroDepthFusion: Boolean = false,
    
    // R.string.vendor_tag_enable_heif_blur_edit
    val enableHeifBlurEdit: Boolean = false,
    
    // R.string.vendor_tag_enable_style_effect
    val enableStyleEffect: Boolean = false,
    
    // R.string.vendor_tag_enable_scale_focus
    val enableScaleFocus: Boolean = false,
    
    // R.string.vendor_tag_enable_live_photo_fov_optimize
    val enableLivePhotoFovOptimize: Boolean = false,
    
    // R.string.vendor_tag_enable_10bit_photo
    val enable10bitPhoto: Boolean = false,
    
    // R.string.vendor_tag_enable_heif_live_photo
    val enableHeifLivePhoto: Boolean = false,
    
    // R.string.vendor_tag_enable_10bit_live_photo
    val enable10bitLivePhoto: Boolean = false,
    
    // R.string.vendor_tag_enable_tol_style_filter
    val enableTolStyleFilter: Boolean = false,

    // Master Filter
    val enableMasterFilter: Boolean = false,

    // R.string.vendor_tag_enable_grand_tour_filter
    val enableGrandTourFilter: Boolean = false,
    
    // R.string.vendor_tag_enable_desert_filter
    val enableDesertFilter: Boolean = false,
    
    // R.string.vendor_tag_enable_vignette_grain_filter
    val enableVignetteGrainFilter: Boolean = false,
    
    // R.string.vendor_tag_enable_jzk_movie_filter
    val enableJzkMovieFilter: Boolean = false,
    
    // R.string.vendor_tag_enable_new_beauty_menu
    val enableNewBeautyMenu: Boolean = false,
    
    // R.string.vendor_tag_enable_super_text_scanner
    val enableSuperTextScanner: Boolean = false,
    
    // R.string.vendor_tag_enable_soft_light_photo_mode
    val enableSoftLightPhotoMode: Boolean = false,
    
    // R.string.vendor_tag_enable_soft_light_night_mode
    val enableSoftLightNightMode: Boolean = false,
    
    // R.string.vendor_tag_enable_soft_light_pro_mode
    val enableSoftLightProMode: Boolean = false,
    
    // R.string.vendor_tag_enable_meishe_filter
    val enableMeisheFilter: Boolean = false,
    
    // R.string.vendor_tag_enable_preview_hdr
    val enablePreviewHdr: Boolean = false,
    
    // R.string.vendor_tag_enable_video_auto_fps
    val enableVideoAutoFps: Boolean = false,
    
    // R.string.vendor_tag_enable_quick_launch
    val enableQuickLaunch: Boolean = false,
    
    // R.string.vendor_tag_enable_live_photo_high_bitrate
    val enableLivePhotoHighBitrate: Boolean = false,
    
    // 实况视频自定义码率值
    val livePhotoBitrate: Int = 45,
    
    // 实况照片最大时长（毫秒）
    val livePhotoMaxDuration: Int = 3200,
    
    // 实况照片最小时长（毫秒）
    val livePhotoMinDuration: Int = 500,
    
    // R.string.vendor_tag_enable_video_stop_sound_immediate
    val enableVideoStopSoundImmediate: Boolean = false,
    
    // R.string.vendor_tag_enable_force_portrait_for_third_party
    val enableForcePortraitForThirdParty: Boolean = false,
    
    // R.string.vendor_tag_enable_front_camera_zoom
    val enableFrontCameraZoom: Boolean = false,
    
    // 人像模式后置闪光灯
    val enablePortraitRearFlash: Boolean = false,
    
    // AI 超清望远算法
    val enableAiHdSwitch: Boolean = false,
    
    // 超清长焦算法
    val enableTeleSdsr: Boolean = false,
    
    // 杜比视频支持
    val enableDolbyVideo: Boolean = false,
    
    // 杜比视频60fps
    val enableDolbyVideo60fps: Boolean = false,
    
    // 长焦杜比视频
    val enableDolbyVideoSat: Boolean = false,
    
    // 前置杜比视频
    val enableFrontDolbyVideo: Boolean = false,
    
    // 视频录制支持锁定镜头
    val enableVideoLockLens: Boolean = false,
    
    // 视频录制锁定白平衡
    val enableVideoLockWb: Boolean = false,
    
    // 视频录制检测麦克风状态
    val enableMicStatusCheck: Boolean = false,
    
    // 姜文滤镜
    val enableJiangWenFilter: Boolean = false,
    
    // AI超清望远算法介入倍率值
    val aiHdZoomValue: Int = 60,
    
    // 超清长焦算法介入倍率值
    val teleSdsrZoomValue: Int = 20,
    
    // 哈苏水印指导支持
    val enableHasselbladWatermarkGuide: Boolean = false,
    
    // 自定义哈苏水印支持
    val enableHasselbladWatermark: Boolean = false,
    
    // 哈苏水印默认开启
    val enableHasselbladWatermarkDefault: Boolean = false,
    
    // EV调节功能
    val enableGlobalEv: Boolean = false,
    
    // ColorOS15新机滤镜
    val enableOs15NewFilter: Boolean = false,
    
    // 点击变焦倍率切换焦距
    val enableSwitchLensFocalLength: Boolean = false,
    
    // 运动模式
    val enableMotionCapture: Boolean = false,
    
    // 4K 120FPS视频
    val enable4K120fpsVideo: Boolean = false,
    
    // 1080P 120FPS视频
    val enable1080p120fpsVideo: Boolean = false,
    
    // 杜比视界120FPS视频
    val enableDolbyVideo120fps: Boolean = false,
    
    // 无影抓拍
    val enableMultiFrameBurstShot: Boolean = false,
    
    // 声音聚焦
    val enableVideoSoundFocus: Boolean = false,
    
    // 前置4K视频
    val enableFront4KVideo: Boolean = false,
    
    // AI场景预设
    val enableAiScenePreset: Boolean = false,

    // ISO 12800
    val enableISOExtension: Boolean = false,

    // Live Photo
    val enableLivePhoto: Boolean = false
)

/**
 * 其他设置
 */
data class OtherSettings(
    // 这里可以添加其他设置项
    val test: Boolean = false
)

/**
 * 应用设置
 */
data class AppSettings(
    val darkMode: Boolean = false,
    val followSystemDarkMode: Boolean = false  // 深色模式跟随系统
)

/**
 * 相册设置
 */
data class GallerySettings(
    // AI 灵感成片
    val enableAIComposition: Boolean = false,
    // AI 消除
    val enableAIEliminate: Boolean = false,
    // AI 去拖影
    val enableAIDeblur: Boolean = false,
    // AI 画质增强
    val enableAIQualityEnhance: Boolean = false,
    // AI 去反光
    val enableAIDeReflection: Boolean = false,
    // AI 最佳表情
    val enableAIBestTake: Boolean = false,

    // 实况封面 ProXDR
    val enableOliveCoverProXDR: Boolean = false,

    // Lumo 水印
    val enableLumoWatermark: Boolean = false
)

/**
 * 默认值配置
 * 保存从设备原始配置中提取的默认值信息
 */
data class DefaultValueConfig(
    val vendorTags: Map<String, DefaultTagInfo> = mapOf()
)

/**
 * 默认标签信息
 */
data class DefaultTagInfo(
    val vendorTag: String,    // VendorTag名称
    val type: String,         // 类型
    val value: String,        // 值
    val isEnabled: Boolean    // 是否启用
) 