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
    val message: String = "", // 配置文件留言
    val exportTime: Long = System.currentTimeMillis(), // 导出时间
    val oplusRomVersion: String = "", // ColorOS/OriginOS版本
    val androidVersion: String = "", // Android版本
    val deviceModel: String = "", // 设备型号
    val deviceMarketName: String = "" // 设备营销名称
)

/**
 * VendorTag设置
 * 基于AddConfig类中的选项
 */
data class VendorTagSettings(
    // 25MP Turbo RAW Resolution Enhance
    val enable25MP: Boolean = true,
    
    // Master Mode (Version 2)
    val enableMasterMode: Boolean = true,
    
    // 大师模式 RAW MAX 格式
    val enableMasterRawMax: Boolean = true,
    
    // 人像模式变焦
    val enablePortraitZoom: Boolean = true,
    
    // 720P 60FPS Video
    val enable720p60fps: Boolean = true,
    
    // 慢动作视频超广角480FPS
    val enableSlowVideo480fps: Boolean = true,
    
    // 新版微距模式
    val enableNewMacroMode: Boolean = true,
    
    // 微距模式调用长焦
    val enableMacroTele: Boolean = true,
    
    // 微距景深融合
    val enableMacroDepthFusion: Boolean = true,
    
    // HEIF模式下相册支持编辑背景虚化
    val enableHeifBlurEdit: Boolean = true,
    
    // 大师模式-滤镜参数预设
    val enableStyleEffect: Boolean = true,
    
    // 大师模式-Pro-放大对焦
    val enableScaleFocus: Boolean = true,
    
    // 实况照片fov优化
    val enableLivePhotoFovOptimize: Boolean = true,
    
    // 10bit照片
    val enable10bitPhoto: Boolean = true,
    
    // 实况HEIF照片
    val enableHeifLivePhoto: Boolean = false,
    
    // 实况10bit照片
    val enable10bitLivePhoto: Boolean = false,
    
    // 光影有声滤镜
    val enableTolStyleFilter: Boolean = true,
    
    // grand tour系列滤镜
    val enableGrandTourFilter: Boolean = true,
    
    // 沙漠系列滤镜
    val enableDesertFilter: Boolean = true,
    
    // vignette grain 滤镜
    val enableVignetteGrainFilter: Boolean = true,
    
    // director 滤镜
    val enableDirectorFilter: Boolean = true,
    
    // 贾樟柯滤镜
    val enableJzkMovieFilter: Boolean = true,
    
    // 新版美颜菜单
    val enableNewBeautyMenu: Boolean = true,
    
    // 超级文本扫描
    val enableSuperTextScanner: Boolean = true,
    
    // 照片模式柔光滤镜
    val enableSoftLightPhotoMode: Boolean = true,
    
    // 夜景模式柔光滤镜
    val enableSoftLightNightMode: Boolean = true,
    
    // 大师模式柔光滤镜
    val enableSoftLightProMode: Boolean = true,
    
    // meishe 系列滤镜
    val enableMeisheFilter: Boolean = true,
    
    // Preview HDR
    val enablePreviewHdr: Boolean = true,
    
    // 视频自动帧率
    val enableVideoAutoFps: Boolean = true,
    
    // 双击音量键快捷启动相机
    val enableQuickLaunch: Boolean = true,
    
    // 实况视频码率
    val enableLivePhotoHighBitrate: Boolean = true,
    
    // 停止录制立即播放提示音
    val enableVideoStopSoundImmediate: Boolean = true,
    
    // 第三方app调用官方相机时可以选择人像模式
    val enableForcePortraitForThirdParty: Boolean = true,
    
    // 前置拍照变焦
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