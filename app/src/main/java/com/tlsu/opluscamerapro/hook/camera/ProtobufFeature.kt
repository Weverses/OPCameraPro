package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object ProtobufFeature : BaseHook() {

    // 数据类：用于清晰地定义一个要添加的特性
    private data class FeatureInfo(
        val name: String,
        val key: String,
        val range: String = "",
        val default: String = "",
        val entryType: String,
        val valueType: String
    )

    // 数据类：用于定义一个完整的修改操作
    private data class FeatureModification(
        val logName: String,
        val targetMode: String,
        val targetCameraTypes: List<String>,
        val featuresToAdd: List<FeatureInfo>
    )

    override fun init() {
        val FEATURE_TABLE_CLASS = "com.oplus.ocs.camera.configure.ProtobufFeatureConfig\$FeatureTable"
        try {
            XposedHelpers.findAndHookMethod(
                FEATURE_TABLE_CLASS,
                safeClassLoader,
                "parseFrom",
                ByteArray::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val originalConfig = param.result ?: return
                        XposedBridge.log("OPCameraPro [Protobuf]:  Intercepted original config. Starting modifications.")

                        try {
                            val builder = XposedHelpers.callMethod(originalConfig, "toBuilder")
                            val strPool = getStringListField(builder, "strPool_")
                            if (strPool.isEmpty()) {
                                XposedBridge.log("OPCameraPro [Protobuf]:  strPool is empty, aborting.")
                                return
                            }

                            val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()

                            if (vendorTags.enableStyleEffect) {
                                applyModification(builder, strPool, getFilterToProModeModification())
                            }

                            if (vendorTags.enableMasterModeLivePhoto) {
                                applyModification(builder, strPool, getLivePhotoToProModeModification())
                            }

                            if (vendorTags.enablePortraitRearFlash) {
                                applyModification(builder, strPool, getFlashToPortraitModeModification())
                            }

                            param.result = XposedHelpers.callMethod(builder, "build")
                            XposedBridge.log("OPCameraPro [Protobuf]:  Config modification process complete!")

                        } catch (e: Throwable) {
                            XposedBridge.log("OPCameraPro [Protobuf]:  Failed to modify proto object.")
                        }
                    }
                })
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro [Protobuf]:  Failed to hook FeatureTable.parseFrom.")
        }
    }

    private fun getFilterToProModeModification() = FeatureModification(
        logName = "Filter to ProMode",
        targetMode = "professional_mode",
        targetCameraTypes = listOf("rear_main", "rear_wide", "rear_tele"),
        featuresToAdd = listOf(
            FeatureInfo(
                name = "com.oplus.camera.feature.filter",
                key = "feature_filter_index",
                range = "[0~100]",
                default = "0",
                entryType = "main_menu,other_app,video_other_app,quick_launch,watch,gimbal",
                valueType = "int"
            )
        )
    )

    private fun getLivePhotoToProModeModification() = FeatureModification(
        logName = "LivePhoto to ProMode",
        targetMode = "professional_mode",
        targetCameraTypes = listOf("rear_main", "rear_wide", "rear_tele"),
        featuresToAdd = listOf(
            FeatureInfo(
                name = "com.oplus.camera.feature.live_photo",
                key = "com.oplus.camera.feature.live_photo",
                entryType = "main_menu",
                valueType = "string"
            )
        )
    )

    private fun getFlashToPortraitModeModification() = FeatureModification(
        logName = "Flash to PortraitMode",
        targetMode = "portrait_mode",
        targetCameraTypes = listOf("rear_sat", "rear_portrait"),
        featuresToAdd = listOf(
            FeatureInfo(
                name = "com.oplus.preview.flash.mode",
                key = "com.oplus.preview.flash.mode",
                entryType = "main_menu,other_app,video_other_app,quick_launch,watch,gimbal",
                valueType = "int"
            ),
            FeatureInfo(
                name = "com.oplus.preview.flash.mode",
                key = "pref_camera_flashmode_key",
                range = "[on,off,torch,auto]",
                default = "off",
                entryType = "main_menu,other_app,video_other_app,quick_launch,watch,gimbal",
                valueType = "string"
            )
        )
    )

    // --- 通用逻辑实现 ---

    /**
     * 应用一个指定的修改任务
     */
    private fun applyModification(featureTableBuilder: Any, strPool: List<String>, mod: FeatureModification) {
        try {
            val cameraFeatureTableBuilder = XposedHelpers.callMethod(featureTableBuilder, "getCameraFeatureTableBuilder")
            val modeFeatureTablesMap = XposedHelpers.callMethod(cameraFeatureTableBuilder, "getMutableModeFeatureTables") as MutableMap<String, Any>

            val modeBuilder = getOrCreateModeBuilder(modeFeatureTablesMap, mod.targetMode) ?: return
            val cameraTypeTablesMap = XposedHelpers.callMethod(modeBuilder, "getMutableCameraTypeFeatureTables") as MutableMap<Int, Any>

            var changesMade = false
            for (cameraType in mod.targetCameraTypes) {
                if (addFeaturesToCameraType(cameraType, strPool, cameraTypeTablesMap, mod.featuresToAdd)) {
                    changesMade = true
                }
            }

            if (changesMade) {
                modeFeatureTablesMap[mod.targetMode] = XposedHelpers.callMethod(modeBuilder, "build")
                XposedBridge.log("OPCameraPro [Protobuf]:  Successfully applied modification: '${mod.logName}'.")
            }
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro [Protobuf]:  Error applying modification '${mod.logName}'.")
        }
    }

    /**
     * 为单个相机类型添加一系列特性
     */
    private fun addFeaturesToCameraType(
        cameraTypeName: String,
        strPool: List<String>,
        cameraTypeTablesMap: MutableMap<Int, Any>,
        featuresToAdd: List<FeatureInfo>
    ): Boolean {
        val cameraTypeIndex = strPool.indexOf(cameraTypeName)
        if (cameraTypeIndex == -1) {
            return false
        }

        val cameraTypeTable = cameraTypeTablesMap[cameraTypeIndex] ?: run {
            XposedBridge.log("OPCameraPro [Protobuf]:  Warning: Table for '$cameraTypeName' (index $cameraTypeIndex) not found.")
            return false
        }

        val cameraTypeBuilder = XposedHelpers.callMethod(cameraTypeTable, "toBuilder")
        var featureAdded = false
        for (featureInfo in featuresToAdd) {
            createNewFeature(strPool, featureInfo)?.let {
                XposedHelpers.callMethod(cameraTypeBuilder, "addFeatureList", it)
                featureAdded = true
            }
        }

        if (featureAdded) {
            cameraTypeTablesMap[cameraTypeIndex] = XposedHelpers.callMethod(cameraTypeBuilder, "build")
        }
        return featureAdded
    }

    /**
     * 获取或创建指定模式的 Builder
     */
    private fun getOrCreateModeBuilder(modeFeatureTablesMap: MutableMap<String, Any>, modeName: String): Any? {
        return modeFeatureTablesMap[modeName]?.let {
            XposedHelpers.callMethod(it, "toBuilder")
        } ?: run {
            XposedBridge.log("OPCameraPro [Protobuf]:  Warning: Mode table for '$modeName' not found.")
            null
        }
    }

    /**
     * 从 FeatureInfo 创建一个 Protobuf Feature 对象
     */
    private fun createNewFeature(strPool: List<String>, info: FeatureInfo): Any? {
        fun getIndex(value: String): Int {
            if (value.isEmpty()) return strPool.indexOf("")
            return strPool.indexOf(value)
        }

        val nameIndex = getIndex(info.name)
        val keyIndex = getIndex(info.key)
        val rangeIndex = getIndex(info.range)
        val defaultIndex = getIndex(info.default)
        val entryTypeIndex = getIndex(info.entryType)
        val valueTypeIndex = getIndex(info.valueType)

        val requiredIndices = listOf(nameIndex, keyIndex, entryTypeIndex, valueTypeIndex)
        if (requiredIndices.any { it == -1 }) {
            XposedBridge.log("OPCameraPro [Protobuf]:  Skipping feature '${info.name}': A required string was not found in strPool.")
            return null
        }

        val featureClass = XposedHelpers.findClass("com.oplus.ocs.camera.configure.ProtobufFeatureConfig\$Feature", safeClassLoader)
        val newFeatureBuilder = XposedHelpers.callStaticMethod(featureClass, "newBuilder")

        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureNameIndex", nameIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureKeyNameIndex", keyIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureValueRangeIndex", rangeIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureDefaultValueIndex", defaultIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setEntryTypeIndex", entryTypeIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureValueTypeIndex", valueTypeIndex)

        return XposedHelpers.callMethod(newFeatureBuilder, "build")
    }

    /**
     * 通过反射获取字符串列表字段
     */
    private fun getStringListField(obj: Any, fieldName: String): List<String> {
        return try {
            val field = XposedHelpers.findField(obj::class.java, fieldName)
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            field.get(obj) as? List<String> ?: emptyList()
        } catch (e: NoSuchFieldException) {
            XposedBridge.log("OPCameraPro [Protobuf]:  Field '$fieldName' not found in ${obj::class.java.name}.")
            emptyList()
        }
    }
}

// BaseHook 的入口保持不变
object ProtobufFeatureHook: BaseHook() {
    override fun init() {
        // 由于现在是单例，init() 只需要被调用一次
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            // 直接初始化，不再需要传递 lpparam
            ProtobufFeature.init()
        }
    }
}