package com.tlsu.opluscamerapro.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.tlsu.opluscamerapro.utils.DeviceCheck
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

// 配置路径定义
@SuppressLint("SdCardPath")
private const val CONFIG_DIR = "/sdcard/Android/OplusCameraPro"
private const val CONFIG_FILE = "$CONFIG_DIR/config.json"
private const val TAG = "ConfigManager"

/**
 * 配置管理器，负责读写配置文件
 */
object ConfigManager {

    // 初始化Shell
    init {
        // 在对象初始化时设置Shell
        Shell.enableVerboseLogging = false
        Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER))
    }

    // 配置状态流
    private val _configState = MutableStateFlow(AppConfig())
    val configState: StateFlow<AppConfig> = _configState.asStateFlow()

    // 检查是否有ROOT权限
    suspend fun checkRootAccess(): Boolean = withContext(Dispatchers.IO) {
        Shell.getShell().isRoot
    }

    // 初始化配置
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        // 确保配置目录存在
        ensureConfigDirExists()
        
        // 加载配置
        loadConfig()
    }

    // 确保配置目录存在
    private suspend fun ensureConfigDirExists() = withContext(Dispatchers.IO) {
        if (Shell.getShell().isRoot) {
            // 使用su创建目录
            val result = Shell.cmd("mkdir -p $CONFIG_DIR").exec()
            if (result.isSuccess) {
                Shell.cmd("chmod 755 $CONFIG_DIR").exec()
            } else {
                Log.e(TAG, "Failed to create config directory: ${result.out}")
            }
        }
    }

    // 加载配置
    private suspend fun loadConfig() = withContext(Dispatchers.IO) {
        try {
            val configFile = File(CONFIG_FILE)
            
            // 检查文件是否存在
            val fileExists = if (Shell.getShell().isRoot) {
                val result = Shell.cmd("[ -f $CONFIG_FILE ] && echo \"true\" || echo \"false\"").exec()
                result.out.joinToString("").trim() == "true"
            } else {
                configFile.exists()
            }
            
            if (fileExists) {
                // 使用su读取文件
                val result = Shell.cmd("cat $CONFIG_FILE").exec()
                if (result.isSuccess) {
                    val jsonStr = result.out.joinToString("\n")
                    if (jsonStr.isNotBlank()) {
                        val config = parseConfig(jsonStr)
                        _configState.update { config }
                    } else {

                    }
                } else {

                }
            } else {
                // 创建默认配置并保存
                saveConfig(_configState.value)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading config", e)
        }
    }
    
    // 解析配置JSON
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
                enableGrandTourFilter = vendorTagsObj.optBoolean("enableGrandTourFilter", false),
                enableDesertFilter = vendorTagsObj.optBoolean("enableDesertFilter", false),
                enableVignetteGrainFilter = vendorTagsObj.optBoolean("enableVignetteGrainFilter", false),
                enableDirectorFilter = vendorTagsObj.optBoolean("enableDirectorFilter", false),
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
                enableVideoStopSoundImmediate = vendorTagsObj.optBoolean("enableVideoStopSoundImmediate", false),
                enableForcePortraitForThirdParty = vendorTagsObj.optBoolean("enableForcePortraitForThirdParty", false),
                enableFrontCameraZoom = vendorTagsObj.optBoolean("enableFrontCameraZoom", false),
                enablePortraitRearFlash = vendorTagsObj.optBoolean("enablePortraitRearFlash", false),
                enableAiHdSwitch = vendorTagsObj.optBoolean("enableAiHdSwitch", false),
                enableTeleSdsr = vendorTagsObj.optBoolean("enableTeleSdsr", false),
                enableDolbyVideo = vendorTagsObj.optBoolean("enableDolbyVideo", false),
                enableDolbyVideo60fps = vendorTagsObj.optBoolean("enableDolbyVideo60fps", false),
                enableDolbyVideoSat = vendorTagsObj.optBoolean("enableDolbyVideoSat", false),
                enableFrontDolbyVideo = vendorTagsObj.optBoolean("enableFrontDolbyVideo", false),
                enableVideoLockLens = vendorTagsObj.optBoolean("enableVideoLockLens", false),
                enableVideoLockWb = vendorTagsObj.optBoolean("enableVideoLockWb", false),
                enableMicStatusCheck = vendorTagsObj.optBoolean("enableMicStatusCheck", false),
                aiHdZoomValue = vendorTagsObj.optInt("aiHdZoomValue", 60),
                teleSdsrZoomValue = vendorTagsObj.optInt("teleSdsrZoomValue", 20)
            )
            
            // 解析其他设置
            val otherSettingsObj = json.optJSONObject("otherSettings") ?: JSONObject()
            val otherSettings = OtherSettings(
                // 这里可以添加其他设置项
            )
            
            // 解析应用设置
            val appSettingsObj = json.optJSONObject("appSettings") ?: JSONObject()
            val appSettings = AppSettings(
                darkMode = appSettingsObj.optBoolean("darkMode", false),
                followSystemDarkMode = appSettingsObj.optBoolean("followSystemDarkMode", false)
            )
            
            // 解析元数据
            val metadataObj = json.optJSONObject("metadata") ?: JSONObject()
            val metadata = ConfigMetadata(
                message = metadataObj.optString("message", ""),
                exportTime = metadataObj.optLong("exportTime", System.currentTimeMillis()),
                oplusRomVersion = metadataObj.optString("oplusRomVersion", ""),
                androidVersion = metadataObj.optString("androidVersion", ""),
                deviceModel = metadataObj.optString("deviceModel", ""),
                deviceMarketName = metadataObj.optString("deviceMarketName", "")
            )
            
            AppConfig(
                vendorTags = vendorTags,
                otherSettings = otherSettings,
                appSettings = appSettings,
                metadata = metadata
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing config", e)
            AppConfig() // 返回默认配置
        }
    }
    
    // 保存配置
    suspend fun saveConfig(config: AppConfig) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                // 保存VendorTag设置
                put("vendorTags", JSONObject().apply {
                    put("enable25MP", config.vendorTags.enable25MP)
                    put("enableMasterMode", config.vendorTags.enableMasterMode)
                    put("enableMasterRawMax", config.vendorTags.enableMasterRawMax)
                    put("enablePortraitZoom", config.vendorTags.enablePortraitZoom)
                    put("enable720p60fps", config.vendorTags.enable720p60fps)
                    put("enableSlowVideo480fps", config.vendorTags.enableSlowVideo480fps)
                    put("enableNewMacroMode", config.vendorTags.enableNewMacroMode)
                    put("enableMacroTele", config.vendorTags.enableMacroTele)
                    put("enableMacroDepthFusion", config.vendorTags.enableMacroDepthFusion)
                    put("enableHeifBlurEdit", config.vendorTags.enableHeifBlurEdit)
                    put("enableStyleEffect", config.vendorTags.enableStyleEffect)
                    put("enableScaleFocus", config.vendorTags.enableScaleFocus)
                    put("enableLivePhotoFovOptimize", config.vendorTags.enableLivePhotoFovOptimize)
                    put("enable10bitPhoto", config.vendorTags.enable10bitPhoto)
                    put("enableHeifLivePhoto", config.vendorTags.enableHeifLivePhoto)
                    put("enable10bitLivePhoto", config.vendorTags.enable10bitLivePhoto)
                    put("enableTolStyleFilter", config.vendorTags.enableTolStyleFilter)
                    put("enableGrandTourFilter", config.vendorTags.enableGrandTourFilter)
                    put("enableDesertFilter", config.vendorTags.enableDesertFilter)
                    put("enableVignetteGrainFilter", config.vendorTags.enableVignetteGrainFilter)
                    put("enableDirectorFilter", config.vendorTags.enableDirectorFilter)
                    put("enableJzkMovieFilter", config.vendorTags.enableJzkMovieFilter)
                    put("enableNewBeautyMenu", config.vendorTags.enableNewBeautyMenu)
                    put("enableSuperTextScanner", config.vendorTags.enableSuperTextScanner)
                    put("enableSoftLightPhotoMode", config.vendorTags.enableSoftLightPhotoMode)
                    put("enableSoftLightNightMode", config.vendorTags.enableSoftLightNightMode)
                    put("enableSoftLightProMode", config.vendorTags.enableSoftLightProMode)
                    put("enableMeisheFilter", config.vendorTags.enableMeisheFilter)
                    put("enablePreviewHdr", config.vendorTags.enablePreviewHdr)
                    put("enableVideoAutoFps", config.vendorTags.enableVideoAutoFps)
                    put("enableQuickLaunch", config.vendorTags.enableQuickLaunch)
                    put("enableLivePhotoHighBitrate", config.vendorTags.enableLivePhotoHighBitrate)
                    put("livePhotoBitrate", config.vendorTags.livePhotoBitrate)
                    put("enableVideoStopSoundImmediate", config.vendorTags.enableVideoStopSoundImmediate)
                    put("enableForcePortraitForThirdParty", config.vendorTags.enableForcePortraitForThirdParty)
                    put("enableFrontCameraZoom", config.vendorTags.enableFrontCameraZoom)
                    put("enablePortraitRearFlash", config.vendorTags.enablePortraitRearFlash)
                    put("enableAiHdSwitch", config.vendorTags.enableAiHdSwitch)
                    put("enableTeleSdsr", config.vendorTags.enableTeleSdsr)
                    put("enableDolbyVideo", config.vendorTags.enableDolbyVideo)
                    put("enableDolbyVideo60fps", config.vendorTags.enableDolbyVideo60fps)
                    put("enableDolbyVideoSat", config.vendorTags.enableDolbyVideoSat)
                    put("enableFrontDolbyVideo", config.vendorTags.enableFrontDolbyVideo)
                    put("enableVideoLockLens", config.vendorTags.enableVideoLockLens)
                    put("enableVideoLockWb", config.vendorTags.enableVideoLockWb)
                    put("enableMicStatusCheck", config.vendorTags.enableMicStatusCheck)
                    put("aiHdZoomValue", config.vendorTags.aiHdZoomValue)
                    put("teleSdsrZoomValue", config.vendorTags.teleSdsrZoomValue)
                })
                
                // 保存其他设置
                put("otherSettings", JSONObject())
                
                // 保存应用设置
                put("appSettings", JSONObject().apply {
                    put("darkMode", config.appSettings.darkMode)
                    put("followSystemDarkMode", config.appSettings.followSystemDarkMode)
                })
                
                // 保存元数据
                put("metadata", JSONObject().apply {
                    put("message", config.metadata.message)
                    put("exportTime", config.metadata.exportTime)
                    put("oplusRomVersion", config.metadata.oplusRomVersion)
                    put("androidVersion", config.metadata.androidVersion)
                    put("deviceModel", config.metadata.deviceModel)
                    put("deviceMarketName", config.metadata.deviceMarketName)
                })
            }
            
            val jsonStr = json.toString(4)
            
            if (Shell.getShell().isRoot) {
                // 确保目录存在
                ensureConfigDirExists()
                
                // 写入配置文件
                val tempFile = File.createTempFile("config", ".json")
                tempFile.writeText(jsonStr)
                
                val result = Shell.cmd(
                    "cat \"${tempFile.absolutePath}\" > $CONFIG_FILE",
                    "chmod 644 $CONFIG_FILE"
                ).exec()
                
                if (!result.isSuccess) {
                    Log.e(TAG, "Failed to save config: ${result.out}")
                }
                
                // 删除临时文件
                tempFile.delete()
            }
            
            // 更新状态
            _configState.update { config }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving config", e)
        }
    }
    
    // 更新设置
    suspend fun updateConfig(update: (AppConfig) -> AppConfig) {
        val newConfig = update(_configState.value)
        saveConfig(newConfig)
    }
    
    // 导出配置到外部存储，带留言和设备信息
    suspend fun exportConfig(targetPath: String, message: String = ""): Boolean = withContext(Dispatchers.IO) {
        try {
            if (Shell.getShell().isRoot) {
                // 更新配置中的留言和设备信息
                val configWithMetadata = _configState.value.copy(
                    metadata = _configState.value.metadata.copy(
                        message = message,
                        exportTime = System.currentTimeMillis(),
                        oplusRomVersion = DeviceCheck.getOplusRomVersion(),
                        androidVersion = DeviceCheck.getAndroidVersion(),
                        deviceModel = DeviceCheck.getDeviceModel(),
                        deviceMarketName = DeviceCheck.getDeviceMarketName()
                    )
                )
                
                // 保存带元数据的配置
                saveConfig(configWithMetadata)
                
                // 复制到目标路径
                val result = Shell.cmd("cp $CONFIG_FILE \"$targetPath\"").exec()
                result.isSuccess
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting config", e)
            false
        }
    }
    
    // 从外部存储导入配置
    suspend fun importConfig(sourcePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (Shell.getShell().isRoot) {
                // 确保目录存在
                ensureConfigDirExists()
                
                val result = Shell.cmd("cp \"$sourcePath\" $CONFIG_FILE").exec()
                if (result.isSuccess) {
                    // 重新加载配置
                    loadConfig()
                    true
                } else {
                    Log.e(TAG, "Failed to import config: ${result.out}")
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error importing config", e)
            false
        }
    }
} 