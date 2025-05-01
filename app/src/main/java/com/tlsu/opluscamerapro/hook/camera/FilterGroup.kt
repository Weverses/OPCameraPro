package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
import com.tlsu.opluscamerapro.utils.ParseConfig.isOplusCameraConfig
import com.tlsu.opluscamerapro.utils.ParseConfig.parseConfig
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


object FilterGroup : BaseHook() {
    override fun init() {
        try {
            val clazz = loadClass("com.oplus.ocs.camera.ipusdk.processunit.filter.list.FilterGroupManager")
            val filterClazz = loadClass("com.oplus.ocs.camera.ipusdk.processunit.filter.list.FilterGroup")

            XposedHelpers.setStaticBooleanField(
                clazz,
                "sbIsSupportJzkMovieFilter",
                true
            )
            XposedHelpers.setStaticBooleanField(
                clazz,
                "sbIsBrandOplusR",
                true
            )
            XposedHelpers.setStaticBooleanField(
                clazz,
                "sbIsExport",
                true
            )

            loadClass("com.oplus.ocs.camera.ipusdk.processunit.filter.list.SystemUtil")
                .methodFinder()
                .filterByName("isMarketNameContainSeriesNum")
                .single()
                .createHook {
                    returnConstant(true)
            }
            clazz
                .methodFinder()
                .filterByName("isDirectorEnableByRUS")
                .single()
                .createHook {
                    returnConstant(true)
            }


            XposedHelpers.findAndHookMethod(
                clazz,
                "initOS15FilmFilterGroup",
                filterClazz,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        // 获得对应的filterGroup
                        val filterGroup = param.args[0]
                        // 保留原有的滤镜
                        invokeAddFrontAndBack(
                            filterGroup,
                            "fuji_cc.bin",
                            "R.string.camera_filter_oplus_qing_xin"
                        )
                        invokeAddFrontAndBack(
                            filterGroup,
                            "fuji-nc.bin",
                            "R.string.camera_filter_oplus_fu_gu"
                        )
                        invokeAddFrontAndBack(
                            filterGroup,
                            "fuji-proNegHi.bin",
                            "R.string.camera_filter_oplus_tong_tou"
                        )
                        // 增加大师滤镜
                        invokeAddFrontAndBack(
                            filterGroup,
                            "Serenity.cube.rgb.bin",
                            "R.string.camera_filter_oplus_master_serenity"
                        )
                        invokeAddFrontAndBack(
                            filterGroup,
                            "Radiance.cube.rgb.bin",
                            "R.string.camera_filter_oplus_master_radiance"
                        )
                        invokeAddFrontAndBack(
                            filterGroup,
                            "Emerald.cube.rgb.bin",
                            "R.string.camera_filter_oplus_master_emerald"
                        )
                        // V15.0.1后才有sFujiFilterSize
                        if (isV1501()) {
                            XposedHelpers.setStaticIntField(clazz, "sFujiFilterSize", 3)
                        }
                        XposedHelpers.setStaticIntField(clazz, "sMasterFilterSize", 3)
                        // 4个国际版专属的滤镜
                        invokeAddFrontAndBack(
                            filterGroup,
                            "gt-lake.cube.rgb.bin",
                            "R.string.camera_filter_gt_blue_wave"
                        )
                        invokeAddFrontAndBack(
                            filterGroup,
                            "gt-japan.cube.rgb.bin",
                            "R.string.camera_filter_gt_clean"
                        )
                        invokeAddFrontAndBack(
                            filterGroup,
                            "gt-earth.cube.rgb.bin",
                            "R.string.camera_filter_gt_classic"
                        )
                        invokeAddFrontAndBack(
                            filterGroup,
                            "gt-rosy.cube.rgb.bin",
                            "R.string.camera_filter_gt_rosy"
                        )
                    }
                }
            )
            XposedHelpers.findAndHookMethod(
                clazz,
                "initProFilterGroup",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam) {
                        // 获取sFilterGroup静态字段值
                        val sFilterGroup =
                            XposedHelpers.getStaticObjectField(clazz, "sFilterGroup")

                        // 将sProFilterGroup设置为sFilterGroup
                        XposedHelpers.setStaticObjectField(
                            clazz,
                            "sProFilterGroup",
                            sFilterGroup
                        )
                    }
                }
            )
            var configName = ""
            loadClass("com.oplus.ocs.camera.consumer.apsAdapter.update.UpdateHelper")
                .methodFinder()
                .filterByName("getValidConfigData")
                .single()
                .createHook {
                    before { param ->
                        configName = param.args[1] as String
                        XposedBridge.log("OplusTest: configName: $configName")
                    }
                    after {
                        val originalConfig = it.result as String
                        if (isOplusCameraConfig(configName)) {
                            XposedBridge.log("OplusTest: isOplusCameraConfig!")
                            val modifyConfig = parseConfig(originalConfig)
                            XposedBridge.log("OplusTest: get modifyConfig successfully")
                            it.result = modifyConfig
                            XposedBridge.log("OplusTest: return modifyConfig successfully")
                        }
                    }
                }
            XposedBridge.log("OplusTest: hook camera successfully")
        } catch (e: Throwable) {
            XposedBridge.log("OplusTest: hook camera failed!")
            XposedBridge.log(e)
        }
    }

    private fun invokeAddFrontAndBack(filterGroup: Any, bin: String, res: String) {
        XposedHelpers.callMethod(filterGroup, "addFrontAndBack", bin, res)
    }
}