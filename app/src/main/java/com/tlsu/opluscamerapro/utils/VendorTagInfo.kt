package com.tlsu.opluscamerapro.utils

/**
 * VendorTag信息类
 * 表示相机VendorTag的配置信息
 */
data class VendorTagInfo(
    val vendorTag: String,    // VendorTag名称
    val type: String,         // 类型 (Byte, Int32, Float, String等)
    val count: String,        // 数量
    val value: String         // 值
) 