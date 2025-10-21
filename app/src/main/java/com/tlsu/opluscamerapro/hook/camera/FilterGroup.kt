package com.tlsu.opluscamerapro.hook.camera

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV15
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV16
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
            if (isV15()){
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
                            // GR
                            if (vendorTags.enableGRFilter) {
                                invokeAddFrontAndBack(
                                    filterGroup,
                                    "gr.posi.rgba.bin",
                                    "R.string.camera_gr_filter_posi"
                                )
                                invokeAddFrontAndBack(
                                    filterGroup,
                                    "gr.nega.rgba.bin",
                                    "R.string.camera_gr_filter_nega"
                                )
                                invokeAddFrontAndBack(
                                    filterGroup,
                                    "gr.bw.rgba.bin",
                                    "R.string.camera_gr_filter_bw"
                                )
                                invokeAddFrontAndBack(
                                    filterGroup,
                                    "gr.hi.bw.rgba.bin",
                                    "R.string.camera_gr_filter_hi_bw"
                                )
                            }
                        }
                    }
                )
            }

            if (isV1501() && vendorTags.unlockFilterInMasterMode) {
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
            if (isV16() && vendorTags.enableXPAN) {
                XposedHelpers.findAndHookMethod(
                    clazz,
                    "initHasselbladXpanFilterGroup",
                    object : XC_MethodReplacement() {
                        @Throws(Throwable::class)
                        override fun replaceHookedMethod(param: MethodHookParam) {
                            val sFilterGroup =
                                XposedHelpers.getStaticObjectField(clazz, "sHasselbladXpanFilterGroup")
                            invokeAddBack(
                                sFilterGroup,
                                "default",
                                "R.string.camera_filter_none"
                            )
                            invokeAddBack(
                                sFilterGroup,
                                "Delta400.3dl.rgb.bin",
                                "R.string.camera_filter_oplus_soft"
                            )
                            invokeAddBack(
                                sFilterGroup,
                                "fuji_cc.bin",
                                "R.string.camera_filter_oplus_qing_xin"
                            )
                            invokeAddBack(
                                sFilterGroup,
                                "fuji-nc.bin",
                                "R.string.camera_filter_oplus_fu_gu"
                            )
                            invokeAddBack(
                                sFilterGroup,
                                "fuji-proNegHi.bin",
                                "R.string.camera_filter_oplus_tong_tou"
                            )
//                        invokeAddBack(
//                            sFilterGroup,
//                            "800t.bin",
//                            "R.string.camera_filter_oplus_neon"
//                        )
                        }
                    }
                )
            }
            XposedBridge.log("OPCameraPro: hook FilterGroup successfully")
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: hook FilterGroup failed!")
            XposedBridge.log(e)
        }
    }

    private fun invokeAddFrontAndBack(filterGroup: Any, bin: String, res: String) {
        XposedHelpers.callMethod(filterGroup, "addFrontAndBack", bin, res)
    }

    private fun invokeAddBack(filterGroup: Any, bin: String, res: String) {
        XposedHelpers.callMethod(filterGroup, "addBack", bin, res)
    }
}