package com.tlsu.opluscamerapro.hook.camera

import android.util.Size
import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.tlsu.opluscamerapro.hook.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


object UnitSDKPatch: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    override fun init() {
        XposedHelpers.findAndHookMethod(
            "com.oplus.ocs.camera.CameraPictureCallback\$CameraPictureImage",
            safeClassLoader,
            "getCurrentZoomSize",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    //这里数值我瞎填的，反正能用
                    val newSize: Size = Size(3072, 4096)
                    param.result = newSize
                }
            })
    }
    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }
}

object UnitSDKPatchHook: BaseHook() {
    override fun init() {
        UnitSDKPatch.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            UnitSDKPatch.setLoadPackageParam(lpparam)
            init()
        }
    }
}



