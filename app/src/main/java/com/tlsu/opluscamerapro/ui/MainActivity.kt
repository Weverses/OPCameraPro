package com.tlsu.opluscamerapro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tlsu.opluscamerapro.ui.screens.MainScreen
import com.tlsu.opluscamerapro.ui.theme.OplusTheme

class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化ViewModel
        viewModel.initialize(this)
        
        setContent {
            // 获取当前配置
            val config by viewModel.config.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()
            
            // 使用应用设置中的深色模式设置或系统默认设置
            var isDarkMode by remember { mutableStateOf(false) }
            isDarkMode = if (config.appSettings.followSystemDarkMode) {
                // 跟随系统模式
                systemInDarkTheme
            } else {
                // 使用应用设置
                config.appSettings.darkMode
            }
            
            OplusTheme(
                darkTheme = isDarkMode
            ) {
                MainScreen(
                    viewModel = viewModel
                )
            }
        }
    }
} 