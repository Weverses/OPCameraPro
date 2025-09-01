package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object ProtobufFeature: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam

    override fun init() {
        try {
            val FEATURE_TABLE_CLASS = "com.oplus.ocs.camera.configure.ProtobufFeatureConfig\$FeatureTable"
            val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()

            XposedHelpers.findAndHookMethod(FEATURE_TABLE_CLASS,
                safeClassLoader,
                "parseFrom",
                ByteArray::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val originalConfig = param.result ?: return
                        XposedBridge.log("OPCameraPro: [Protobuf] Intercepted original config object.")

                        try {
                            val builder = XposedHelpers.callMethod(originalConfig, "toBuilder")

                            if (vendorTags.enableStyleEffect) {
                                addFilterToProfessionalMode(builder)
                            }

                            if (vendorTags.enableMasterModeLivePhoto) {
                                addLivePhotoToProfessionalMode(builder)
                            }

                            param.result = XposedHelpers.callMethod(builder, "build")
                            XposedBridge.log("OPCameraPro: [Protobuf] Config modification complete!")

                        } catch (e: Throwable) {
                            XposedBridge.log("OPCameraPro: [Protobuf] Failed to modify proto object: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: [Protobuf] Failed to set up Application.onCreate hook: ${e.message}")
        }
    }

    private fun addFilterToProfessionalMode(featureTableBuilder: Any) {
        try {
            val strPool = getStringListField(featureTableBuilder, "strPool_")
            if (strPool.isEmpty()) return

            val rearMainIndex = strPool.indexOf("rear_main")
            val rearWideIndex = strPool.indexOf("rear_wide")
            val rearTeleIndex = strPool.indexOf("rear_tele")

            val filterNameIndex = strPool.indexOf("com.oplus.camera.feature.filter")
            val filterKeyIndex = strPool.indexOf("feature_filter_index")
            val filterRangeIndex = strPool.indexOf("[0~100]")
            val filterDefaultIndex = strPool.indexOf("0")
            val entryTypeIndex = strPool.indexOf("main_menu,other_app,video_other_app,quick_launch,watch,gimbal")
            val valueTypeIndex = strPool.indexOf("int")

            val requiredIndices = listOf(rearMainIndex, rearWideIndex, rearTeleIndex, filterNameIndex, filterKeyIndex, filterRangeIndex, filterDefaultIndex, entryTypeIndex, valueTypeIndex)
            if (requiredIndices.any { it == -1 }) {
                XposedBridge.log("OPCameraPro: [Protobuf] Skipping 'Filter to ProMode': required indices not found.")
                return
            }

            val cameraFeatureTableBuilder = XposedHelpers.callMethod(featureTableBuilder, "getCameraFeatureTableBuilder")
            val modeFeatureTablesMap = XposedHelpers.callMethod(cameraFeatureTableBuilder, "getMutableModeFeatureTables") as MutableMap<String, Any>
            val professionalModeTable = modeFeatureTablesMap["professional_mode"] ?: return
            val professionalModeTableBuilder = XposedHelpers.callMethod(professionalModeTable, "toBuilder")
            val cameraTypeFeatureTablesMap = XposedHelpers.callMethod(professionalModeTableBuilder, "getMutableCameraTypeFeatureTables") as MutableMap<Int, Any>
            val rearMainTable = cameraTypeFeatureTablesMap[rearMainIndex] ?: return
            val rearWideTable = cameraTypeFeatureTablesMap[rearWideIndex] ?: return
            val rearTeleTable = cameraTypeFeatureTablesMap[rearTeleIndex] ?: return
            val rearMainTableBuilder = XposedHelpers.callMethod(rearMainTable, "toBuilder")
            val rearWideTableBuilder = XposedHelpers.callMethod(rearWideTable, "toBuilder")
            val rearTeleTableBuilder = XposedHelpers.callMethod(rearTeleTable, "toBuilder")

            val newFilterFeature = createNewFeature(
                nameIndex = filterNameIndex,
                keyIndex = filterKeyIndex,
                rangeIndex = filterRangeIndex,
                defaultIndex = filterDefaultIndex,
                entryTypeIndex = entryTypeIndex,
                valueTypeIndex = valueTypeIndex
            )

            XposedHelpers.callMethod(rearMainTableBuilder, "addFeatureList", newFilterFeature)
            XposedHelpers.callMethod(rearWideTableBuilder, "addFeatureList", newFilterFeature)
            XposedHelpers.callMethod(rearTeleTableBuilder, "addFeatureList", newFilterFeature)

            cameraTypeFeatureTablesMap[rearMainIndex] = XposedHelpers.callMethod(rearMainTableBuilder, "build")
            cameraTypeFeatureTablesMap[rearWideIndex] = XposedHelpers.callMethod(rearWideTableBuilder, "build")
            cameraTypeFeatureTablesMap[rearTeleIndex] = XposedHelpers.callMethod(rearTeleTableBuilder, "build")
            modeFeatureTablesMap["professional_mode"] = XposedHelpers.callMethod(professionalModeTableBuilder, "build")

            XposedBridge.log("OPCameraPro: [Protobuf] Successfully added Filter to Professional Mode.")
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: [Protobuf] Error in addFilterToProfessionalMode: ${e.message}")
        }
    }

    private fun addLivePhotoToProfessionalMode(featureTableBuilder: Any) {
        try {
            val strPool = getStringListField(featureTableBuilder, "strPool_")
            if (strPool.isEmpty()) return

            val rearMainIndex = strPool.indexOf("rear_main")
            val rearWideIndex = strPool.indexOf("rear_wide")
            val rearTeleIndex = strPool.indexOf("rear_tele")

            val filterNameIndex = strPool.indexOf("com.oplus.camera.feature.live_photo")
            val filterKeyIndex = strPool.indexOf("com.oplus.camera.feature.live_photo")
            val filterRangeIndex = strPool.indexOf("")
            val filterDefaultIndex = strPool.indexOf("")
            val entryTypeIndex = strPool.indexOf("main_menu")
            val valueTypeIndex = strPool.indexOf("string")

            val requiredIndices = listOf(rearMainIndex, rearWideIndex, rearTeleIndex, filterNameIndex, filterKeyIndex, filterRangeIndex, filterDefaultIndex, entryTypeIndex, valueTypeIndex)
            if (requiredIndices.any { it == -1 }) {
                XposedBridge.log("OPCameraPro: [Protobuf] Skipping 'Filter to ProMode': required indices not found.")
                return
            }

            val cameraFeatureTableBuilder = XposedHelpers.callMethod(featureTableBuilder, "getCameraFeatureTableBuilder")
            val modeFeatureTablesMap = XposedHelpers.callMethod(cameraFeatureTableBuilder, "getMutableModeFeatureTables") as MutableMap<String, Any>
            val professionalModeTable = modeFeatureTablesMap["professional_mode"] ?: return
            val professionalModeTableBuilder = XposedHelpers.callMethod(professionalModeTable, "toBuilder")
            val cameraTypeFeatureTablesMap = XposedHelpers.callMethod(professionalModeTableBuilder, "getMutableCameraTypeFeatureTables") as MutableMap<Int, Any>
            val rearMainTable = cameraTypeFeatureTablesMap[rearMainIndex] ?: return
            val rearWideTable = cameraTypeFeatureTablesMap[rearWideIndex] ?: return
            val rearTeleTable = cameraTypeFeatureTablesMap[rearTeleIndex] ?: return
            val rearMainTableBuilder = XposedHelpers.callMethod(rearMainTable, "toBuilder")
            val rearWideTableBuilder = XposedHelpers.callMethod(rearWideTable, "toBuilder")
            val rearTeleTableBuilder = XposedHelpers.callMethod(rearTeleTable, "toBuilder")

            val newFilterFeature = createNewFeature(
                nameIndex = filterNameIndex,
                keyIndex = filterKeyIndex,
                rangeIndex = filterRangeIndex,
                defaultIndex = filterDefaultIndex,
                entryTypeIndex = entryTypeIndex,
                valueTypeIndex = valueTypeIndex
            )

            XposedHelpers.callMethod(rearMainTableBuilder, "addFeatureList", newFilterFeature)
            XposedHelpers.callMethod(rearWideTableBuilder, "addFeatureList", newFilterFeature)
            XposedHelpers.callMethod(rearTeleTableBuilder, "addFeatureList", newFilterFeature)

            cameraTypeFeatureTablesMap[rearMainIndex] = XposedHelpers.callMethod(rearMainTableBuilder, "build")
            cameraTypeFeatureTablesMap[rearWideIndex] = XposedHelpers.callMethod(rearWideTableBuilder, "build")
            cameraTypeFeatureTablesMap[rearTeleIndex] = XposedHelpers.callMethod(rearTeleTableBuilder, "build")
            modeFeatureTablesMap["professional_mode"] = XposedHelpers.callMethod(professionalModeTableBuilder, "build")

            XposedBridge.log("OPCameraPro: [Protobuf] Successfully added Filter to Professional Mode.")
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: [Protobuf] Error in addFilterToProfessionalMode: ${e.message}")
        }
    }

    private fun createNewFeature(nameIndex: Int, keyIndex: Int, rangeIndex: Int, defaultIndex: Int, entryTypeIndex: Int, valueTypeIndex: Int): Any {
        val FEATURE_CLASS = "com.oplus.ocs.camera.configure.ProtobufFeatureConfig\$Feature"
        val featureClass = XposedHelpers.findClass(FEATURE_CLASS, lpparam.classLoader)
        val newFeatureBuilder = XposedHelpers.callStaticMethod(featureClass, "newBuilder")

        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureNameIndex", nameIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureKeyNameIndex", keyIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureValueRangeIndex", rangeIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureDefaultValueIndex", defaultIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setEntryTypeIndex", entryTypeIndex)
        XposedHelpers.callMethod(newFeatureBuilder, "setFeatureValueTypeIndex", valueTypeIndex)

        return XposedHelpers.callMethod(newFeatureBuilder, "build")
    }

    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }

    private fun getStringListField(obj: Any, fieldName: String): List<String> {
        val field = XposedHelpers.findField(obj::class.java, fieldName)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(obj) as List<String>
    }

    private fun MutableList<String>.addIfNotExists(element: String): Int {
        val index = this.indexOf(element)
        if (index != -1) {
            return index
        }
        this.add(element)
        return this.size - 1
    }
}

object ProtobufFeatureHook: BaseHook() {
    override fun init() {
        ProtobufFeature.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            ProtobufFeature.setLoadPackageParam(lpparam)
            init()
        }
    }
}