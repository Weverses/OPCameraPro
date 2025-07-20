package com.tlsu.opluscamerapro.ui.screens.camera

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
 * 相机设置屏幕
 */
@Composable
fun CameraSettingsScreen(
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
                // VendorTag设置组
                VendorTagSettingsGroup(
                    vendorTagSettings = config.vendorTags,
                    onSettingChanged = { key, value ->
                        viewModel.updateVendorTagSetting { currentConfig ->
                            val updatedVendorTags = when (key) {
                                "enable25MP" -> currentConfig.vendorTags.copy(enable25MP = value)
                                "enableMasterMode" -> currentConfig.vendorTags.copy(enableMasterMode = value)
                                "enableMasterRawMax" -> currentConfig.vendorTags.copy(enableMasterRawMax = value)
                                "enablePortraitZoom" -> currentConfig.vendorTags.copy(enablePortraitZoom = value)
                                "enable720p60fps" -> currentConfig.vendorTags.copy(enable720p60fps = value)
                                "enableSlowVideo480fps" -> currentConfig.vendorTags.copy(enableSlowVideo480fps = value)
                                "enableNewMacroMode" -> currentConfig.vendorTags.copy(enableNewMacroMode = value)
                                "enableMacroTele" -> currentConfig.vendorTags.copy(enableMacroTele = value)
                                "enableMacroDepthFusion" -> currentConfig.vendorTags.copy(enableMacroDepthFusion = value)
                                "enableHeifBlurEdit" -> currentConfig.vendorTags.copy(enableHeifBlurEdit = value)
                                "enableStyleEffect" -> currentConfig.vendorTags.copy(enableStyleEffect = value)
                                "enableScaleFocus" -> currentConfig.vendorTags.copy(enableScaleFocus = value)
                                "enableLivePhotoFovOptimize" -> currentConfig.vendorTags.copy(enableLivePhotoFovOptimize = value)
                                "enable10bitPhoto" -> currentConfig.vendorTags.copy(enable10bitPhoto = value)
                                "enableHeifLivePhoto" -> currentConfig.vendorTags.copy(enableHeifLivePhoto = value)
                                "enable10bitLivePhoto" -> currentConfig.vendorTags.copy(enable10bitLivePhoto = value)
                                "enableTolStyleFilter" -> currentConfig.vendorTags.copy(enableTolStyleFilter = value)

                                "enableGrandTourFilter" -> currentConfig.vendorTags.copy(enableGrandTourFilter = value)
                                "enableDesertFilter" -> currentConfig.vendorTags.copy(enableDesertFilter = value)
                                "enableVignetteGrainFilter" -> currentConfig.vendorTags.copy(enableVignetteGrainFilter = value)
                                "enableJzkMovieFilter" -> currentConfig.vendorTags.copy(enableJzkMovieFilter = value)
                                "enableNewBeautyMenu" -> currentConfig.vendorTags.copy(enableNewBeautyMenu = value)
                                "enableSuperTextScanner" -> currentConfig.vendorTags.copy(enableSuperTextScanner = value)
                                "enableSoftLightPhotoMode" -> currentConfig.vendorTags.copy(enableSoftLightPhotoMode = value)
                                "enableSoftLightNightMode" -> currentConfig.vendorTags.copy(enableSoftLightNightMode = value)
                                "enableSoftLightProMode" -> currentConfig.vendorTags.copy(enableSoftLightProMode = value)
                                "enableMeisheFilter" -> currentConfig.vendorTags.copy(enableMeisheFilter = value)
                                "enablePreviewHdr" -> currentConfig.vendorTags.copy(enablePreviewHdr = value)
                                "enableVideoAutoFps" -> currentConfig.vendorTags.copy(enableVideoAutoFps = value)
                                "enableQuickLaunch" -> currentConfig.vendorTags.copy(enableQuickLaunch = value)
                                "enableLivePhotoHighBitrate" -> currentConfig.vendorTags.copy(enableLivePhotoHighBitrate = value)
                                "enableVideoStopSoundImmediate" -> currentConfig.vendorTags.copy(enableVideoStopSoundImmediate = value)
                                "enableForcePortraitForThirdParty" -> currentConfig.vendorTags.copy(enableForcePortraitForThirdParty = value)
                                "enableFrontCameraZoom" -> currentConfig.vendorTags.copy(enableFrontCameraZoom = value)
                                "enablePortraitRearFlash" -> currentConfig.vendorTags.copy(enablePortraitRearFlash = value)
                                "enableAiHdSwitch" -> currentConfig.vendorTags.copy(enableAiHdSwitch = value)
                                "enableTeleSdsr" -> currentConfig.vendorTags.copy(enableTeleSdsr = value)
                                "enableDolbyVideo" -> currentConfig.vendorTags.copy(enableDolbyVideo = value)
                                "enableDolbyVideo60fps" -> currentConfig.vendorTags.copy(enableDolbyVideo60fps = value)
                                "enableDolbyVideoSat" -> currentConfig.vendorTags.copy(enableDolbyVideoSat = value)
                                "enableFrontDolbyVideo" -> currentConfig.vendorTags.copy(enableFrontDolbyVideo = value)
                                "enableVideoLockLens" -> currentConfig.vendorTags.copy(enableVideoLockLens = value)
                                "enableVideoLockWb" -> currentConfig.vendorTags.copy(enableVideoLockWb = value)
                                "enableMicStatusCheck" -> currentConfig.vendorTags.copy(enableMicStatusCheck = value)
                                "enableMasterFilter" -> currentConfig.vendorTags.copy(enableMasterFilter = value)
                                "enableJiangWenFilter" -> currentConfig.vendorTags.copy(enableJiangWenFilter = value)
                                "enableHasselbladWatermark" -> currentConfig.vendorTags.copy(
                                    enableHasselbladWatermark = value,
                                    enableHasselbladWatermarkGuide = value,
                                    enableHasselbladWatermarkDefault = value
                                )
                                "enableGlobalEv" -> currentConfig.vendorTags.copy(enableGlobalEv = value)
                                "enableOs15NewFilter" -> currentConfig.vendorTags.copy(enableOs15NewFilter = value)
                                "enableSwitchLensFocalLength" -> currentConfig.vendorTags.copy(enableSwitchLensFocalLength = value)
                                "enableMotionCapture" -> currentConfig.vendorTags.copy(enableMotionCapture = value)
                                "enable4K120fpsVideo" -> currentConfig.vendorTags.copy(enable4K120fpsVideo = value)
                                "enable1080p120fpsVideo" -> currentConfig.vendorTags.copy(enable1080p120fpsVideo = value)
                                "enableDolbyVideo120fps" -> currentConfig.vendorTags.copy(enableDolbyVideo120fps = value)
                                "enableMultiFrameBurstShot" -> currentConfig.vendorTags.copy(enableMultiFrameBurstShot = value)
                                "enableVideoSoundFocus" -> currentConfig.vendorTags.copy(enableVideoSoundFocus = value)
                                "enableFront4KVideo" -> currentConfig.vendorTags.copy(enableFront4KVideo = value)
                                "enableAiScenePreset" -> currentConfig.vendorTags.copy(enableAiScenePreset = value)
                                "enableISOExtension" -> currentConfig.vendorTags.copy(enableISOExtension = value)
                                "enableLivePhoto" -> currentConfig.vendorTags.copy(enableLivePhoto = value)
                                "enableMasterModeLivePhoto" -> currentConfig.vendorTags.copy(enableMasterModeLivePhoto = value)
                                "enableSoftLightFilter" -> currentConfig.vendorTags.copy(enableSoftLightFilter = value)
                                "enableFlashFilter" -> currentConfig.vendorTags.copy(enableFlashFilter = value)

                                else -> currentConfig.vendorTags
                            }
                            
                            currentConfig.copy(vendorTags = updatedVendorTags)
                        }
                        
                        // 显示保存成功提示
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.settings_saved))
                        }
                    },
                    onBitrateChanged = { newBitrate ->
                        // 处理码率变更
                        viewModel.updateVendorTagSetting { currentConfig ->
                            val updatedVendorTags = currentConfig.vendorTags.copy(
                                livePhotoBitrate = newBitrate
                            )
                            currentConfig.copy(vendorTags = updatedVendorTags)
                        }
                        
                        // 显示保存成功提示
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("实况视频码率已更新为 ${newBitrate}Mbps")
                        }
                    },
                    onAiHdZoomValueChanged = { newValue ->
                        // 处理AI超清望远算法倍率变更
                        viewModel.updateVendorTagSetting { currentConfig ->
                            val updatedVendorTags = currentConfig.vendorTags.copy(
                                aiHdZoomValue = newValue
                            )
                            currentConfig.copy(vendorTags = updatedVendorTags)
                        }
                        
                        // 显示保存成功提示
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("AI超清望远算法介入倍率已更新为 ${newValue}倍")
                        }
                    },
                    onTeleSdsrZoomValueChanged = { newValue ->
                        // 处理超清长焦算法倍率变更
                        viewModel.updateVendorTagSetting { currentConfig ->
                            val updatedVendorTags = currentConfig.vendorTags.copy(
                                teleSdsrZoomValue = newValue
                            )
                            currentConfig.copy(vendorTags = updatedVendorTags)
                        }
                        
                        // 显示保存成功提示
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("超清长焦算法介入倍率已更新为 ${newValue}倍")
                        }
                    },
                    onDurationChanged = { maxDuration, minDuration ->
                        // 处理实况照片时长变更
                        viewModel.updateVendorTagSetting { currentConfig ->
                            val updatedVendorTags = currentConfig.vendorTags.copy(
                                livePhotoMaxDuration = maxDuration,
                                livePhotoMinDuration = minDuration
                            )
                            currentConfig.copy(vendorTags = updatedVendorTags)
                        }
                        
                        // 显示保存成功提示
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("实况照片时长已更新为：最大${maxDuration}毫秒，最小${minDuration}毫秒")
                        }
                    }
                )
                
                // 其他设置组
                OtherSettingsGroup(
                    otherSettings = config.otherSettings,
                    onSettingChanged = { key, value ->
                        // 目前没有其他设置，可以在后续添加
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.settings_saved))
                        }
                    }
                )
            }
        }
    }
} 