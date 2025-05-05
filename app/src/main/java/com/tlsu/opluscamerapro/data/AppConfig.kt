package com.tlsu.opluscamerapro.data

/**
 * 应用配置数据模型
 */
data class AppConfig(
    val vendorTags: VendorTagSettings = VendorTagSettings(),
    val otherSettings: OtherSettings = OtherSettings(),
    val appSettings: AppSettings = AppSettings(),
    val metadata: ConfigMetadata = ConfigMetadata()
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
    val enableHasselbladWatermarkDefault: Boolean = false
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