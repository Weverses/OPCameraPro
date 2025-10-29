package com.tlsu.opluscamerapro.hook.gallery

import android.content.res.XResForwarder
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.ConfigBasedAddConfig
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV16
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources

object DrawableRedirect : BaseHook() {
    private const val RESOURCE_TYPE = "drawable"
    private val logTag = "OPCameraPro[DrawableRedirect]"

    private const val SOURCE_CLASSIC_LOGO = "realme_gr_logo"
    private const val SOURCE_CLASSIC_LOGO_DARK = "realme_gr_logo_dark"
    private val TARGET_CLASSIC_LOGOS = listOf(
        "classic_135_camera",
        "classic_double_reverse1",
        "classic_double_reverse2",
        "classic_double_reverse3",
        "classic_double_reverse4"
    )
    private val TARGET_CLASSIC_LOGOS_DARK = listOf(
        "classic_135_camera_dark",
        "classic_double_reverse1_dark",
        "classic_double_reverse2_dark",
        "classic_double_reverse3_dark",
        "classic_double_reverse4_dark"
    )

    private const val SOURCE_SKETCH_PREFIX = "sketch_personalize_camera_retro_camera_"
    private const val TARGET_SKETCH_PREFIX = "sketch_realme_gr_"
    private const val LAND_SUFFIX = "_land"
    private const val LAND_SUFFIX2 = "land_"
    private val SKETCH_INDICES = 1..5 // Numbers 1 to 5
    // 获取配置
    val vendorTags = ConfigBasedAddConfig.getVendorTagSettings()

    fun initResourceHook(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (isV16() && vendorTags.enableGRWatermark) {
            XposedBridge.log("$logTag: Initializing resource hook for package ${resparam.packageName}")

            try {
                redirectDrawables(resparam, SOURCE_CLASSIC_LOGO, TARGET_CLASSIC_LOGOS)
                redirectDrawables(
                    resparam,
                    SOURCE_CLASSIC_LOGO_DARK,
                    TARGET_CLASSIC_LOGOS_DARK,
                    "_dark"
                ) // Add suffix for logging
            } catch (e: Throwable) {
                XposedBridge.log("$logTag: Error during classic logo redirection: ${e.message}")
                XposedBridge.log(e)
            }

            try {
                // Redirect normal sketch images (1 to 5)
                SKETCH_INDICES.forEach { index ->
                    val sourceName = "$SOURCE_SKETCH_PREFIX$index"
                    val targetName = "$TARGET_SKETCH_PREFIX$index"
                    redirectSingleDrawable(
                        resparam,
                        sourceToRedirect = sourceName,
                        targetToUse = targetName
                    )
                }

//            // Redirect landscape sketch images (1_land to 5_land)
//            SKETCH_INDICES.forEach { index ->
//                val sourceName = "$SOURCE_SKETCH_PREFIX$index$LAND_SUFFIX"
//                val targetName = "$TARGET_SKETCH_PREFIX$LAND_SUFFIX2$index"
//                redirectSingleDrawable(resparam, sourceToRedirect = sourceName, targetToUse = targetName)
//            }
            } catch (e: Throwable) {
                XposedBridge.log("$logTag: Error during sketch image redirection: ${e.message}")
                XposedBridge.log(e)
            }
            XposedBridge.log("$logTag: Finished applying redirect rules.")
        }
    }

    /**
     * Helper function to redirect a list of target drawables to a single source drawable.
     */
    private fun redirectDrawables(
        resparam: XC_InitPackageResources.InitPackageResourcesParam,
        sourceDrawableName: String,
        targetDrawableNames: List<String>,
        logSuffix: String = ""
    ) {
        val sourceResId = resparam.res.getIdentifier(sourceDrawableName, RESOURCE_TYPE, resparam.packageName)
        if (sourceResId == 0) {
            XposedBridge.log("$logTag: Source drawable '$sourceDrawableName' not found. Skipping redirection for targets${logSuffix}.")
            return
        }
        XposedBridge.log("$logTag: Found source drawable '$sourceDrawableName' with ID: $sourceResId")
        val resForwarder = XResForwarder(resparam.res, sourceResId)

        targetDrawableNames.forEach { targetName ->
            try {
                resparam.res.setReplacement(resparam.packageName, RESOURCE_TYPE, targetName, resForwarder)
                //XposedBridge.log("$logTag: Rule set: '$targetName' -> '$sourceDrawableName'") // Log can be verbose
            } catch (innerE: Throwable) {
                XposedBridge.log("$logTag: Failed to set replacement for '$targetName'${logSuffix}: ${innerE.message}")
            }
        }
        XposedBridge.log("$logTag: Applied rules for ${targetDrawableNames.size} targets${logSuffix} -> '$sourceDrawableName'")
    }

    /**
     * Helper function to redirect a single source drawable to a single target drawable.
     * Note: Here 'source' is what the app *requests*, 'target' is what we *provide*.
     */
    private fun redirectSingleDrawable(
        resparam: XC_InitPackageResources.InitPackageResourcesParam,
        sourceToRedirect: String,
        targetToUse: String
    ) {
        val targetResId = resparam.res.getIdentifier(targetToUse, RESOURCE_TYPE, resparam.packageName)
        if (targetResId == 0) {
            XposedBridge.log("$logTag: Target drawable '$targetToUse' not found. Cannot redirect '$sourceToRedirect'.")
            return
        }
        // XposedBridge.log("$logTag: Found target drawable '$targetToUse' with ID: $targetResId for source '$sourceToRedirect'") // Optional log
        val resForwarder = XResForwarder(resparam.res, targetResId)

        try {
            resparam.res.setReplacement(resparam.packageName, RESOURCE_TYPE, sourceToRedirect, resForwarder)
            XposedBridge.log("$logTag: Rule set: '$sourceToRedirect' -> '$targetToUse'")
        } catch (innerE: Throwable) {
            XposedBridge.log("$logTag: Failed to set replacement for '$sourceToRedirect' -> '$targetToUse': ${innerE.message}")
        }
    }

    override fun init() {}
}

object DrawableRedirectHookManager : BaseHook() {
    private const val TARGET_PACKAGE = "com.coloros.gallery3d"
    override fun init() {}

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName == TARGET_PACKAGE) {
            DrawableRedirect.initResourceHook(resparam)
        }
    }
}