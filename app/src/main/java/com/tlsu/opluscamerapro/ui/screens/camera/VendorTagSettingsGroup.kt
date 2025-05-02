package com.tlsu.opluscamerapro.ui.screens.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.data.VendorTagSettings
import com.tlsu.opluscamerapro.ui.components.SettingsSwitchItem

/**
 * VendorTag设置组
 */
@Composable
fun VendorTagSettingsGroup(
    vendorTagSettings: VendorTagSettings,
    onSettingChanged: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "VendorTag设置",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        
        // 高级拍照设置
        SettingsCard(title = "高级拍照设置") {
            SettingsSwitchItem(
                title = "25MP超高清拍照",
                description = "启用25MP Turbo RAW增强分辨率",
                checked = vendorTagSettings.enable25MP,
                onCheckedChange = { onSettingChanged("enable25MP", it) }
            )
            
            SettingsSwitchItem(
                title = "大师模式",
                description = "启用大师模式2.0",
                checked = vendorTagSettings.enableMasterMode,
                onCheckedChange = { onSettingChanged("enableMasterMode", it) }
            )
            
            SettingsSwitchItem(
                title = "大师模式 RAW MAX",
                description = "启用大师模式RAW MAX格式",
                checked = vendorTagSettings.enableMasterRawMax,
                onCheckedChange = { onSettingChanged("enableMasterRawMax", it) }
            )
            
            SettingsSwitchItem(
                title = "人像模式变焦",
                description = "启用人像模式变焦功能",
                checked = vendorTagSettings.enablePortraitZoom,
                onCheckedChange = { onSettingChanged("enablePortraitZoom", it) }
            )
        }
        
        // 视频设置
        SettingsCard(title = "视频设置") {
            SettingsSwitchItem(
                title = "720P 60FPS视频",
                description = "启用720P 60帧视频录制",
                checked = vendorTagSettings.enable720p60fps,
                onCheckedChange = { onSettingChanged("enable720p60fps", it) }
            )
            
            SettingsSwitchItem(
                title = "慢动作视频超广角480FPS",
                description = "启用慢动作视频超广角480帧录制",
                checked = vendorTagSettings.enableSlowVideo480fps,
                onCheckedChange = { onSettingChanged("enableSlowVideo480fps", it) }
            )
            
            SettingsSwitchItem(
                title = "视频自动帧率",
                description = "启用视频自动帧率功能",
                checked = vendorTagSettings.enableVideoAutoFps,
                onCheckedChange = { onSettingChanged("enableVideoAutoFps", it) }
            )
            
            SettingsSwitchItem(
                title = "实况视频高码率",
                description = "提高实况视频码率",
                checked = vendorTagSettings.enableLivePhotoHighBitrate,
                onCheckedChange = { onSettingChanged("enableLivePhotoHighBitrate", it) }
            )
            
            SettingsSwitchItem(
                title = "录制结束立即播放提示音",
                description = "停止录制时立即播放提示音",
                checked = vendorTagSettings.enableVideoStopSoundImmediate,
                onCheckedChange = { onSettingChanged("enableVideoStopSoundImmediate", it) }
            )
        }
        
        // 微距设置
        SettingsCard(title = "微距设置") {
            SettingsSwitchItem(
                title = "新版微距模式",
                description = "启用新版微距模式",
                checked = vendorTagSettings.enableNewMacroMode,
                onCheckedChange = { onSettingChanged("enableNewMacroMode", it) }
            )
            
            SettingsSwitchItem(
                title = "微距模式调用长焦",
                description = "允许微距模式调用长焦相机",
                checked = vendorTagSettings.enableMacroTele,
                onCheckedChange = { onSettingChanged("enableMacroTele", it) }
            )
            
            SettingsSwitchItem(
                title = "微距景深融合",
                description = "启用微距景深融合功能",
                checked = vendorTagSettings.enableMacroDepthFusion,
                onCheckedChange = { onSettingChanged("enableMacroDepthFusion", it) }
            )
        }
        
        // 滤镜设置
        SettingsCard(title = "滤镜设置") {
            SettingsSwitchItem(
                title = "大师模式滤镜参数预设",
                description = "启用大师模式滤镜参数预设",
                checked = vendorTagSettings.enableStyleEffect,
                onCheckedChange = { onSettingChanged("enableStyleEffect", it) }
            )
            
            SettingsSwitchItem(
                title = "光影有声滤镜",
                description = "启用光影有声滤镜",
                checked = vendorTagSettings.enableTolStyleFilter,
                onCheckedChange = { onSettingChanged("enableTolStyleFilter", it) }
            )
            
            SettingsSwitchItem(
                title = "Grand Tour滤镜",
                description = "启用Grand Tour系列滤镜",
                checked = vendorTagSettings.enableGrandTourFilter,
                onCheckedChange = { onSettingChanged("enableGrandTourFilter", it) }
            )
            
            SettingsSwitchItem(
                title = "沙漠系列滤镜",
                description = "启用沙漠系列滤镜",
                checked = vendorTagSettings.enableDesertFilter,
                onCheckedChange = { onSettingChanged("enableDesertFilter", it) }
            )
            
            SettingsSwitchItem(
                title = "Vignette Grain滤镜",
                description = "启用Vignette Grain滤镜",
                checked = vendorTagSettings.enableVignetteGrainFilter,
                onCheckedChange = { onSettingChanged("enableVignetteGrainFilter", it) }
            )
            
            SettingsSwitchItem(
                title = "Director滤镜",
                description = "启用Director滤镜",
                checked = vendorTagSettings.enableDirectorFilter,
                onCheckedChange = { onSettingChanged("enableDirectorFilter", it) }
            )
            
            SettingsSwitchItem(
                title = "贾樟柯滤镜",
                description = "启用贾樟柯滤镜",
                checked = vendorTagSettings.enableJzkMovieFilter,
                onCheckedChange = { onSettingChanged("enableJzkMovieFilter", it) }
            )
            
            SettingsSwitchItem(
                title = "Meishe系列滤镜",
                description = "启用Meishe系列滤镜",
                checked = vendorTagSettings.enableMeisheFilter,
                onCheckedChange = { onSettingChanged("enableMeisheFilter", it) }
            )
            
            SettingsSwitchItem(
                title = "柔光滤镜(照片模式)",
                description = "在照片模式中启用柔光滤镜",
                checked = vendorTagSettings.enableSoftLightPhotoMode,
                onCheckedChange = { onSettingChanged("enableSoftLightPhotoMode", it) }
            )
            
            SettingsSwitchItem(
                title = "柔光滤镜(夜景模式)",
                description = "在夜景模式中启用柔光滤镜",
                checked = vendorTagSettings.enableSoftLightNightMode,
                onCheckedChange = { onSettingChanged("enableSoftLightNightMode", it) }
            )
            
            SettingsSwitchItem(
                title = "柔光滤镜(大师模式)",
                description = "在大师模式中启用柔光滤镜",
                checked = vendorTagSettings.enableSoftLightProMode,
                onCheckedChange = { onSettingChanged("enableSoftLightProMode", it) }
            )
        }
        
        // HEIF/HDR设置
        SettingsCard(title = "HEIF/HDR设置") {
            SettingsSwitchItem(
                title = "HEIF模式编辑背景虚化",
                description = "在HEIF模式下支持相册编辑背景虚化",
                checked = vendorTagSettings.enableHeifBlurEdit,
                onCheckedChange = { onSettingChanged("enableHeifBlurEdit", it) }
            )
            
            SettingsSwitchItem(
                title = "10bit照片",
                description = "启用10bit照片拍摄",
                checked = vendorTagSettings.enable10bitPhoto,
                onCheckedChange = { onSettingChanged("enable10bitPhoto", it) }
            )
            
            SettingsSwitchItem(
                title = "实况HEIF照片",
                description = "启用实况HEIF照片格式",
                checked = vendorTagSettings.enableHeifLivePhoto,
                onCheckedChange = { onSettingChanged("enableHeifLivePhoto", it) }
            )
            
            SettingsSwitchItem(
                title = "实况10bit照片",
                description = "启用实况10bit照片格式",
                checked = vendorTagSettings.enable10bitLivePhoto,
                onCheckedChange = { onSettingChanged("enable10bitLivePhoto", it) }
            )
            
            SettingsSwitchItem(
                title = "实况照片FOV优化",
                description = "优化实况照片视场角",
                checked = vendorTagSettings.enableLivePhotoFovOptimize,
                onCheckedChange = { onSettingChanged("enableLivePhotoFovOptimize", it) }
            )
            
            SettingsSwitchItem(
                title = "预览HDR",
                description = "启用预览HDR功能",
                checked = vendorTagSettings.enablePreviewHdr,
                onCheckedChange = { onSettingChanged("enablePreviewHdr", it) }
            )
        }
        
        // 其他功能
        SettingsCard(title = "其他功能") {
            SettingsSwitchItem(
                title = "大师模式放大对焦",
                description = "在大师模式中启用放大对焦功能",
                checked = vendorTagSettings.enableScaleFocus,
                onCheckedChange = { onSettingChanged("enableScaleFocus", it) }
            )
            
            SettingsSwitchItem(
                title = "新版美颜菜单",
                description = "启用新版美颜菜单",
                checked = vendorTagSettings.enableNewBeautyMenu,
                onCheckedChange = { onSettingChanged("enableNewBeautyMenu", it) }
            )
            
            SettingsSwitchItem(
                title = "超级文本扫描",
                description = "启用超级文本扫描功能",
                checked = vendorTagSettings.enableSuperTextScanner,
                onCheckedChange = { onSettingChanged("enableSuperTextScanner", it) }
            )
            
            SettingsSwitchItem(
                title = "双击音量键快捷启动相机",
                description = "启用双击音量键快捷启动相机功能",
                checked = vendorTagSettings.enableQuickLaunch,
                onCheckedChange = { onSettingChanged("enableQuickLaunch", it) }
            )
            
            SettingsSwitchItem(
                title = "第三方APP调用人像模式",
                description = "第三方APP调用官方相机时可以选择人像模式",
                checked = vendorTagSettings.enableForcePortraitForThirdParty,
                onCheckedChange = { onSettingChanged("enableForcePortraitForThirdParty", it) }
            )
            
            SettingsSwitchItem(
                title = "前置拍照变焦",
                description = "启用前置相机变焦功能",
                checked = vendorTagSettings.enableFrontCameraZoom,
                onCheckedChange = { onSettingChanged("enableFrontCameraZoom", it) }
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