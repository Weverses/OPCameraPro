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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.ui.MainViewModel
import com.tlsu.opluscamerapro.ui.components.NoRootAccessDialog
import com.tlsu.opluscamerapro.ui.components.SettingsClickableItem
import com.tlsu.opluscamerapro.ui.components.SettingsSwitchItem
import com.tlsu.opluscamerapro.utils.DeviceCheck.execWithResult
import com.tlsu.opluscamerapro.utils.SubModule.MAGISK_MODULE_PATH
import com.tlsu.opluscamerapro.utils.SubModule.deleteFrameworkAndLibs
import com.tlsu.opluscamerapro.utils.SubModule.getCurrentMountMethod
import com.tlsu.opluscamerapro.utils.SubModule.switchToDefaultMount
import com.tlsu.opluscamerapro.utils.SubModule.switchToMountBind
import com.tlsu.opluscamerapro.utils.SubModule.switchToOverlayFS
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

    var mountCurrent by remember { mutableStateOf("") }

    // 对话框状态
    var showAboutDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showExportMessageDialog by remember { mutableStateOf(false) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }
    var showImportSuccessDialog by remember { mutableStateOf(false) }
    var showImportFailedDialog by remember { mutableStateOf(false) }
    var showSubmoduleDialog by remember { mutableStateOf(false) }
    var showCrashFixDialog by remember { mutableStateOf(false) }
    var showSwitchMountDialog by remember { mutableStateOf(false) }
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
                            context.getString(R.string.unknown)
                        }
                        
                        // 获取设备信息
                        importOplusRomVersion = metadataObj.optString("oplusRomVersion", context.getString(R.string.unknown))
                        importAndroidVersion = metadataObj.optString("androidVersion", context.getString(R.string.unknown))
                        importDeviceModel = metadataObj.optString("deviceModel", context.getString(R.string.unknown))
                        importDeviceMarketName = metadataObj.optString("deviceMarketName", context.getString(R.string.unknown))
                        
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
                    text = stringResource(R.string.loading),
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
                            text = stringResource(R.string.module_settings),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // 深色模式开关
                        SettingsSwitchItem(
                            title = stringResource(R.string.dark_mode),
                            description = stringResource(R.string.dark_mode_description),
                            checked = config.appSettings.darkMode,
                            icon = { Icon(Icons.Filled.DarkMode, contentDescription = null) },
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            enabled = !config.appSettings.followSystemDarkMode
                        )
                        
                        // 深色模式跟随系统
                        SettingsSwitchItem(
                            title = stringResource(R.string.follow_system_dark_mode),
                            description = stringResource(R.string.follow_system_dark_mode_description),
                            checked = config.appSettings.followSystemDarkMode,
                            icon = { Icon(Icons.Filled.DarkMode, contentDescription = null) },
                            onCheckedChange = { viewModel.toggleFollowSystemDarkMode() }
                        )
                        
                        // 导出配置
                        SettingsClickableItem(
                            title = stringResource(R.string.export_config),
                            description = stringResource(R.string.export_config_description),
                            icon = { Icon(Icons.Filled.FileUpload, contentDescription = null) },
                            onClick = {
                                if (hasRootAccess) {
                                    exportMessage = ""
                                    showExportMessageDialog = true
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.need_root_for_export))
                                    }
                                }
                            }
                        )
                        
                        // 导入配置
                        SettingsClickableItem(
                            title = stringResource(R.string.import_config),
                            description = stringResource(R.string.import_config_description),
                            icon = { Icon(Icons.Filled.FileDownload, contentDescription = null) },
                            onClick = {
                                if (hasRootAccess) {
                                    importFileLauncher.launch("application/json")
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.need_root_for_import))
                                    }
                                }
                            }
                        )
                        
                        // 重启相机
                        SettingsClickableItem(
                            title = stringResource(R.string.restart_camera_and_gallery),
                            description = stringResource(R.string.restart_camera_and_gallery_desc),
                            icon = { Icon(Icons.Filled.PhotoCamera, contentDescription = null) },
                            onClick = {
                                if (hasRootAccess) {
                                    coroutineScope.launch {
                                        val result = viewModel.restartCameraApp()
                                        if (result) {
                                            snackbarHostState.showSnackbar(context.getString(R.string.camera_and_gallery_restarted))
                                        } else {
                                            snackbarHostState.showSnackbar(context.getString(R.string.restart_failed))
                                        }
                                    }
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.no_root_permission))
                                    }
                                }
                            }
                        )

