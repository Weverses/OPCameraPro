package com.tlsu.opluscamerapro.utils
import com.tlsu.opluscamerapro.utils.AddConfig.addConfig
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
        // 清除之前的预设标签，确保使用最新配置
        clearPresetTags()
        
        val jsonArray = JSONArray(originalJson)
        val tagIndexMap = buildTagIndexMap(jsonArray)
        // 添加Config
        ConfigBasedAddConfig.addConfig()
        presetTags.forEach { newTag ->
            tagIndexMap[newTag.vendorTag]?.let { index ->
                // 覆盖现有条目
                jsonArray.put(index, createJsonObject(newTag))
            } ?: run {
                // 添加新条目
                jsonArray.put(createJsonObject(newTag))
            }
        }

        return jsonArray.toString(4)
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