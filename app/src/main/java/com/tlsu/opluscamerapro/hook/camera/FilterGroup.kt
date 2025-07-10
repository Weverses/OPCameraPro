package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV15
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
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
                "sbIsBrandOplusR",
                false
            )
            XposedHelpers.setStaticBooleanField(
                clazz,
                "sbIsExport",
                false
            )


//            loadClass("com.oplus.ocs.camera.ipusdk.processunit.filter.list.SystemUtil")
//                .methodFinder()
//                .filterByName("isMarketNameContainSeriesNum")
//                .single()
//                .createHook {
//                    returnConstant(true)
//            }

            // 获取VendorTag设置
            val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()
            if (isV15() && (vendorTags.enableTolStyleFilter || vendorTags.enableJzkMovieFilter || vendorTags.enableMasterFilter)) {
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

                            // 根据开关控制是否增加大师滤镜
                            if (vendorTags.enableMasterFilter) {
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
                                XposedHelpers.setStaticIntField(clazz, "sMasterFilterSize", 3)
                            }

                            if (vendorTags.enableJzkMovieFilter) {
                                invokeAddFrontAndBack(
                                    filterGroup,
                                    "jzk-movie.cube.rgb.bin",
                                    "R.string.camera_filter_type_director_joint_filter_JZK"
                                )
                            }
                            if (vendorTags.enableTolStyleFilter) {
                                invokeAddFrontAndBack(
                                    filterGroup,
                                    "tone-of-light.cube.rgb.bin",
                                    "R.string.tol_filter_type"
                                )
                            }
                            // V15.0.1后才有sFujiFilterSize
                            if (isV1501()) {
                                XposedHelpers.setStaticIntField(clazz, "sFujiFilterSize", 3)
                            }
//                        // 根据开关控制是否增加Grand Tour滤镜
//                        if (vendorTags.enableGrandTourFilter) {
//                            // 4个国际版专属的滤镜
//                            invokeAddFrontAndBack(
//                                filterGroup,
//                                "gt-lake.cube.rgb.bin",
//                                "R.string.camera_filter_gt_blue_wave"
//                            )
//                            invokeAddFrontAndBack(
//                                filterGroup,
//                                "gt-japan.cube.rgb.bin",
//                                "R.string.camera_filter_gt_clean"
//                            )
//                            invokeAddFrontAndBack(
//                                filterGroup,
//                                "gt-earth.cube.rgb.bin",
//                                "R.string.camera_filter_gt_classic"
//                            )
//                            invokeAddFrontAndBack(
//                                filterGroup,
//                                "gt-rosy.cube.rgb.bin",
//                                "R.string.camera_filter_gt_rosy"
//                            )
//                        }
                        }
                    }
                )
            }

            if (isV1501() && vendorTags.enableStyleEffect) {
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