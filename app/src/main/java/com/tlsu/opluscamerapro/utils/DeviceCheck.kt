package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import java.io.IOException
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

object DeviceCheck {

    @SuppressLint("PrivateApi")
    fun getProp(mKey: String): String = Class.forName("android.os.SystemProperties").getMethod("get", String::class.java).invoke(Class.forName("android.os.SystemProperties"), mKey)!!
        .toString()

    fun getOplusRomVersion(): String {
        return (getProp("ro.build.version.oplusrom"))
    }

    fun getAndroidVersion(): String {
        return (getProp("ro.build.version.release"))
    }

    fun isV1501(): Boolean {
        // 获取OplusRom版本
        val romVersion = getOplusRomVersion()
        
        // 提取主要版本号部分
        val versionRegex = "V(\\d+)\\.(\\d+)\\.(\\d+).*".toRegex()
        val matchResult = versionRegex.find(romVersion) ?: return false
        
        try {
            // 提取主版本号、次版本号和修订版本号
            val (majorStr, minorStr, patchStr) = matchResult.destructured
            val major = majorStr.toInt()
            val minor = minorStr.toInt()
            val patch = patchStr.toInt()
            
            // 判断版本是否大于等于V15.0.1
            return when {
                major > 15 -> true  // 例如V16.0.0
                major == 15 && minor > 0 -> true  // 例如V15.1.0
                major == 15 && minor == 0 && patch >= 1 -> true  // V15.0.1, V15.0.2等
                else -> false
            }
        } catch (e: Exception) {
            // 如果解析失败，保守返回false
            return false
        }
    }

    fun isV15(): Boolean {
        return (getProp("ro.oplus.theme.version") >= "15000")
    }

    fun getDeviceModel(): String {
        return (getProp("ro.product.model"))
    }

    fun getDeviceMarketName(): String {
        return (getProp("ro.vendor.oplus.market.enname"))
    }

    fun exec(command: String): String {
        var process: Process? = null
        var reader: BufferedReader? = null
        var `is`: InputStreamReader? = null
        var os: DataOutputStream? = null
        return try {
            process = Runtime.getRuntime().exec("su")
            `is` = InputStreamReader(process.inputStream)
            reader = BufferedReader(`is`)
            os = DataOutputStream(process.outputStream)
            os.writeBytes(
                command.trimIndent()
            )
            os.writeBytes("\nexit\n")
            os.flush()
            var read: Int
            val buffer = CharArray(4096)
            val output = StringBuilder()
            while (reader.read(buffer).also { read = it } > 0) {
                output.append(buffer, 0, read)
            }
            process.waitFor()
            output.toString()
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }finally {
            try {
                os?.close()
                `is`?.close()
                reader?.close()
                process?.destroy()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun exec(commands: Array<String>): String {
        val stringBuilder = java.lang.StringBuilder()
        for (command in commands) {
            stringBuilder.append(exec(command))
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}