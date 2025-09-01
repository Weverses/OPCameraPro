package com.tlsu.opluscamerapro.ui.screens.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.data.VendorTagSettings
import com.tlsu.opluscamerapro.ui.components.SettingsSwitchItem
import com.tlsu.opluscamerapro.utils.DefaultConfigManager
import com.tlsu.opluscamerapro.utils.DeviceCheck.execWithResult
import com.tlsu.opluscamerapro.utils.DeviceCheck.isNewCameraVer
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501

/**
 * VendorTag设置组
 */
@Composable
fun VendorTagSettingsGroup(
    vendorTagSettings: VendorTagSettings,
    onSettingChanged: (String, Boolean) -> Unit,
    onBitrateChanged: (Int) -> Unit = {},
    onAiHdZoomValueChanged: (Int) -> Unit = {},
    onTeleSdsrZoomValueChanged: (Int) -> Unit = {},
    onDurationChanged: (Int, Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    
    // 状态变量控制码率输入对话框
    var showBitrateDialog by remember { mutableStateOf(false) }
    var bitrateValue by remember { mutableStateOf(vendorTagSettings.livePhotoBitrate.toString()) }
    var bitrateError by remember { mutableStateOf<String?>(null) }
    
    // 状态变量控制实况照片时长输入对话框
    var showDurationDialog by remember { mutableStateOf(false) }
    var maxDurationValue by remember { mutableStateOf(vendorTagSettings.livePhotoMaxDuration.toString()) }
    var minDurationValue by remember { mutableStateOf(vendorTagSettings.livePhotoMinDuration.toString()) }
    var maxDurationError by remember { mutableStateOf<String?>(null) }
    var minDurationError by remember { mutableStateOf<String?>(null) }
    
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
    
    // 实况照片时长对话框
    if (showDurationDialog) {
        AlertDialog(
            onDismissRequest = { showDurationDialog = false },
            title = { Text("设置实况照片时长") },
            text = {
                Column {
                    Text("请输入实况照片的时长设置（毫秒）：")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 最大时长输入框
                    OutlinedTextField(
                        value = maxDurationValue,
                        onValueChange = { 
                            maxDurationValue = it
                            // 验证输入
                            maxDurationError = try {
                                val value = it.toInt()
                                if (value < 10) {
                                    "最大时长不能小于10毫秒"
                                } else if (value > 100000) {
                                    "最大时长不能大于100000毫秒"
                                } else {
                                    null
                                }
                            } catch (e: NumberFormatException) {
                                "请输入有效的数字"
                            }
                        },
                        isError = maxDurationError != null,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("最大时长 (毫秒)") },
                        singleLine = true
                    )
                    if (maxDurationError != null) {
                        Text(
                            text = maxDurationError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 最小时长输入框
                    OutlinedTextField(
                        value = minDurationValue,
                        onValueChange = { 
                            minDurationValue = it
                            // 验证输入
                            minDurationError = try {
                                val value = it.toInt()
                                if (value < 100) {
                                    "最小时长不能小于100毫秒"
                                } else if (value > 1000) {
                                    "最小时长不能大于1000毫秒"
                                } else {
                                    null
                                }
                            } catch (e: NumberFormatException) {
                                "请输入有效的数字"
                            }
                        },
                        isError = minDurationError != null,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("最小时长 (毫秒)") },
                        singleLine = true
                    )
                    if (minDurationError != null) {
                        Text(
                            text = minDurationError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "当前值: 最大${vendorTagSettings.livePhotoMaxDuration}毫秒，最小${vendorTagSettings.livePhotoMinDuration}毫秒",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "提示: 调整实况照片的视频时长，影响回放时间和文件大小",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val newMaxDuration = maxDurationValue.toInt()
                            val newMinDuration = minDurationValue.toInt()
                            
                            // 检查最大值大于最小值
                            if (newMaxDuration <= newMinDuration) {
                                maxDurationError = "最大值必须大于最小值"
                                return@Button
                            }
                            
                            if (maxDurationError == null && minDurationError == null) {
                                val maxDurationChanged = newMaxDuration != vendorTagSettings.livePhotoMaxDuration
                                val minDurationChanged = newMinDuration != vendorTagSettings.livePhotoMinDuration
                                
                                if (maxDurationChanged || minDurationChanged) {
                                    onDurationChanged(newMaxDuration, newMinDuration)
                                }
                            }
                            showDurationDialog = false
                        } catch (e: NumberFormatException) {
                            maxDurationError = "请输入有效的数字"
                            minDurationError = "请输入有效的数字"
                        }
                    },
                    enabled = maxDurationError == null && minDurationError == null
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDurationDialog = false }) {
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
                                if (value < 3) {
                                    "倍率不能小于3"
                                } else if (value > 120) {
                                    "倍率不能大于120"
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
                                if (value < 3) {
                                    "倍率不能小于3"
                                } else if (value > 120) {
                                    "倍率不能大于120"
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
            text = stringResource(R.string.camera_settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        
        // 拍照设置

        if (isNewCameraVer(45) && !isV1501()) {
            HintCard(title = stringResource(R.string.unsupport_camera_app_version)) {
            }
        }

        SettingsCard(title = stringResource(R.string.camera_settings_category_advanced)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_25mp_title),
                description = stringResource(R.string.camera_settings_25mp_desc),
                checked = vendorTagSettings.enable25MP,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enable25MP"),
                onCheckedChange = { onSettingChanged("enable25MP", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_enable_ai_scene_preset),
                description = stringResource(R.string.camera_settings_enable_ai_scene_preset_desc),
                checked = vendorTagSettings.enableAiScenePreset,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableAiScenePreset"),
                onCheckedChange = { onSettingChanged("enableAiScenePreset", it) }
            )

            if (execWithResult("md5sum /odm/lib64/libAlgoInterface.so")
                        .out.joinToString("").contains("f723969a47ac1806769d1e90de77124b")
                || execWithResult("md5sum /odm/lib64/libAlgoInterface.so")
                    .out.joinToString("").contains("31bfee2af2c77acdffff374c28cde2d0")) {
                SettingsSwitchItem(
                    title = stringResource(R.string.camera_settings_preview_hdr_title),
                    description = stringResource(R.string.camera_settings_preview_hdr_desc),
                    checked = vendorTagSettings.enablePreviewHdr,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                        context,
                        "enablePreviewHdr"
                    ),

                    onCheckedChange = { onSettingChanged("enablePreviewHdr", it) }
                )
            }

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_new_beauty_menu_title),
                description = stringResource(R.string.camera_settings_new_beauty_menu_desc),
                checked = vendorTagSettings.enableNewBeautyMenu,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableNewBeautyMenu"),
                onCheckedChange = { onSettingChanged("enableNewBeautyMenu", it) }
            )

//            SettingsSwitchItem(
//                title = stringResource(R.string.camera_settings_super_text_scanner_title),
//                description = stringResource(R.string.camera_settings_super_text_scanner_desc),
//                checked = vendorTagSettings.enableSuperTextScanner,
//                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSuperTextScanner"),
//                onCheckedChange = { onSettingChanged("enableSuperTextScanner", it) }
//            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_quick_launch_title),
                description = stringResource(R.string.camera_settings_quick_launch_desc),
                checked = vendorTagSettings.enableQuickLaunch,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableQuickLaunch"),
                onCheckedChange = { onSettingChanged("enableQuickLaunch", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_front_camera_zoom_title),
                description = stringResource(R.string.camera_settings_front_camera_zoom_desc),
                checked = vendorTagSettings.enableFrontCameraZoom,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableFrontCameraZoom"),
                onCheckedChange = { onSettingChanged("enableFrontCameraZoom", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_global_ev_title),
                description = stringResource(R.string.camera_settings_global_ev_desc),
                checked = vendorTagSettings.enableGlobalEv,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableGlobalEv"),
                onCheckedChange = { onSettingChanged("enableGlobalEv", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_switch_lens_focal_length_title),
                description = stringResource(R.string.camera_settings_switch_lens_focal_length_desc),
                checked = vendorTagSettings.enableSwitchLensFocalLength,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSwitchLensFocalLength"),
                onCheckedChange = { onSettingChanged("enableSwitchLensFocalLength", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_motion_capture_title),
                description = stringResource(R.string.camera_settings_motion_capture_desc),
                checked = vendorTagSettings.enableMotionCapture,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMotionCapture"),
                onCheckedChange = { onSettingChanged("enableMotionCapture", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_multi_frame_burst_shot_title),
                description = stringResource(R.string.camera_settings_multi_frame_burst_shot_desc),
                checked = vendorTagSettings.enableMultiFrameBurstShot,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMultiFrameBurstShot"),
                onCheckedChange = { onSettingChanged("enableMultiFrameBurstShot", it) }
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
        
        // 大师模式设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_master)) {

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_mode_title),
                description = stringResource(R.string.camera_settings_master_mode_desc),
                checked = vendorTagSettings.enableMasterMode,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                    context,
                    "enableStyleEffect"
                ),
                onCheckedChange = { onSettingChanged("enableMasterMode", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_raw_max_title),
                description = stringResource(R.string.camera_settings_master_raw_max_desc),
                checked = vendorTagSettings.enableMasterRawMax,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMasterRawMax"),
                onCheckedChange = { onSettingChanged("enableMasterRawMax", it) }
            )

            if (isNewCameraVer(45)) {
                SettingsSwitchItem(
                    title = stringResource(R.string.camera_settings_scale_focus_title),
                    description = stringResource(R.string.camera_settings_scale_focus_desc),
                    checked = vendorTagSettings.enableScaleFocus,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableScaleFocus"),
                    onCheckedChange = { onSettingChanged("enableScaleFocus", it) }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.camera_settings_style_effect_title),
                    description = stringResource(R.string.camera_settings_style_effect_desc),
                    checked = vendorTagSettings.enableStyleEffect,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                        context,
                        "enableStyleEffect"
                    ),
                    onCheckedChange = { onSettingChanged("enableStyleEffect", it) }
                )
            }

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_soft_light_pro_title),
                description = stringResource(R.string.camera_settings_soft_light_pro_desc),
                checked = vendorTagSettings.enableSoftLightProMode,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableSoftLightProMode"),
                onCheckedChange = { onSettingChanged("enableSoftLightProMode", it) }
            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_iso_extension_title),
                description = stringResource(R.string.camera_settings_iso_extension_desc),
                checked = vendorTagSettings.enableISOExtension,
                onCheckedChange = { onSettingChanged("enableISOExtension", it) }
            )

        }
        
        // 人像模式设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_portrait)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_portrait_zoom_title),
                description = stringResource(R.string.camera_settings_portrait_zoom_desc),
                checked = vendorTagSettings.enablePortraitZoom,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enablePortraitZoom"),
                onCheckedChange = { onSettingChanged("enablePortraitZoom", it) }
            )

            if (isNewCameraVer(46)) {
                SettingsSwitchItem(
                    title = stringResource(R.string.camera_settings_portrait_rear_flash_title),
                    description = stringResource(R.string.camera_settings_portrait_rear_flash_desc),
                    checked = vendorTagSettings.enablePortraitRearFlash,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                        context,
                        "enablePortraitRearFlash"
                    ),
                    onCheckedChange = { onSettingChanged("enablePortraitRearFlash", it) }
                )
            }
            