//                        // 子模块设置
//                        SettingsClickableItem(
//                            title = stringResource(R.string.submodule_title),
//                            description = stringResource(R.string.submodule_desc),
//                            icon = { Icon(Icons.Default.InstallMobile, contentDescription = null) },
//                            onClick = { showSubmoduleDialog = true }
//                        )
//
//                        // 子模块挂载切换
//                        if(execWithResult("test -f $MAGISK_MODULE_PATH/version.txt && echo true || echo false")
//                                .out.joinToString("").contains("true")) {
//                            SettingsClickableItem(
//                                title = stringResource(R.string.submodule_switch_mount_title),
//                                description = stringResource(R.string.submodule_switch_mount_desc),
//                                icon = {
//                                    Icon(
//                                        Icons.Default.Autorenew,
//                                        contentDescription = null
//                                    )
//                                },
//                                onClick = { showSwitchMountDialog = true }
//                            )
//                        }

                        SettingsClickableItem(
                            title = stringResource(R.string.delete_libs_and_framework),
                            description = stringResource(R.string.delete_libs_and_framework_desc),
                            icon = { Icon(Icons.Filled.Construction, contentDescription = null) },
                            onClick = {
                                showCrashFixDialog = true
                            }
                        )

                        // 捐赠
                        SettingsClickableItem(
                            title = stringResource(R.string.donate_title),
                            description = stringResource(R.string.donate_desc),
                            icon = { Icon(Icons.Filled.Coffee, contentDescription = null) },
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gitee.com/bugme7/OPCameraPro/blob/master/donate.jpg"))
                                context.startActivity(intent)
                            }
                        )

                        // 关于
                        SettingsClickableItem(
                            title = stringResource(R.string.about),
                            description = stringResource(R.string.about_description),
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
                title = { Text(stringResource(R.string.about_dialog_title)) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        // 应用名称
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // 版本信息卡片
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.version_label),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.about_dialog_version),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        // 作者信息卡片
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/36076935"))
                                    context.startActivity(intent)
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.author_label),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.about_dialog_author),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        // Telegram信息卡片
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://t.me/+awg-7X5Ggrs5NzZl"))
                                    context.startActivity(intent)
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Telegram",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.telegram_channel_desc),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        // 模块介绍卡片
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.module_introduction_label),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.about_dialog_description),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            )
        }
        
        // 导出留言对话框
        if (showExportMessageDialog) {
            AlertDialog(
                onDismissRequest = { showExportMessageDialog = false },
                title = { Text(stringResource(R.string.export_dialog_title)) },
                text = {
                    Column {
                        Text(stringResource(R.string.export_dialog_message))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = exportMessage,
                            onValueChange = { exportMessage = it },
                            label = { Text(stringResource(R.string.export_dialog_hint)) },
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
                                        snackbarHostState.showSnackbar(context.getString(R.string.export_failed_check_permission))
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(context.getString(R.string.export_failed_with_reason, e.message))
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.export))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportMessageDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        
        // 导出成功对话框
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text(stringResource(R.string.export_success_title)) },
                text = { Text(context.getString(R.string.export_success_message, exportPath)) },
                confirmButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text(stringResource(R.string.confirm))
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
                title = { Text(stringResource(R.string.import_confirm_title)) },
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
                                        text = stringResource(R.string.config_message_card_title),
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
                                    text = stringResource(R.string.device_info_card_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                DeviceInfoItem(
                                    label = stringResource(R.string.config_metadata_device_model), 
                                    value = if (importDeviceMarketName.isNotBlank() && importDeviceMarketName != context.getString(R.string.unknown)) 
                                        context.getString(R.string.device_model_with_market_name, importDeviceModel, importDeviceMarketName) 
                                    else 
                                        importDeviceModel
                                )
                                
                                DeviceInfoItem(label = stringResource(R.string.config_metadata_oplus_rom_version), value = importOplusRomVersion)
                                DeviceInfoItem(label = stringResource(R.string.config_metadata_android_version), value = importAndroidVersion)
                                
                                if (importTime.isNotBlank() && importTime != context.getString(R.string.unknown)) {
                                    DeviceInfoItem(label = stringResource(R.string.export_time_label), value = importTime)
                                }
                            }
                        }
                        
                        Text(stringResource(R.string.import_confirm_message))
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
                        Text(stringResource(R.string.import_confirm_button))
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
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
        
        // 导入成功对话框
        if (showImportSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showImportSuccessDialog = false },
                title = { Text(stringResource(R.string.import_success_title)) },
                text = { Text(stringResource(R.string.import_success_message)) },
                confirmButton = {
                    TextButton(onClick = { showImportSuccessDialog = false }) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            )
        }
        
        // 导入失败对话框
        if (showImportFailedDialog) {
            AlertDialog(
                onDismissRequest = { showImportFailedDialog = false },
                title = { Text(stringResource(R.string.import_failed_title)) },
                text = { Text(stringResource(R.string.import_failed_message)) },
                confirmButton = {
                    TextButton(onClick = { showImportFailedDialog = false }) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            )
        }

        if (showCrashFixDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCrashFixDialog = false
                },
                title = { Text(stringResource(R.string.delete_libs_and_framework)) },
                text = {
                    Column {
                        Text(stringResource(R.string.delete_libs_and_framework_desc))
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (hasRootAccess) {
                            coroutineScope.launch {
                                val result = deleteFrameworkAndLibs()
                                if (result) {
                                    snackbarHostState.showSnackbar(context.getString(R.string.delete_success))
                                } else {
                                    snackbarHostState.showSnackbar(context.getString(R.string.delete_failed))
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.no_root_permission))
                            }
                        }
                        showCrashFixDialog = false
                    }) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCrashFixDialog = false
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                })
        }
        // 子模块选择对话框
        if (showSubmoduleDialog) {
            AlertDialog(
                onDismissRequest = { showSubmoduleDialog = false },
                title = { Text(stringResource(R.string.submodule_title)) },
                text = {
                    Column {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    showSubmoduleDialog = false
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gitee.com/bugme7/OPCameraPro/raw/master/SubModule/Submodule_2.3.0.zip"))
                                    context.startActivity(intent)
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.submodule_common_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.submodule_common_desc),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    showSubmoduleDialog = false
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gitee.com/bugme7/OPCameraPro/raw/master/SubModule/CameraApp_5052383.zip"))
                                    context.startActivity(intent)
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.submodule_apk_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.submodule_apk_desc),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.submodule_install_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.submodule_install_desc),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSubmoduleDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        // 子模块挂载切换对话框
        if (showSwitchMountDialog) {
            var currentMountMehod = getCurrentMountMethod()
            AlertDialog(
                onDismissRequest = { showSwitchMountDialog = false },
                title = { Text(stringResource(R.string.submodule_switch_mount_title)) },
                text = {
                    Column {
                        // 卡片1
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    showSwitchMountDialog = false
                                    switchToDefaultMount()
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.submodule_default_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.submodule_default_desc),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        // 卡片2
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    showSwitchMountDialog = false
                                    switchToOverlayFS()
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.submodule_overlayfs_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.submodule_overlayfs_desc),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        // 卡片3
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    showSwitchMountDialog = false
                                    switchToMountBind()
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.submodule_mountbind_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.submodule_mountbind_desc),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.submodule_mount_current),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentMountMehod,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showSwitchMountDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
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
    val context = LocalContext.current
    
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
        
        if (label != stringResource(R.string.export_time_label)) {
            Divider(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            )
        }
    }
}