package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity.MODE_WORLD_READABLE
import java.io.IOException
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

object DeviceCheck {

    /**
     * 类似于libsu的Shell.Result结构
     */
    class ShellResult(
        val code: Int,          // 命令退出码
        val out: List<String>,  // 标准输出行
        val err: List<String>   // 标准错误行
    ) {
        val isSuccess: Boolean get() = code == 0
    }

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

    fun isV1600(): Boolean {
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
                major == 15 && minor == 0 && patch >= 2 -> true  // V15.0.1, V15.0.2等
                else -> false
            }
        } catch (e: Exception) {
            // 如果解析失败，保守返回false
            return false
        }
    }

    fun isOP13(): Boolean {
        return (getDeviceModel() == "PJZ110")
    }

    fun isV15(): Boolean {
        return (getProp("ro.oplus.theme.version") >= "15000")
    }

    fun isV16(): Boolean {
        return (getProp("ro.oplus.theme.version") >= "16000")
    }

    fun getDeviceModel(): String {
        return (getProp("ro.product.model"))
    }

    fun getDeviceMarketName(): String {
        return (getProp("ro.vendor.oplus.market.enname"))
    }

    fun isNewCameraVer(ver: Int): Boolean {
        val opCameraVersion = exec("dumpsys package com.oplus.camera 2>/dev/null | awk '/codePath=\\/data\\/app/{p=1} p && /versionName/{print; exit}' | cut -d'=' -f2")
        if (opCameraVersion.isNullOrEmpty()) {
            return false
        }
        return checkVersionName(opCameraVersion, ver)
    }

    fun checkVersionName(versionName: String, checkVer: Int): Boolean {
        val parts = versionName.split('.')

        try {
            // 将每个部分转换为整数
            val major = parts[0].toInt()
            // val minor = parts[1].toInt()
            val patch = parts[2].toInt()
            // val build = parts[3].toInt()

            // >=5.0.46
            return (major == 5 && patch >= checkVer) || (major > 5)
        } catch (e: NumberFormatException) {
            return false
        }
    }
    /**
     * 执行命令并返回字符串结果（旧方式，保留兼容性）
     */
    fun exec(command: String): String {
        val result = execWithResult(command)
        return result.out.joinToString("\n")
    }
    /**
     * 执行命令并返回结构化结果
     */
    fun execWithResult(command: String): ShellResult {
        var process: Process? = null
        var stdReader: BufferedReader? = null
        var errReader: BufferedReader? = null
        var stdIs: InputStreamReader? = null
        var errIs: InputStreamReader? = null
        var os: DataOutputStream? = null
        
        try {
            process = Runtime.getRuntime().exec("su")
            stdIs = InputStreamReader(process.inputStream)
            errIs = InputStreamReader(process.errorStream)
            stdReader = BufferedReader(stdIs)
            errReader = BufferedReader(errIs)
            os = DataOutputStream(process.outputStream)
            
            os.writeBytes(command.trimIndent())
            os.writeBytes("\nexit\n")
            os.flush()
            
            // 收集标准输出
            val stdOutput = mutableListOf<String>()
            var line: String?
            while (stdReader.readLine().also { line = it } != null) {
                line?.let { stdOutput.add(it) }
            }
            
            // 收集错误输出
            val errOutput = mutableListOf<String>()
            while (errReader.readLine().also { line = it } != null) {
                line?.let { errOutput.add(it) }
            }
            
            // 等待进程结束并获取退出码
            val exitCode = process.waitFor()
            
            return ShellResult(exitCode, stdOutput, errOutput)
        } catch (e: IOException) {
            return ShellResult(1, emptyList(), listOf(e.toString()))
        } catch (e: InterruptedException) {
            return ShellResult(1, emptyList(), listOf(e.toString()))
        } finally {
            try {
                os?.close()
                stdIs?.close()
                errIs?.close()
                stdReader?.close()
                errReader?.close()
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
    
    /**
     * 执行多个命令并返回结构化结果
     */
    fun execWithResult(commands: Array<String>): ShellResult {
        val allOutput = mutableListOf<String>()
        val allErrors = mutableListOf<String>()
        var finalCode = 0
        
        for (command in commands) {
            val result = execWithResult(command)
            allOutput.addAll(result.out)
            allErrors.addAll(result.err)
            
            // 如果有任何命令失败，记录非零退出码
            if (result.code != 0) {
                finalCode = result.code
            }
        }
        
        return ShellResult(finalCode, allOutput, allErrors)
    }
}