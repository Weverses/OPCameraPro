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

    fun copyAssetsToInternalStorage(context: Context, assetPath: String, destinationDir: File): Boolean {
        val assetManager = context.assets
        try {
            val assets = assetManager.list(assetPath)
            if (assets.isNullOrEmpty()) {
                return copyAssetFile(context, assetPath, destinationDir)
            } else {
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