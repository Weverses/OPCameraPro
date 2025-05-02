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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.R
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
                onCheckedChange = { onSettingChanged("enable25MP", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_mode_title),
                description = stringResource(R.string.camera_settings_master_mode_desc),
                checked = vendorTagSettings.enableMasterMode,
                onCheckedChange = { onSettingChanged("enableMasterMode", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_master_raw_max_title),
                description = stringResource(R.string.camera_settings_master_raw_max_desc),
                checked = vendorTagSettings.enableMasterRawMax,
                onCheckedChange = { onSettingChanged("enableMasterRawMax", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_portrait_zoom_title),
                description = stringResource(R.string.camera_settings_portrait_zoom_desc),
                checked = vendorTagSettings.enablePortraitZoom,
                onCheckedChange = { onSettingChanged("enablePortraitZoom", it) }
            )
        }
        
        // 视频设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_video)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_720p_60fps_title),
                description = stringResource(R.string.camera_settings_720p_60fps_desc),
                checked = vendorTagSettings.enable720p60fps,
                onCheckedChange = { onSettingChanged("enable720p60fps", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_slow_video_480fps_title),
                description = stringResource(R.string.camera_settings_slow_video_480fps_desc),
                checked = vendorTagSettings.enableSlowVideo480fps,
                onCheckedChange = { onSettingChanged("enableSlowVideo480fps", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_video_auto_fps_title),
                description = stringResource(R.string.camera_settings_video_auto_fps_desc),
                checked = vendorTagSettings.enableVideoAutoFps,
                onCheckedChange = { onSettingChanged("enableVideoAutoFps", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_live_photo_bitrate_title),
                description = stringResource(R.string.camera_settings_live_photo_bitrate_desc),
                checked = vendorTagSettings.enableLivePhotoHighBitrate,
                onCheckedChange = { onSettingChanged("enableLivePhotoHighBitrate", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_video_stop_sound_title),
                description = stringResource(R.string.camera_settings_video_stop_sound_desc),
                checked = vendorTagSettings.enableVideoStopSoundImmediate,
                onCheckedChange = { onSettingChanged("enableVideoStopSoundImmediate", it) }
            )
        }
        
        // 微距设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_macro)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_new_macro_mode_title),
                description = stringResource(R.string.camera_settings_new_macro_mode_desc),
                checked = vendorTagSettings.enableNewMacroMode,
                onCheckedChange = { onSettingChanged("enableNewMacroMode", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_macro_tele_title),
                description = stringResource(R.string.camera_settings_macro_tele_desc),
                checked = vendorTagSettings.enableMacroTele,
                onCheckedChange = { onSettingChanged("enableMacroTele", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_macro_depth_fusion_title),
                description = stringResource(R.string.camera_settings_macro_depth_fusion_desc),
                checked = vendorTagSettings.enableMacroDepthFusion,
                onCheckedChange = { onSettingChanged("enableMacroDepthFusion", it) }
            )
        }
        
        // 滤镜设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_filters)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_style_effect_title),
                description = stringResource(R.string.camera_settings_style_effect_desc),
                checked = vendorTagSettings.enableStyleEffect,
                onCheckedChange = { onSettingChanged("enableStyleEffect", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_tol_style_filter_title),
                description = stringResource(R.string.camera_settings_tol_style_filter_desc),
                checked = vendorTagSettings.enableTolStyleFilter,
                onCheckedChange = { onSettingChanged("enableTolStyleFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_grand_tour_filter_title),
                description = stringResource(R.string.camera_settings_grand_tour_filter_desc),
                checked = vendorTagSettings.enableGrandTourFilter,
                onCheckedChange = { onSettingChanged("enableGrandTourFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_desert_filter_title),
                description = stringResource(R.string.camera_settings_desert_filter_desc),
                checked = vendorTagSettings.enableDesertFilter,
                onCheckedChange = { onSettingChanged("enableDesertFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_vignette_grain_filter_title),
                description = stringResource(R.string.camera_settings_vignette_grain_filter_desc),
                checked = vendorTagSettings.enableVignetteGrainFilter,
                onCheckedChange = { onSettingChanged("enableVignetteGrainFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_director_filter_title),
                description = stringResource(R.string.camera_settings_director_filter_desc),
                checked = vendorTagSettings.enableDirectorFilter,
                onCheckedChange = { onSettingChanged("enableDirectorFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_jzk_movie_filter_title),
                description = stringResource(R.string.camera_settings_jzk_movie_filter_desc),
                checked = vendorTagSettings.enableJzkMovieFilter,
                onCheckedChange = { onSettingChanged("enableJzkMovieFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_meishe_filter_title),
                description = stringResource(R.string.camera_settings_meishe_filter_desc),
                checked = vendorTagSettings.enableMeisheFilter,
                onCheckedChange = { onSettingChanged("enableMeisheFilter", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_soft_light_photo_title),
                description = stringResource(R.string.camera_settings_soft_light_photo_desc),
                checked = vendorTagSettings.enableSoftLightPhotoMode,
                onCheckedChange = { onSettingChanged("enableSoftLightPhotoMode", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_soft_light_night_title),
                description = stringResource(R.string.camera_settings_soft_light_night_desc),
                checked = vendorTagSettings.enableSoftLightNightMode,
                onCheckedChange = { onSettingChanged("enableSoftLightNightMode", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_soft_light_pro_title),
                description = stringResource(R.string.camera_settings_soft_light_pro_desc),
                checked = vendorTagSettings.enableSoftLightProMode,
                onCheckedChange = { onSettingChanged("enableSoftLightProMode", it) }
            )
        }
        
        // HEIF/HDR设置
        SettingsCard(title = stringResource(R.string.camera_settings_category_heif_hdr)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_heif_blur_edit_title),
                description = stringResource(R.string.camera_settings_heif_blur_edit_desc),
                checked = vendorTagSettings.enableHeifBlurEdit,
                onCheckedChange = { onSettingChanged("enableHeifBlurEdit", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_10bit_photo_title),
                description = stringResource(R.string.camera_settings_10bit_photo_desc),
                checked = vendorTagSettings.enable10bitPhoto,
                onCheckedChange = { onSettingChanged("enable10bitPhoto", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_heif_live_photo_title),
                description = stringResource(R.string.camera_settings_heif_live_photo_desc),
                checked = vendorTagSettings.enableHeifLivePhoto,
                onCheckedChange = { onSettingChanged("enableHeifLivePhoto", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_10bit_live_photo_title),
                description = stringResource(R.string.camera_settings_10bit_live_photo_desc),
                checked = vendorTagSettings.enable10bitLivePhoto,
                onCheckedChange = { onSettingChanged("enable10bitLivePhoto", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_live_photo_fov_title),
                description = stringResource(R.string.camera_settings_live_photo_fov_desc),
                checked = vendorTagSettings.enableLivePhotoFovOptimize,
                onCheckedChange = { onSettingChanged("enableLivePhotoFovOptimize", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_preview_hdr_title),
                description = stringResource(R.string.camera_settings_preview_hdr_desc),
                checked = vendorTagSettings.enablePreviewHdr,
                onCheckedChange = { onSettingChanged("enablePreviewHdr", it) }
            )
        }
        
        // 其他功能
        SettingsCard(title = stringResource(R.string.camera_settings_category_other)) {
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_scale_focus_title),
                description = stringResource(R.string.camera_settings_scale_focus_desc),
                checked = vendorTagSettings.enableScaleFocus,
                onCheckedChange = { onSettingChanged("enableScaleFocus", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_new_beauty_menu_title),
                description = stringResource(R.string.camera_settings_new_beauty_menu_desc),
                checked = vendorTagSettings.enableNewBeautyMenu,
                onCheckedChange = { onSettingChanged("enableNewBeautyMenu", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_super_text_scanner_title),
                description = stringResource(R.string.camera_settings_super_text_scanner_desc),
                checked = vendorTagSettings.enableSuperTextScanner,
                onCheckedChange = { onSettingChanged("enableSuperTextScanner", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_quick_launch_title),
                description = stringResource(R.string.camera_settings_quick_launch_desc),
                checked = vendorTagSettings.enableQuickLaunch,
                onCheckedChange = { onSettingChanged("enableQuickLaunch", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_force_portrait_title),
                description = stringResource(R.string.camera_settings_force_portrait_desc),
                checked = vendorTagSettings.enableForcePortraitForThirdParty,
                onCheckedChange = { onSettingChanged("enableForcePortraitForThirdParty", it) }
            )
            
            SettingsSwitchItem(
                title = stringResource(R.string.camera_settings_front_camera_zoom_title),
                description = stringResource(R.string.camera_settings_front_camera_zoom_desc),
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