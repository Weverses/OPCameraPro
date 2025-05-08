package com.tlsu.opluscamerapro.ui.screens.gallery

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.R
import com.tlsu.opluscamerapro.data.GallerySettings
import com.tlsu.opluscamerapro.ui.components.SettingsSwitchItem

/**
 * 相册设置组
 */
@Composable
fun GallerySettingsGroup(
    gallerySettings: GallerySettings,
    onSettingChanged: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.gallery_settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        
        // AI功能设置
        SettingsCard(title = stringResource(R.string.gallery_settings_category_ai)) {
            SettingsSwitchItem(
                title = stringResource(R.string.gallery_settings_ai_composition_title),
                description = stringResource(R.string.gallery_settings_ai_composition_desc),
                checked = gallerySettings.enableAIComposition,
                defaultValueDescription = "相册无法获取设备默认值",
                onCheckedChange = { onSettingChanged("enableAIComposition", it) }
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