//            SettingsSwitchItem(
//                title = stringResource(R.string.camera_settings_force_portrait_title),
//                description = stringResource(R.string.camera_settings_force_portrait_desc),
//                checked = vendorTagSettings.enableForcePortraitForThirdParty,
//                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableForcePortraitForThirdParty"),
//                onCheckedChange = { onSettingChanged("enableForcePortraitForThirdParty", it) }
//            )
        }

        // 滤镜设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_filter)) {
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
                title = stringResource(R.string.camera_settings_os15_new_filter_title),
                description = stringResource(R.string.camera_settings_os15_new_filter_desc),
                checked = vendorTagSettings.enableOs15NewFilter,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableOs15NewFilter"),
                onCheckedChange = { onSettingChanged("enableOs15NewFilter", it) }
            )

            if (isNewCameraVer(52)) {
                SettingsSwitchItem(
                    title = stringResource(R.string.camera_setiings_ccd_filter_title),
                    description = stringResource(R.string.camera_setiings_ccd_filter_desc),
                    checked = vendorTagSettings.enableFlashFilter,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                        context,
                        "enableFlashFilter"
                    ),
                    onCheckedChange = { onSettingChanged("enableFlashFilter", it) }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.camera_setiings_softlight_filter_title),
                    description = stringResource(R.string.camera_setiings_softlight_filter_desc),
                    checked = vendorTagSettings.enableSoftLightFilter,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                        context,
                        "enableSoftLightFilter"
                    ),
                    onCheckedChange = { onSettingChanged("enableSoftLightFilter", it) }
                )
            }
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
                title = stringResource(R.string.camera_settings_enable_front_4k_video),
                description = stringResource(R.string.camera_settings_enable_front_4k_video_desc),
                checked = vendorTagSettings.enableFront4KVideo,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableFront4KVideo"),
                onCheckedChange = { onSettingChanged("enableFront4KVideo", it) }
            )

            if (isNewCameraVer(46)) {
                SettingsSwitchItem(
                    title = stringResource(R.string.camera_settings_1080p_120fps_video_title),
                    description = stringResource(R.string.camera_settings_1080p_120fps_video_desc),
                    checked = vendorTagSettings.enable1080p120fpsVideo,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                        context,
                        "enable1080p120fpsVideo"
                    ),
                    onCheckedChange = { onSettingChanged("enable1080p120fpsVideo", it) }
                )
            }
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_4k_120fps_video_title),
                description = stringResource(R.string.camera_settings_4k_120fps_video_desc),
                checked = vendorTagSettings.enable4K120fpsVideo,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enable4K120fpsVideo"),
                onCheckedChange = { onSettingChanged("enable4K120fpsVideo", it) }
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
            
