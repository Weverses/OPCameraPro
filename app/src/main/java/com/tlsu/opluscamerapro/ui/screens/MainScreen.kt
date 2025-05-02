package com.tlsu.opluscamerapro.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.ui.MainViewModel
import com.tlsu.opluscamerapro.ui.screens.camera.CameraSettingsScreen
import com.tlsu.opluscamerapro.ui.screens.module.ModuleSettingsScreen

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
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "模块设置") },
                    label = { Text("模块设置") },
                    selected = currentPage == 1,
                    onClick = { currentPage = 1 }
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
                1 -> ModuleSettingsScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope
                )
            }
        }
    }
} 