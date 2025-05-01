package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint


object DeviceCheck {

    @SuppressLint("PrivateApi")
    fun getProp(mKey: String): String = Class.forName("android.os.SystemProperties").getMethod("get", String::class.java).invoke(Class.forName("android.os.SystemProperties"), mKey)!!
        .toString()

    private fun getOplusRomVersion(): String {
        return (getProp("ro.build.version.oplusrom"))
    }

    fun isV1501(): Boolean {
        return (getOplusRomVersion() == "V15.0.1")
    }

    fun isV15(): Boolean {
        return (getProp("ro.oplus.theme.version") >= "15000")
    }
}