//            SettingsSwitchItem(
//                title = stringResource(R.string.camera_settings_video_lock_wb_title),
//                description = stringResource(R.string.camera_settings_video_lock_wb_desc),
//                checked = vendorTagSettings.enableVideoLockWb,
//                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableVideoLockWb"),
//                onCheckedChange = { onSettingChanged("enableVideoLockWb", it) }
//            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_video_sound_focus_title),
                description = stringResource(R.string.camera_settings_video_sound_focus_desc),
                checked = vendorTagSettings.enableVideoSoundFocus,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableVideoSoundFocus"),
                onCheckedChange = { onSettingChanged("enableVideoSoundFocus", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_mic_status_check_title),
                description = stringResource(R.string.camera_settings_mic_status_check_desc),
                checked = vendorTagSettings.enableMicStatusCheck,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableMicStatusCheck"),
                onCheckedChange = { onSettingChanged("enableMicStatusCheck", it) }
            )
        }
        
        // 杜比视频设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_dolby)) {
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
                title = stringResource(R.string.camera_settings_dolby_video_120fps_title),
                description = stringResource(R.string.camera_settings_dolby_video_120fps_desc),
                checked = vendorTagSettings.enableDolbyVideo120fps,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableDolbyVideo120fps"),
                onCheckedChange = { onSettingChanged("enableDolbyVideo120fps", it) }
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

        // 实况照片设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_other)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_live_photo_title),
                description = stringResource(R.string.camera_settings_live_photo_desc),
                checked = vendorTagSettings.enableLivePhoto,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableLivePhoto"),
                onCheckedChange = { onSettingChanged("enableLivePhoto", it) }
            )

            if (isNewCameraVer(46)) {
                SettingsSwitchItem(
                    title = stringResource(R.string.camera_setiings_live_photo_mastermode_title),
                    description = stringResource(R.string.camera_setiings_live_photo_mastermode_desc),
                    checked = vendorTagSettings.enableMasterModeLivePhoto,
                    defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(
                        context,
                        "enableMasterModeLivePhoto"
                    ),
                    onCheckedChange = { onSettingChanged("enableMasterModeLivePhoto", it) }
                )
            }
