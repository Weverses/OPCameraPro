package com.tlsu.opluscamerapro.hook.gallery

import android.content.res.XModuleResources
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.tlsu.opluscamerapro.hook.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.InputStream
import java.io.FileNotFoundException

object GRWatermark : BaseHook() {
    private var lpparam: XC_LoadPackage.LoadPackageParam? = null
    private val moduleResources: XModuleResources? by lazy {
        MODULE_PATH?.let { XModuleResources.createInstance(it, null) }
    }

    private val fileNameMappings = mapOf(
        "watermark_master_styles/personalize_retroCamera_1.json" to "gr_style_1.json",
        "watermark_master_styles/personalize_retroCamera_2.json" to "gr_style_2.json",
        "watermark_master_styles/personalize_retroCamera_3.json" to "gr_style_3.json",
        "watermark_master_styles/personalize_retroCamera_4.json" to "gr_style_4.json",
        "watermark_master_styles/personalize_retroCamera_5.json" to "gr_style_5.json"
    )

    var configName = ""

    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }

    override fun init() {
        val currentLpparam = lpparam ?: run {
            XposedBridge.log("OPCameraPro[GRFilter]: lpparam is null, skipping init.")
            return
        }
        // Check MODULE_PATH early
        if (MODULE_PATH == null) {
            XposedBridge.log("OPCameraPro[GRFilter]: MODULE_PATH is null, skipping init.")
            return
        }

        XposedBridge.log("OPCameraPro[GRFilter]: Initializing AssetManager hook for ${currentLpparam.packageName}")

        val hookCallback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val fileName = param.args[0] as? String ?: return

                val replacementFileName = fileNameMappings[fileName]
                if (replacementFileName != null) {
                    try {
                        // Use the lazily initialized moduleResources
                        val modRes = moduleResources ?: run {
                            XposedBridge.log("OPCameraPro[GRFilter]: moduleResources is null, cannot replace.")
                            return
                        }

                        val replacementStream: InputStream = modRes.assets.open(replacementFileName)

                        param.result = replacementStream
                        XposedBridge.log("OPCameraPro[GRFilter]: Replaced '$fileName' with module's '$replacementFileName'")

                    } catch (e: FileNotFoundException) {
                        XposedBridge.log("OPCameraPro[GRFilter]: Replacement file '$replacementFileName' not found in module assets for target '$fileName'. Error: ${e.message}")
                    } catch (e: Throwable) {
                        XposedBridge.log("OPCameraPro[GRFilter]: Failed to replace '$fileName' with '$replacementFileName'. Error: ${e.message}")
                        XposedBridge.log(e)
                    }
                }
            }
        }

        try {
            XposedHelpers.findAndHookMethod(
                "android.content.res.AssetManager",
                safeClassLoader,
                "open",
                String::class.java,
                hookCallback
            )

            loadClass("com.oplus.gallery.framework.abilities.watermark.masterstyle.WatermarkMasterStyle\$Companion")
                .methodFinder().filterByName("isRealmeGRFrame").single().createHook {
                    before { param ->
                        configName = param.args[0] as String
                    }
                    after {
                        if (configName.contains("personalize_retroCamera_")) {
                            returnConstant(true)
                        }
                    }
                }
            XposedBridge.log("OPCameraPro[GRFilter]: Successfully hooked AssetManager.open methods.")

        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro[GRFilter]: Failed to hook AssetManager.open: ${e.message}")
            XposedBridge.log(e)
        }
    }
}

object GRFilterHook : BaseHook() {
    override fun init() {
        GRWatermark.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.coloros.gallery3d") {
            GRWatermark.setLoadPackageParam(lpparam)
            init()
        }
    }
}