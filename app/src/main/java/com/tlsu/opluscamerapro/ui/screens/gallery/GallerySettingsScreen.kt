package com.tlsu.opluscamerapro.ui.screens.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.ui.MainViewModel
import com.tlsu.opluscamerapro.ui.components.NoRootAccessDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 相册设置屏幕
 */
@Composable
fun GallerySettingsScreen(
    viewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val isLoading = viewModel.isLoading
    val hasRootAccess = viewModel.hasRootAccess
    val config by viewModel.config.collectAsState()
    val context = LocalContext.current
    
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
        } else if (!hasRootAccess) {
            // 无ROOT权限提示
            NoRootAccessDialog()
        } else {
            // 正常显示设置页面
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 相册设置组
                GallerySettingsGroup(
                    gallerySettings = config.gallerySettings,
                    onSettingChanged = { key, value ->
                        viewModel.updateVendorTagSetting { currentConfig ->
                            when (key) {
                                "enableAIComposition" -> currentConfig.copy(
                                    gallerySettings = currentConfig.gallerySettings.copy(
                                        enableAIComposition = value
                                    )
                                )
                                else -> currentConfig
                            }
                        }
                        
                        // 显示保存成功提示
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.settings_saved))
                        }
                    }
                )
            }
        }
    }
} 