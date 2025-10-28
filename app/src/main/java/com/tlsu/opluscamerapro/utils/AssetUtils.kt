package com.tlsu.opluscamerapro.utils

import android.app.Application
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object AssetUtils {

    private const val TAG = "AssetUtils"

    /**
     * 将 assets 目录下的单个文件或整个文件夹复制到应用的文件目录。
     *
     * @param context Context 对象，用于访问 assets 和应用目录。
     * @param assetPath 要复制的 assets 内的相对路径（例如 "meishe_lut" 或 "configs/default.json"）。
     * @param destinationDir 目标目录，通常是 context.filesDir。
     * @return Boolean 返回 true 表示复制成功或文件已存在，false 表示失败。
     */
    fun copyAssetsToInternalStorage(context: Context, assetPath: String, destinationDir: File): Boolean {
        val assetManager = context.assets
        try {
            // list() 方法可以列出指定路径下的所有文件和文件夹名
            val assets = assetManager.list(assetPath)
            if (assets.isNullOrEmpty()) {
                // 如果列表为空，说明它可能是一个文件
                return copyAssetFile(context, assetPath, destinationDir)
            } else {
                // 如果列表不为空，说明它是一个文件夹
                val destDir = File(destinationDir, assetPath)
                if (!destDir.exists() && !destDir.mkdirs()) {
                    Log.e(TAG, "无法创建目标目录: ${destDir.absolutePath}")
                    return false
                }

                var allSuccess = true
                for (asset in assets) {
                    // 递归复制子文件/子文件夹
                    val success = copyAssetsToInternalStorage(context, "$assetPath/$asset", destinationDir)
                    if (!success) {
                        allSuccess = false
                    }
                }
                return allSuccess
            }
        } catch (e: IOException) {
            Log.e(TAG, "从 assets 复制失败: $assetPath", e)
            // 如果发生IO异常，很可能 assetPath 是一个文件而不是目录，尝试按文件复制
            return copyAssetFile(context, assetPath, destinationDir)
        }
    }

    /**
     * 复制单个 asset 文件。
     */
    private fun copyAssetFile(context: Context, assetPath: String, destinationDir: File): Boolean {
        try {
            val destinationFile = File(destinationDir, assetPath)
            // 确保父目录存在
            destinationFile.parentFile?.mkdirs()

            context.assets.open(assetPath).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    Log.i(TAG, "文件已复制: ${destinationFile.absolutePath}")
                }
            }
            return true
        } catch (e: Exception) {
            Log.e(TAG, "复制文件失败: $assetPath", e)
            return false
        }
    }
}