//            SettingsSwitchItem(
//                title = stringResource(R.string.camera_settings_heif_live_photo_title),
//                description = stringResource(R.string.camera_settings_heif_live_photo_desc),
//                checked = vendorTagSettings.enableHeifLivePhoto,
//                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableHeifLivePhoto"),
//                onCheckedChange = { onSettingChanged("enableHeifLivePhoto", it) }
//            )
//
//            SettingsSwitchItem(
//                title = stringResource(R.string.camera_settings_10bit_live_photo_title),
//                description = stringResource(R.string.camera_settings_10bit_live_photo_desc),
//                checked = vendorTagSettings.enable10bitLivePhoto,
//                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enable10bitLivePhoto"),
//                onCheckedChange = { onSettingChanged("enable10bitLivePhoto", it) }
//            )

            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_live_photo_fov_title),
                description = stringResource(R.string.camera_settings_live_photo_fov_desc),
                checked = vendorTagSettings.enableLivePhotoFovOptimize,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableLivePhotoFovOptimize"),
                onCheckedChange = { onSettingChanged("enableLivePhotoFovOptimize", it) }
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
            
            // 实况照片时长调整
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_live_photo_duration_title),
                description = "${stringResource(R.string.camera_settings_live_photo_duration_desc)} (最大: ${vendorTagSettings.livePhotoMaxDuration}毫秒, 最小: ${vendorTagSettings.livePhotoMinDuration}毫秒)",
                checked = true,
                defaultValueDescription = "默认值：最大3200毫秒，最小500毫秒",
                onCheckedChange = { 
                    // 点击直接显示调整对话框，不像码率那样需要先开启
                    maxDurationValue = vendorTagSettings.livePhotoMaxDuration.toString()
                    minDurationValue = vendorTagSettings.livePhotoMinDuration.toString()
                    maxDurationError = null
                    minDurationError = null
                    showDurationDialog = true
                }
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
                title = stringResource(R.string.camera_settings_hasselblad_watermark_title),
                description = stringResource(R.string.camera_settings_hasselblad_watermark_desc),
                checked = vendorTagSettings.enableHasselbladWatermark,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableHasselbladWatermark"),
                onCheckedChange = { onSettingChanged("enableHasselbladWatermark", it) }
            )
            SettingsSwitchItem(
                title = stringResource(R.string.camera_setiings_xpan_title),
                description = stringResource(R.string.camera_setiings_xpan_desc),
                checked = vendorTagSettings.enableXPAN,
                defaultValueDescription = DefaultConfigManager.getDefaultValueDescription(context, "enableXPAN"),
                onCheckedChange = { onSettingChanged("enableXPAN", it) }
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

/**
 * 设置Hint组件
 */
@Composable
private fun HintCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        // 设置卡片的颜色
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffc3172f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 内容部分的字体大小需要在调用时定义
            content()
        }
    }
}