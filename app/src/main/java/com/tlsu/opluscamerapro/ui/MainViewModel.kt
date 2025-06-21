package com.tlsu.opluscamerapro.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlsu.opluscamerapro.data.AppConfig
import com.tlsu.opluscamerapro.data.ConfigManager
import com.tlsu.opluscamerapro.utils.DeviceCheck.execWithResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel : ViewModel() {
    
    // 应用状态
    var hasRootAccess by mutableStateOf(false)
        private set
    
    var isLoading by mutableStateOf(true)
        private set
    
    // 模块更新状态
    var needModuleUpdate by mutableStateOf(false)
        private set

    var moduleUpdateState by mutableStateOf(ModuleUpdateState.NONE)
        private set

    var moduleUpdateProgress by mutableStateOf(0)
        private set
    
    // 配置状态
    val config: StateFlow<AppConfig> = ConfigManager.configState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppConfig()
        )
    
    // 模块更新状态枚举
    enum class ModuleUpdateState {
        NONE,       // 无更新状态
        CHECKING,   // 检查中
        INSTALLING, // 安装中
        SUCCESS,    // 安装成功
        FAILED,     // 安装失败
        IGNORE      // 跳过安装
    }
    
    // 初始化ViewModel
    fun initialize(context: Context) {
        viewModelScope.launch {
            isLoading = true
            
            // 检查root权限
            hasRootAccess = ConfigManager.checkRootAccess()
            
            // 初始化配置
            ConfigManager.initialize(context)
            
            isLoading = false

        }
    }
    
    // 检查模块是否需要更新，如需更新则直接安装
    private fun checkModuleUpdateAndInstall(context: Context) {

    }
    
    // 安装模块更新

    // 重启设备
    fun rebootDevice() {
        viewModelScope.launch {
            try {
                execWithResult("reboot")
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to reboot: ${e.message}")
            }
        }
    }
    
    // 重置模块更新状态
    fun resetModuleUpdateState() {
        moduleUpdateState = ModuleUpdateState.NONE
        moduleUpdateProgress = 0
    }
    
    // 更新VendorTag设置
    fun updateVendorTagSetting(update: (AppConfig) -> AppConfig) {
        viewModelScope.launch {
            ConfigManager.updateConfig(update)
        }
    }
    
    // 切换深色模式
    fun toggleDarkMode() {
        viewModelScope.launch {
            ConfigManager.updateConfig { currentConfig ->
                // 如果启用了跟随系统，则禁用它
                if (currentConfig.appSettings.followSystemDarkMode) {
                    currentConfig.copy(
                        appSettings = currentConfig.appSettings.copy(
                            darkMode = !currentConfig.appSettings.darkMode,
                            followSystemDarkMode = false
                        )
                    )
                } else {
                    currentConfig.copy(
                        appSettings = currentConfig.appSettings.copy(
                            darkMode = !currentConfig.appSettings.darkMode
                        )
                    )
                }
            }
        }
    }
    
    // 切换跟随系统深色模式
    fun toggleFollowSystemDarkMode() {
        viewModelScope.launch {
            ConfigManager.updateConfig { currentConfig ->
                currentConfig.copy(
                    appSettings = currentConfig.appSettings.copy(
                        followSystemDarkMode = !currentConfig.appSettings.followSystemDarkMode
                    )
                )
            }
        }
    }
    
    // 导出配置
    suspend fun exportConfig(targetPath: String, message: String = ""): Boolean {
        return ConfigManager.exportConfig(targetPath, message)
    }
    
    // 导入配置
    suspend fun importConfig(sourcePath: String): Boolean {
        return ConfigManager.importConfig(sourcePath)
    }
    
    // 重启相机应用
    fun restartCameraApp(): Boolean {
        return try {
            execWithResult("killall com.oplus.camera")
            execWithResult("killall com.coloros.gallery3d")
            true
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Failed to restart camera app: ${e.message}")
            false
        }
    }

} 