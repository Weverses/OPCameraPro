package com.tlsu.opluscamerapro.ui.screens.module

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.data.AppConfig
import com.tlsu.opluscamerapro.ui.MainViewModel
import com.tlsu.opluscamerapro.ui.components.NoRootAccessDialog
import com.tlsu.opluscamerapro.ui.components.SettingsClickableItem
import com.tlsu.opluscamerapro.ui.components.SettingsSwitchItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 模块设置屏幕
 */
@Composable
fun ModuleSettingsScreen(
    viewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val isLoading = viewModel.isLoading
    val hasRootAccess = viewModel.hasRootAccess
    val config by viewModel.config.collectAsState()
    val context = LocalContext.current
    
    // 对话框状态
    var showAboutDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showExportMessageDialog by remember { mutableStateOf(false) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }
    var showImportSuccessDialog by remember { mutableStateOf(false) }
    var showImportFailedDialog by remember { mutableStateOf(false) }
    var exportPath by remember { mutableStateOf("") }
    var exportMessage by remember { mutableStateOf("") }
    
    // 导入相关状态
    var importFilePath by remember { mutableStateOf("") }
    var importMessage by remember { mutableStateOf("") }
    var importTime by remember { mutableStateOf("") }
    var importOplusRomVersion by remember { mutableStateOf("") }
    var importAndroidVersion by remember { mutableStateOf("") }
    var importDeviceModel by remember { mutableStateOf("") }
    var importDeviceMarketName by remember { mutableStateOf("") }
    
    // 文件选择器
    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // 处理导入文件
            coroutineScope.launch {
                try {
                    // 将Uri转换为文件路径
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val tempFile = File.createTempFile("import", ".json", context.cacheDir)
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    try {
                        // 读取配置文件，获取留言和设备信息
                        val configContent = tempFile.readText()
                        val jsonObj = JSONObject(configContent)
                        val metadataObj = jsonObj.optJSONObject("metadata") ?: JSONObject()
                        
                        // 获取留言信息
                        importMessage = metadataObj.optString("message", "")
                        val exportTimestamp = metadataObj.optLong("exportTime", 0L)
                        
                        // 格式化导出时间
                        importTime = if (exportTimestamp > 0) {
                            val date = Date(exportTimestamp)
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
                        } else {
                            "未知"
                        }
                        
                        // 获取设备信息
                        importOplusRomVersion = metadataObj.optString("oplusRomVersion", "未知")
                        importAndroidVersion = metadataObj.optString("androidVersion", "未知")
                        importDeviceModel = metadataObj.optString("deviceModel", "未知")
                        importDeviceMarketName = metadataObj.optString("deviceMarketName", "未知")
                        
                        // 保存导入文件路径
                        importFilePath = tempFile.absolutePath
                        
                        // 显示导入确认对话框
                        showImportConfirmDialog = true
                    } catch (e: Exception) {
                        // JSON解析失败
                        tempFile.delete()
                        showImportFailedDialog = true
                    }
                } catch (e: Exception) {
                    // 显示导入失败对话框
                    showImportFailedDialog = true
                }
            }
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            // 加载状态
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "加载中...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            // 正常显示设置页面
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 模块设置卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "模块设置",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // 深色模式开关
                        SettingsSwitchItem(
                            title = "深色模式",
                            description = "使用深色主题",
                            checked = config.appSettings.darkMode,
                            icon = { Icon(Icons.Filled.DarkMode, contentDescription = null) },
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            enabled = !config.appSettings.followSystemDarkMode
                        )
                        
                        // 深色模式跟随系统
                        SettingsSwitchItem(
                            title = "跟随系统深色模式",
                            description = "自动跟随系统深色模式设置",
                            checked = config.appSettings.followSystemDarkMode,
                            icon = { Icon(Icons.Filled.DarkMode, contentDescription = null) },
                            onCheckedChange = { viewModel.toggleFollowSystemDarkMode() }
                        )
                        
                        // 导出配置
                        SettingsClickableItem(
                            title = "导出配置",
                            description = "导出当前配置到下载目录",
                            icon = { Icon(Icons.Filled.FileDownload, contentDescription = null) },
                            onClick = {
                                if (hasRootAccess) {
                                    exportMessage = ""
                                    showExportMessageDialog = true
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("导出配置需要ROOT权限")
                                    }
                                }
                            }
                        )
                        
                        // 导入配置
                        SettingsClickableItem(
                            title = "导入配置",
                            description = "从文件导入配置",
                            icon = { Icon(Icons.Filled.FileUpload, contentDescription = null) },
                            onClick = {
                                if (hasRootAccess) {
                                    importFileLauncher.launch("application/json")
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("导入配置需要ROOT权限")
                                    }
                                }
                            }
                        )
                        
                        // 关于
                        SettingsClickableItem(
                            title = "关于",
                            description = "查看模块信息和作者",
                            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                            onClick = { showAboutDialog = true }
                        )
                    }
                }
            }
        }
        
        // 没有ROOT权限的提示
        if (!isLoading && !hasRootAccess) {
            NoRootAccessDialog()
        }
        
        // 关于对话框
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("关于") },
                text = {
                    Column {
                        Text("OplusCameraPro")
                        Text("版本: 1.0")
                        Text("作者: TLSU")
                        Text("\n这是一个用于增强OPPO/OnePlus相机应用功能的Xposed模块")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
        
        // 导出留言对话框
        if (showExportMessageDialog) {
            AlertDialog(
                onDismissRequest = { showExportMessageDialog = false },
                title = { Text("导出配置") },
                text = {
                    Column {
                        Text("您可以为导出的配置添加一段留言：")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = exportMessage,
                            onValueChange = { exportMessage = it },
                            label = { Text("留言（可选）") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExportMessageDialog = false
                            coroutineScope.launch {
                                try {
                                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                    val fileName = "OplusCameraPro_config_$timestamp.json"
                                    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    val file = File(downloadDir, fileName)
                                    
                                    val result = viewModel.exportConfig(file.absolutePath, exportMessage)
                                    if (result) {
                                        exportPath = file.absolutePath
                                        showExportDialog = true
                                    } else {
                                        snackbarHostState.showSnackbar("导出失败，请检查权限")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("导出失败：${e.message}")
                                }
                            }
                        }
                    ) {
                        Text("导出")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportMessageDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
        
        // 导出成功对话框
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("导出成功") },
                text = { Text("配置已导出到:\n$exportPath") },
                confirmButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
        
        // 导入确认对话框
        if (showImportConfirmDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showImportConfirmDialog = false
                    // 清理临时文件
                    File(importFilePath).delete()
                },
                title = { Text("确认导入") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        // 留言卡片
                        if (importMessage.isNotBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "配置文件留言",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = importMessage,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                        
                        // 设备信息卡片
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "源设备信息",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                DeviceInfoItem(
                                    label = "设备型号", 
                                    value = if (importDeviceMarketName.isNotBlank() && importDeviceMarketName != "未知") 
                                        "$importDeviceModel ($importDeviceMarketName)" 
                                    else 
                                        importDeviceModel
                                )
                                
                                DeviceInfoItem(label = "ColorOS版本", value = importOplusRomVersion)
                                DeviceInfoItem(label = "Android版本", value = importAndroidVersion)
                                
                                if (importTime.isNotBlank() && importTime != "未知") {
                                    DeviceInfoItem(label = "导出时间", value = importTime)
                                }
                            }
                        }
                        
                        Text("确定要导入此配置文件吗？这将覆盖当前设置。")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showImportConfirmDialog = false
                            coroutineScope.launch {
                                try {
                                    val result = viewModel.importConfig(importFilePath)
                                    if (result) {
                                        showImportSuccessDialog = true
                                    } else {
                                        showImportFailedDialog = true
                                    }
                                } catch (e: Exception) {
                                    showImportFailedDialog = true
                                } finally {
                                    // 清理临时文件
                                    File(importFilePath).delete()
                                }
                            }
                        }
                    ) {
                        Text("确定导入")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showImportConfirmDialog = false
                            // 清理临时文件
                            File(importFilePath).delete()
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
        
        // 导入成功对话框
        if (showImportSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showImportSuccessDialog = false },
                title = { Text("导入成功") },
                text = { Text("配置已成功导入并应用") },
                confirmButton = {
                    TextButton(onClick = { showImportSuccessDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
        
        // 导入失败对话框
        if (showImportFailedDialog) {
            AlertDialog(
                onDismissRequest = { showImportFailedDialog = false },
                title = { Text("导入失败") },
                text = { Text("无法导入配置文件，请确保文件格式正确") },
                confirmButton = {
                    TextButton(onClick = { showImportFailedDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

/**
 * 设备信息项组件
 */
@Composable
private fun DeviceInfoItem(
    label: String,
    value: String,
    icon: ImageVector? = null
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (label != "导出时间") {
            Divider(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            )
        }
    }
} 