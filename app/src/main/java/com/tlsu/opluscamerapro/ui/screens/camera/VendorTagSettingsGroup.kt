package com.tlsu.opluscamerapro.ui.screens.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.data.VendorTagSettings
import com.tlsu.opluscamerapro.ui.components.SettingsSwitchItem
import com.tlsu.opluscamerapro.utils.DefaultConfigManager

/**
 * VendorTag设置组
 */
@Composable
fun VendorTagSettingsGroup(
    vendorTagSettings: VendorTagSettings,
    onSettingChanged: (String, Boolean) -> Unit,
    onBitrateChanged: (Int) -> Unit = {},
    onAiHdZoomValueChanged: (Int) -> Unit = {},
    onTeleSdsrZoomValueChanged: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    
    // 状态变量控制码率输入对话框
    var showBitrateDialog by remember { mutableStateOf(false) }
    var bitrateValue by remember { mutableStateOf(vendorTagSettings.livePhotoBitrate.toString()) }
    var bitrateError by remember { mutableStateOf<String?>(null) }
    
    // 状态变量控制AI超清望远算法倍率输入对话框
    var showAiHdZoomDialog by remember { mutableStateOf(false) }
    var aiHdZoomValue by remember { mutableStateOf(vendorTagSettings.aiHdZoomValue.toString()) }
    var aiHdZoomError by remember { mutableStateOf<String?>(null) }
    
    // 状态变量控制超清长焦算法倍率输入对话框
    var showTeleSdsrZoomDialog by remember { mutableStateOf(false) }
    var teleSdsrZoomValue by remember { mutableStateOf(vendorTagSettings.teleSdsrZoomValue.toString()) }
    var teleSdsrZoomError by remember { mutableStateOf<String?>(null) }
    
    if (showBitrateDialog) {
        AlertDialog(
            onDismissRequest = { showBitrateDialog = false },
            title = { Text("设置实况视频码率") },
            text = {
                Column {
                    Text("请输入实况视频的码率值（Mbps）：")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bitrateValue,
                        onValueChange = { 
                            bitrateValue = it
                            // 验证输入
                            bitrateError = try {
                                val value = it.toInt()
                                if (value < 10) {
                                    "码率不能小于10Mbps"
                                } else if (value > 100) {
                                    "码率不能大于100Mbps"
                                } else {
                                    null
                                }
                            } catch (e: NumberFormatException) {
                                "请输入有效的数字"
                            }
                        },
                        isError = bitrateError != null,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("码率 (Mbps)") },
                        singleLine = true
                    )
                    if (bitrateError != null) {
                        Text(
                            text = bitrateError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "当前值: ${vendorTagSettings.livePhotoBitrate}Mbps",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "提示: 更高的码率意味着更高的视频质量，但会占用更多存储空间",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val newBitrate = bitrateValue.toInt()
                            if (bitrateError == null && newBitrate != vendorTagSettings.livePhotoBitrate) {
                                onBitrateChanged(newBitrate)
                            }
                            showBitrateDialog = false
                        } catch (e: NumberFormatException) {
                            bitrateError = "请输入有效的数字"
                        }
                    },
                    enabled = bitrateError == null
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBitrateDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // AI超清望远算法倍率对话框
    if (showAiHdZoomDialog) {
        AlertDialog(
            onDismissRequest = { showAiHdZoomDialog = false },
            title = { Text("设置AI超清望远算法介入倍率") },
            text = {
                Column {
                    Text("请输入AI超清望远算法介入倍率值：")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = aiHdZoomValue,
                        onValueChange = { 
                            aiHdZoomValue = it
                            // 验证输入
                            aiHdZoomError = try {
                                val value = it.toInt()
                                if (value < 10) {
                                    "倍率不能小于10"
                                } else if (value > 100) {
                                    "倍率不能大于100"
                                } else {
                                    null
                                }
                            } catch (e: NumberFormatException) {
                                "请输入有效的数字"
                            }
                        },
                        isError = aiHdZoomError != null,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("倍率") },
                        singleLine = true
                    )
                    if (aiHdZoomError != null) {
                        Text(
                            text = aiHdZoomError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "当前值: ${vendorTagSettings.aiHdZoomValue}倍",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "提示: 该值表示AI超清望远算法在多少倍率下开始介入，建议值为60",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val newValue = aiHdZoomValue.toInt()
                            if (aiHdZoomError == null && newValue != vendorTagSettings.aiHdZoomValue) {
                                onAiHdZoomValueChanged(newValue)
                            }
                            showAiHdZoomDialog = false
                        } catch (e: NumberFormatException) {
                            aiHdZoomError = "请输入有效的数字"
                        }
                    },
                    enabled = aiHdZoomError == null
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiHdZoomDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 超清长焦算法倍率对话框
    if (showTeleSdsrZoomDialog) {
        AlertDialog(
            onDismissRequest = { showTeleSdsrZoomDialog = false },
            title = { Text("设置超清长焦算法介入倍率") },
            text = {
                Column {
                    Text("请输入超清长焦算法介入倍率值：")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = teleSdsrZoomValue,
                        onValueChange = { 
                            teleSdsrZoomValue = it
                            // 验证输入
                            teleSdsrZoomError = try {
                                val value = it.toInt()
                                if (value < 5) {
                                    "倍率不能小于5"
                                } else if (value > 60) {
                                    "倍率不能大于60"
                                } else {
                                    null
                                }
                            } catch (e: NumberFormatException) {
                                "请输入有效的数字"
                            }
                        },
                        isError = teleSdsrZoomError != null,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("倍率") },
                        singleLine = true
                    )
                    if (teleSdsrZoomError != null) {
                        Text(
                            text = teleSdsrZoomError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "当前值: ${vendorTagSettings.teleSdsrZoomValue}倍",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "提示: 该值表示超清长焦算法在多少倍率下开始介入，建议值为20",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val newValue = teleSdsrZoomValue.toInt()
                            if (teleSdsrZoomError == null && newValue != vendorTagSettings.teleSdsrZoomValue) {
                                onTeleSdsrZoomValueChanged(newValue)
                            }
                            showTeleSdsrZoomDialog = false
                        } catch (e: NumberFormatException) {
                            teleSdsrZoomError = "请输入有效的数字"
                        }
                    },
                    enabled = teleSdsrZoomError == null
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTeleSdsrZoomDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.vendor_tag_settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        
        // 高级拍照设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_advanced)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_25mp_title),
                description = stringResource(R.string.camera_settings_25mp_desc),
                checked = vendorTagSettings.enable25MP,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enable25MP"),
                onCheckedChange = { onSettingChanged("enable25MP", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_mode_title),
                description = stringResource(R.string.camera_settings_master_mode_desc),
                checked = vendorTagSettings.enableMasterMode,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMasterMode"),
                onCheckedChange = { onSettingChanged("enableMasterMode", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_preview_hdr_title),
                description = stringResource(R.string.camera_settings_preview_hdr_desc),
                checked = vendorTagSettings.enablePreviewHdr,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enablePreviewHdr"),
                onCheckedChange = { onSettingChanged("enablePreviewHdr", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_raw_max_title),
                description = stringResource(R.string.camera_settings_master_raw_max_desc),
                checked = vendorTagSettings.enableMasterRawMax,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMasterRawMax"),
                onCheckedChange = { onSettingChanged("enableMasterRawMax", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_portrait_zoom_title),
                description = stringResource(R.string.camera_settings_portrait_zoom_desc),
                checked = vendorTagSettings.enablePortraitZoom,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enablePortraitZoom"),
                onCheckedChange = { onSettingChanged("enablePortraitZoom", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_portrait_rear_flash_title),
                description = stringResource(R.string.camera_settings_portrait_rear_flash_desc),
                checked = vendorTagSettings.enablePortraitRearFlash,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enablePortraitRearFlash"),
                onCheckedChange = { onSettingChanged("enablePortraitRearFlash", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_scale_focus_title),
                description = stringResource(R.string.camera_settings_scale_focus_desc),
                checked = vendorTagSettings.enableScaleFocus,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableScaleFocus"),
                onCheckedChange = { onSettingChanged("enableScaleFocus", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_new_beauty_menu_title),
                description = stringResource(R.string.camera_settings_new_beauty_menu_desc),
                checked = vendorTagSettings.enableNewBeautyMenu,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableNewBeautyMenu"),
                onCheckedChange = { onSettingChanged("enableNewBeautyMenu", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_super_text_scanner_title),
                description = stringResource(R.string.camera_settings_super_text_scanner_desc),
                checked = vendorTagSettings.enableSuperTextScanner,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSuperTextScanner"),
                onCheckedChange = { onSettingChanged("enableSuperTextScanner", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_quick_launch_title),
                description = stringResource(R.string.camera_settings_quick_launch_desc),
                checked = vendorTagSettings.enableQuickLaunch,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableQuickLaunch"),
                onCheckedChange = { onSettingChanged("enableQuickLaunch", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_force_portrait_title),
                description = stringResource(R.string.camera_settings_force_portrait_desc),
                checked = vendorTagSettings.enableForcePortraitForThirdParty,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableForcePortraitForThirdParty"),
                onCheckedChange = { onSettingChanged("enableForcePortraitForThirdParty", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_front_camera_zoom_title),
                description = stringResource(R.string.camera_settings_front_camera_zoom_desc),
                checked = vendorTagSettings.enableFrontCameraZoom,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableFrontCameraZoom"),
                onCheckedChange = { onSettingChanged("enableFrontCameraZoom", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_ai_hd_switch_title),
                description = "${stringResource(R.string.camera_settings_ai_hd_switch_desc)} (当前介入倍率: ${vendorTagSettings.aiHdZoomValue}倍)",
                checked = vendorTagSettings.enableAiHdSwitch,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableAiHdSwitch"),
                onCheckedChange = { newState -> 
                    onSettingChanged("enableAiHdSwitch", newState)
                    if (newState) {
                        // 如果开启了AI超清望远算法，显示倍率设置对话框
                        aiHdZoomValue = vendorTagSettings.aiHdZoomValue.toString()
                        aiHdZoomError = null
                        showAiHdZoomDialog = true
                    }
                }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_tele_sdsr_title),
                description = "${stringResource(R.string.camera_settings_tele_sdsr_desc)} (当前介入倍率: ${vendorTagSettings.teleSdsrZoomValue}倍)",
                checked = vendorTagSettings.enableTeleSdsr,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableTeleSdsr"),
                onCheckedChange = { newState -> 
                    onSettingChanged("enableTeleSdsr", newState)
                    if (newState) {
                        // 如果开启了超清长焦算法，显示倍率设置对话框
                        teleSdsrZoomValue = vendorTagSettings.teleSdsrZoomValue.toString()
                        teleSdsrZoomError = null
                        showTeleSdsrZoomDialog = true
                    }
                }
            )
        }
        
        // 视频设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_video)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_720p_60fps_title),
                description = stringResource(R.string.camera_settings_720p_60fps_desc),
                checked = vendorTagSettings.enable720p60fps,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enable720p60fps"),
                onCheckedChange = { onSettingChanged("enable720p60fps", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_slow_video_480fps_title),
                description = stringResource(R.string.camera_settings_slow_video_480fps_desc),
                checked = vendorTagSettings.enableSlowVideo480fps,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSlowVideo480fps"),
                onCheckedChange = { onSettingChanged("enableSlowVideo480fps", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_video_auto_fps_title),
                description = stringResource(R.string.camera_settings_video_auto_fps_desc),
                checked = vendorTagSettings.enableVideoAutoFps,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableVideoAutoFps"),
                onCheckedChange = { onSettingChanged("enableVideoAutoFps", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_live_photo_bitrate_title),
                description = "${stringResource(R.string.camera_settings_live_photo_bitrate_desc)} (当前: ${vendorTagSettings.livePhotoBitrate}Mbps)",
                checked = vendorTagSettings.enableLivePhotoHighBitrate,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableLivePhotoHighBitrate"),
                onCheckedChange = { newState -> 
                    onSettingChanged("enableLivePhotoHighBitrate", newState)
                    if (newState) {
                        // 如果开启了高码率功能，显示自定义码率对话框
                        bitrateValue = vendorTagSettings.livePhotoBitrate.toString()
                        bitrateError = null
                        showBitrateDialog = true
                    }
                }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_dolby_video_title),
                description = stringResource(R.string.camera_settings_dolby_video_desc),
                checked = vendorTagSettings.enableDolbyVideo,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableDolbyVideo"),
                onCheckedChange = { onSettingChanged("enableDolbyVideo", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_dolby_video_60fps_title),
                description = stringResource(R.string.camera_settings_dolby_video_60fps_desc),
                checked = vendorTagSettings.enableDolbyVideo60fps,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableDolbyVideo60fps"),
                onCheckedChange = { onSettingChanged("enableDolbyVideo60fps", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_dolby_video_sat_title),
                description = stringResource(R.string.camera_settings_dolby_video_sat_desc),
                checked = vendorTagSettings.enableDolbyVideoSat,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableDolbyVideoSat"),
                onCheckedChange = { onSettingChanged("enableDolbyVideoSat", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_front_dolby_video_title),
                description = stringResource(R.string.camera_settings_front_dolby_video_desc),
                checked = vendorTagSettings.enableFrontDolbyVideo,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableFrontDolbyVideo"),
                onCheckedChange = { onSettingChanged("enableFrontDolbyVideo", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_video_stop_sound_title),
                description = stringResource(R.string.camera_settings_video_stop_sound_desc),
                checked = vendorTagSettings.enableVideoStopSoundImmediate,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableVideoStopSoundImmediate"),
                onCheckedChange = { onSettingChanged("enableVideoStopSoundImmediate", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_video_lock_lens_title),
                description = stringResource(R.string.camera_settings_video_lock_lens_desc),
                checked = vendorTagSettings.enableVideoLockLens,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableVideoLockLens"),
                onCheckedChange = { onSettingChanged("enableVideoLockLens", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_video_lock_wb_title),
                description = stringResource(R.string.camera_settings_video_lock_wb_desc),
                checked = vendorTagSettings.enableVideoLockWb,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableVideoLockWb"),
                onCheckedChange = { onSettingChanged("enableVideoLockWb", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_mic_status_check_title),
                description = stringResource(R.string.camera_settings_mic_status_check_desc),
                checked = vendorTagSettings.enableMicStatusCheck,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMicStatusCheck"),
                onCheckedChange = { onSettingChanged("enableMicStatusCheck", it) }
            )
        }
        
        // 微距设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_macro)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_new_macro_mode_title),
                description = stringResource(R.string.camera_settings_new_macro_mode_desc),
                checked = vendorTagSettings.enableNewMacroMode,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableNewMacroMode"),
                onCheckedChange = { onSettingChanged("enableNewMacroMode", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_macro_tele_title),
                description = stringResource(R.string.camera_settings_macro_tele_desc),
                checked = vendorTagSettings.enableMacroTele,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMacroTele"),
                onCheckedChange = { onSettingChanged("enableMacroTele", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_macro_depth_fusion_title),
                description = stringResource(R.string.camera_settings_macro_depth_fusion_desc),
                checked = vendorTagSettings.enableMacroDepthFusion,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMacroDepthFusion"),
                onCheckedChange = { onSettingChanged("enableMacroDepthFusion", it) }
            )
        }
        
        // 滤镜设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_filter)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_style_effect_title),
                description = stringResource(R.string.camera_settings_style_effect_desc),
                checked = vendorTagSettings.enableStyleEffect,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableStyleEffect"),
                onCheckedChange = { onSettingChanged("enableStyleEffect", it) }
            )

            // 大师滤镜
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_filter_title),
                description = stringResource(R.string.camera_settings_master_filter_desc),
                checked = vendorTagSettings.enableMasterFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMasterFilter"),
                onCheckedChange = { onSettingChanged("enableMasterFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_tol_style_filter_title),
                description = stringResource(R.string.camera_settings_tol_style_filter_desc),
                checked = vendorTagSettings.enableTolStyleFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableTolStyleFilter"),
                onCheckedChange = { onSettingChanged("enableTolStyleFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_grand_tour_filter_title),
                description = stringResource(R.string.camera_settings_grand_tour_filter_desc),
                checked = vendorTagSettings.enableGrandTourFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableGrandTourFilter"),
                onCheckedChange = { onSettingChanged("enableGrandTourFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_desert_filter_title),
                description = stringResource(R.string.camera_settings_desert_filter_desc),
                checked = vendorTagSettings.enableDesertFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableDesertFilter"),
                onCheckedChange = { onSettingChanged("enableDesertFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_vignette_grain_filter_title),
                description = stringResource(R.string.camera_settings_vignette_grain_filter_desc),
                checked = vendorTagSettings.enableVignetteGrainFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableVignetteGrainFilter"),
                onCheckedChange = { onSettingChanged("enableVignetteGrainFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_jiang_wen_filter_title),
                description = stringResource(R.string.camera_settings_jiang_wen_filter_desc),
                checked = vendorTagSettings.enableJiangWenFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableJiangWenFilter"),
                onCheckedChange = { onSettingChanged("enableJiangWenFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_jzk_movie_filter_title),
                description = stringResource(R.string.camera_settings_jzk_movie_filter_desc),
                checked = vendorTagSettings.enableJzkMovieFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableJzkMovieFilter"),
                onCheckedChange = { onSettingChanged("enableJzkMovieFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_meishe_filter_title),
                description = stringResource(R.string.camera_settings_meishe_filter_desc),
                checked = vendorTagSettings.enableMeisheFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMeisheFilter"),
                onCheckedChange = { onSettingChanged("enableMeisheFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_soft_light_photo_title),
                description = stringResource(R.string.camera_settings_soft_light_photo_desc),
                checked = vendorTagSettings.enableSoftLightPhotoMode,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSoftLightPhotoMode"),
                onCheckedChange = { onSettingChanged("enableSoftLightPhotoMode", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_soft_light_night_title),
                description = stringResource(R.string.camera_settings_soft_light_night_desc),
                checked = vendorTagSettings.enableSoftLightNightMode,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSoftLightNightMode"),
                onCheckedChange = { onSettingChanged("enableSoftLightNightMode", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_soft_light_pro_title),
                description = stringResource(R.string.camera_settings_soft_light_pro_desc),
                checked = vendorTagSettings.enableSoftLightProMode,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSoftLightProMode"),
                onCheckedChange = { onSettingChanged("enableSoftLightProMode", it) }
            )
        }

        // 其他功能
        SettingsCard(title = stringResource(R.string.camera_settings_category_other)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_heif_live_photo_title),
                description = stringResource(R.string.camera_settings_heif_live_photo_desc),
                checked = vendorTagSettings.enableHeifLivePhoto,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableHeifLivePhoto"),
                onCheckedChange = { onSettingChanged("enableHeifLivePhoto", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_10bit_live_photo_title),
                description = stringResource(R.string.camera_settings_10bit_live_photo_desc),
                checked = vendorTagSettings.enable10bitLivePhoto,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enable10bitLivePhoto"),
                onCheckedChange = { onSettingChanged("enable10bitLivePhoto", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_live_photo_fov_title),
                description = stringResource(R.string.camera_settings_live_photo_fov_desc),
                checked = vendorTagSettings.enableLivePhotoFovOptimize,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableLivePhotoFovOptimize"),
                onCheckedChange = { onSettingChanged("enableLivePhotoFovOptimize", it) }
            )

        }

        // HEIF/HDR设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_heif_hdr)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_heif_blur_edit_title),
                description = stringResource(R.string.camera_settings_heif_blur_edit_desc),
                checked = vendorTagSettings.enableHeifBlurEdit,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableHeifBlurEdit"),
                onCheckedChange = { onSettingChanged("enableHeifBlurEdit", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_10bit_photo_title),
                description = stringResource(R.string.camera_settings_10bit_photo_desc),
                checked = vendorTagSettings.enable10bitPhoto,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enable10bitPhoto"),
                onCheckedChange = { onSettingChanged("enable10bitPhoto", it) }
            )
        }
        
        // 哈苏相关设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_hasselblad)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_filter_title),
                description = stringResource(R.string.camera_settings_master_filter_desc),
                checked = vendorTagSettings.enableMasterFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMasterFilter"),
                onCheckedChange = { onSettingChanged("enableMasterFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_hasselblad_watermark_guide_title),
                description = stringResource(R.string.camera_settings_hasselblad_watermark_guide_desc),
                checked = vendorTagSettings.enableHasselbladWatermarkGuide,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableHasselbladWatermarkGuide"),
                onCheckedChange = { onSettingChanged("enableHasselbladWatermarkGuide", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_hasselblad_watermark_title),
                description = stringResource(R.string.camera_settings_hasselblad_watermark_desc),
                checked = vendorTagSettings.enableHasselbladWatermark,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableHasselbladWatermark"),
                onCheckedChange = { onSettingChanged("enableHasselbladWatermark", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_hasselblad_watermark_default_title),
                description = stringResource(R.string.camera_settings_hasselblad_watermark_default_desc),
                checked = vendorTagSettings.enableHasselbladWatermarkDefault,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableHasselbladWatermarkDefault"),
                onCheckedChange = { onSettingChanged("enableHasselbladWatermarkDefault", it) }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * 设置卡片组件
 * 接收标题和内容，渲染为一个独立的卡片
 */
@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            content()
        }
    }
} 