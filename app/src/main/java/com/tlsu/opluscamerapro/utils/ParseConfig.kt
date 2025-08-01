package com.tlsu.opluscamerapro.utils

import de.robv.android.xposed.XposedBridge
import org.json.JSONArray
import org.json.JSONObject

enum class MergeStrategy {
    SKIP,     // 保留原有条目
    OVERRIDE  // 用新条目覆盖
}

object ParseConfig {
    private val presetTags = mutableListOf<VendorTagInfo>()

    /**
     * 添加预设配置（自动去重）
     * @param strategy 冲突解决策略，默认为覆盖
     */
    fun addPresetTag(info: VendorTagInfo, strategy: MergeStrategy = MergeStrategy.OVERRIDE) {
        val existingIndex = presetTags.indexOfFirst { it.vendorTag == info.vendorTag }
        when {
            existingIndex == -1 -> presetTags.add(info)
            strategy == MergeStrategy.OVERRIDE -> presetTags[existingIndex] = info
        }
    }
    
    /**
     * 清除之前的预设标签列表
     * 确保每次parseConfig调用时使用最新配置
     */
    private fun clearPresetTags() {
        presetTags.clear()
    }

    /**
     * 核心处理方法（支持覆盖逻辑）
     * @return 处理后的JSON字符串
     */
    fun parseConfig(originalJson: String): String {
        try {
            // 保存原始配置并解析默认值信息
            DefaultConfigManager.parseAndSaveDefaultConfig(originalJson)
            
            // 清除之前的预设标签，确保使用最新配置
            clearPresetTags()
            
            // 判断原始JSON格式
            val isObjectWithFileData = !originalJson.trim().startsWith("[") && 
                                      originalJson.contains("\"file_data\"")
            
            // 解析JSON
            val jsonArray: JSONArray
            var jsonObject: JSONObject? = null
            
            if (isObjectWithFileData) {
                // 格式2: {file_version:x, file_data:[...]}
                jsonObject = JSONObject(originalJson)
                jsonArray = jsonObject.getJSONArray("file_data")
            } else {
                // 格式1: 直接的数组
                jsonArray = JSONArray(originalJson)
            }
            
            val tagIndexMap = buildTagIndexMap(jsonArray)
            
            try {
                // 添加Config
                ConfigBasedAddConfig.addConfig()
            } catch (e: Exception) {
                XposedBridge.log("ParseConfig: Error adding config: ${e.message}")
            }
            
            presetTags.forEach { newTag ->
                tagIndexMap[newTag.vendorTag]?.let { index ->
                    // 覆盖现有条目
                    jsonArray.put(index, createJsonObject(newTag))
                } ?: run {
                    // 添加新条目
                    jsonArray.put(createJsonObject(newTag))
                }
            }

            // 根据原始格式返回
            return if (isObjectWithFileData && jsonObject != null) {
                // 将修改后的jsonArray放回原始对象结构中
                jsonObject.put("file_data", jsonArray)
                jsonObject.toString(4)
            } else {
                // 直接返回数组格式
                jsonArray.toString(4)
            }
        } catch (e: Exception) {
            XposedBridge.log("ParseConfig: Error in parseConfig: ${e.message}")
            e.printStackTrace()
            // 返回原始配置，避免破坏相机功能
            return originalJson
        }
    }


    // 构建VendorTag位置索引
    private fun buildTagIndexMap(jsonArray: JSONArray): MutableMap<String, Int> {
        return mutableMapOf<String, Int>().apply {
            for (i in 0 until jsonArray.length()) {
                jsonArray.getJSONObject(i).getString("VendorTag").let { tag ->
                    put(tag, i)
                }
            }
        }
    }

    // 创建JSON对象
    private fun createJsonObject(info: VendorTagInfo): JSONObject {
        return JSONObject().apply {
            put("VendorTag", info.vendorTag)
            put("Type", info.type)
            put("Count", info.count)
            put("Value", info.value)
        }
    }

    // 配置验证
    fun isOplusCameraConfig(configName: String): Boolean {
        return configName.contains("oplus_camera_config", ignoreCase = true)
    }

}