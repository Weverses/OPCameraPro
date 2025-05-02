package com.tlsu.opluscamerapro.ui.screens.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tlsu.opluscamerapro.data.OtherSettings

/**
 * 其他设置组
 */
@Composable
fun OtherSettingsGroup(
    otherSettings: OtherSettings,
    onSettingChanged: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "其他设置",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )
        
        // 示例设置卡片（当有实际设置时可以添加多个卡片）
        SettingsCard(title = "其他功能") {
            // 这里可以添加其他设置项
            Text(
                text = "暂无其他设置",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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