package com.tlsu.opluscamerapro.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.ui.MainViewModel
import com.tlsu.opluscamerapro.ui.screens.camera.CameraSettingsScreen
import com.tlsu.opluscamerapro.ui.screens.module.ModuleSettingsScreen
import com.tlsu.opluscamerapro.ui.screens.gallery.GallerySettingsScreen

/**
 * 主屏幕
 */
@Composable
fun MainScreen(viewModel: MainViewModel) {
    // 页面状态
    var currentPage by rememberSaveable { mutableStateOf(0) }
    
    // Snackbar状态
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Camera, contentDescription = "相机设置") },
                    label = { Text("相机设置") },
                    selected = currentPage == 0,
                    onClick = { currentPage = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Photo, contentDescription = "相册设置") },
                    label = { Text("相册设置") },
                    selected = currentPage == 1,
                    onClick = { currentPage = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "模块设置") },
                    label = { Text("模块设置") },
                    selected = currentPage == 2,
                    onClick = { currentPage = 2 }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentPage) {
                0 -> CameraSettingsScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope
                )
                1 -> GallerySettingsScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope
                )
                2 -> ModuleSettingsScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope
                )
            }
        }
        
        // 模块更新对话框
        ModuleUpdateDialog(viewModel = viewModel, context = context)
    }
}

/**
 * 模块更新对话框
 */
@Composable
fun ModuleUpdateDialog(viewModel: MainViewModel, context: android.content.Context) {
    val updateState = viewModel.moduleUpdateState
    val updateProgress = viewModel.moduleUpdateProgress
    
    // 当状态是CHECKING，显示检查中对话框
    if (updateState == MainViewModel.ModuleUpdateState.CHECKING) {
        AlertDialog(
            onDismissRequest = { /* 不允许关闭 */ },
            title = { Text(stringResource(R.string.module_update_title)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.module_update_checking))
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            },
            confirmButton = { /* 无按钮 */ },
            dismissButton = { /* 无按钮 */ }
        )
    }
    
    // 当状态是INSTALLING，显示安装中对话框
    if (updateState == MainViewModel.ModuleUpdateState.INSTALLING) {
        AlertDialog(
            onDismissRequest = { /* 不允许关闭 */ },
            title = { Text(stringResource(R.string.module_update_title)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.module_update_installing))
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { updateProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = { /* 无按钮 */ },
            dismissButton = { /* 无按钮 */ }
        )
    }
    
    // 当状态是SUCCESS，显示完成对话框
    if (updateState == MainViewModel.ModuleUpdateState.SUCCESS) {
        AlertDialog(
            onDismissRequest = { /* 不允许关闭 */ },
            title = { Text(stringResource(R.string.module_update_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.module_update_success))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.module_update_reboot_message))
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.rebootDevice() }) {
                    Text(stringResource(R.string.reboot_now))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    // 重置状态为NONE
                    viewModel.resetModuleUpdateState()
                }) {
                    Text(stringResource(R.string.reboot_delay))
                }
            }
        )
    }
    
    // 当状态是FAILED，显示失败对话框
    if (updateState == MainViewModel.ModuleUpdateState.FAILED) {
        AlertDialog(
            onDismissRequest = { /* 不允许关闭 */ },
            title = { Text(stringResource(R.string.module_update_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.module_update_failed))
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.rebootDevice() }) {
                    Text(stringResource(R.string.reboot_now))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // 重置状态为NONE
                    viewModel.resetModuleUpdateState()
                }) {
                    Text(stringResource(R.string.reboot_delay))
                }
            }
        )
    }
    if (updateState == MainViewModel.ModuleUpdateState.IGNORE) {
        AlertDialog(
            onDismissRequest = { /* 不允许关闭 */ },
            title = { Text(stringResource(R.string.module_update_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.module_update_ignore))
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.rebootDevice() }) {
                    Text(stringResource(R.string.reboot_now))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // 重置状态为NONE
                    viewModel.resetModuleUpdateState()
                }) {
                    Text(stringResource(R.string.reboot_delay))
                }
            }
        )
    }
} 