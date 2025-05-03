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
    val enable25MP: Boolean = true,
    
    // R.string.vendor_tag_enable_master_mode
    val enableMasterMode: Boolean = true,
    
    // R.string.vendor_tag_enable_master_raw_max
    val enableMasterRawMax: Boolean = true,
    
    // R.string.vendor_tag_enable_portrait_zoom
    val enablePortraitZoom: Boolean = true,
    
    // R.string.vendor_tag_enable_720p_60fps
    val enable720p60fps: Boolean = true,
    
    // R.string.vendor_tag_enable_slow_video_480fps
    val enableSlowVideo480fps: Boolean = true,
    
    // R.string.vendor_tag_enable_new_macro_mode
    val enableNewMacroMode: Boolean = true,
    
    // R.string.vendor_tag_enable_macro_tele
    val enableMacroTele: Boolean = true,
    
    // R.string.vendor_tag_enable_macro_depth_fusion
    val enableMacroDepthFusion: Boolean = true,
    
    // R.string.vendor_tag_enable_heif_blur_edit
    val enableHeifBlurEdit: Boolean = true,
    
    // R.string.vendor_tag_enable_style_effect
    val enableStyleEffect: Boolean = true,
    
    // R.string.vendor_tag_enable_scale_focus
    val enableScaleFocus: Boolean = true,
    
    // R.string.vendor_tag_enable_live_photo_fov_optimize
    val enableLivePhotoFovOptimize: Boolean = true,
    
    // R.string.vendor_tag_enable_10bit_photo
    val enable10bitPhoto: Boolean = true,
    
    // R.string.vendor_tag_enable_heif_live_photo
    val enableHeifLivePhoto: Boolean = false,
    
    // R.string.vendor_tag_enable_10bit_live_photo
    val enable10bitLivePhoto: Boolean = false,
    
    // R.string.vendor_tag_enable_tol_style_filter
    val enableTolStyleFilter: Boolean = true,
    
    // R.string.vendor_tag_enable_grand_tour_filter
    val enableGrandTourFilter: Boolean = true,
    
    // R.string.vendor_tag_enable_desert_filter
    val enableDesertFilter: Boolean = true,
    
    // R.string.vendor_tag_enable_vignette_grain_filter
    val enableVignetteGrainFilter: Boolean = true,
    
    // R.string.vendor_tag_enable_director_filter
    val enableDirectorFilter: Boolean = true,
    
    // R.string.vendor_tag_enable_jzk_movie_filter
    val enableJzkMovieFilter: Boolean = true,
    
    // R.string.vendor_tag_enable_new_beauty_menu
    val enableNewBeautyMenu: Boolean = true,
    
    // R.string.vendor_tag_enable_super_text_scanner
    val enableSuperTextScanner: Boolean = true,
    
    // R.string.vendor_tag_enable_soft_light_photo_mode
    val enableSoftLightPhotoMode: Boolean = true,
    
    // R.string.vendor_tag_enable_soft_light_night_mode
    val enableSoftLightNightMode: Boolean = true,
    
    // R.string.vendor_tag_enable_soft_light_pro_mode
    val enableSoftLightProMode: Boolean = true,
    
    // R.string.vendor_tag_enable_meishe_filter
    val enableMeisheFilter: Boolean = true,
    
    // R.string.vendor_tag_enable_preview_hdr
    val enablePreviewHdr: Boolean = true,
    
    // R.string.vendor_tag_enable_video_auto_fps
    val enableVideoAutoFps: Boolean = true,
    
    // R.string.vendor_tag_enable_quick_launch
    val enableQuickLaunch: Boolean = true,
    
    // R.string.vendor_tag_enable_live_photo_high_bitrate
    val enableLivePhotoHighBitrate: Boolean = true,
    
    // 实况视频自定义码率值
    val livePhotoBitrate: Int = 45,
    
    // R.string.vendor_tag_enable_video_stop_sound_immediate
    val enableVideoStopSoundImmediate: Boolean = true,
    
    // R.string.vendor_tag_enable_force_portrait_for_third_party
    val enableForcePortraitForThirdParty: Boolean = true,
    
    // R.string.vendor_tag_enable_front_camera_zoom
    val enableFrontCameraZoom: Boolean = true
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