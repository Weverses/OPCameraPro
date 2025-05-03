package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.tlsu.opluscamerapro.utils.DeviceCheck.exec
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
import com.topjohnwu.superuser.Shell
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * ZIP文件解压工具
 * 用于将应用内资源解压到指定目录
 */
object ZipExtractor {
    private const val TAG = "ZipExtractor"
    
    @SuppressLint("SdCardPath")
    private const val TARGET_DIR = "/sdcard/Android/OplusCameraPro/SubModule"
    private const val ZIP_ASSET_NAME = "OPCameraPro.zip"
    private const val VERSION_FILE = "version.txt"
    private const val CURRENT_VERSION = "2.0.02"
    
    // Magisk模块路径
    private const val MAGISK_MODULE_PATH = "/data/adb/modules/OPCameraPro"
    
    /**
     * 检查并解压模块文件，然后尝试将其安装到Magisk
     * @param context 上下文
     * @return 是否成功操作
     */
    fun processModuleFiles(context: Context): Boolean {
        // 首先解压模块文件到SD卡
        val extractResult = extractModuleIfNeeded(context)
        if (!extractResult) {
            Log.e(TAG, "Failed to extract module files")
            return false
        }
        
        // 尝试安装到Magisk模块目录
        return installToMagisk()
    }
    
    /**
     * 将解压后的模块文件安装到Magisk目录
     * @return 是否成功安装
     */
    private fun installToMagisk(): Boolean {
        try {
            // 检查Magisk模块目录是否存在
//            if (!checkMagiskModuleExists()) {
//                Log.d(TAG, "Magisk module directory doesn't exist, creating it")
//                Shell.cmd("mkdir -p $MAGISK_MODULE_PATH").exec()
//
//            }
            exec("su -c mkdir -p /data/adb/modules/OPCameraPro/")

            // 检查版本是否需要更新
            if (!shouldUpdateMagiskModule()) {
                Log.d(TAG, "Magisk module is up to date, no need to copy files")
                return true
            }
            
            // 复制文件到Magisk模块目录
            Log.d(TAG, "Copying files to Magisk module directory")
            try {
                if (isV1501()) {
                    exec("su -c rm -rf $TARGET_DIR/product/framework/")
                }
                exec("su -c cp -rf $TARGET_DIR/* $MAGISK_MODULE_PATH/")
                // 设置正确的权限
                exec("su -c chmod -R 755 $MAGISK_MODULE_PATH")
                Log.d(TAG, "Successfully installed module to Magisk")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to copy files to Magisk module directory: ", e)
                return false
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error installing to Magisk: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * 检查Magisk模块目录是否存在
     * @return 目录是否存在
     */
    private fun checkMagiskModuleExists(): Boolean {
        val result = Shell.cmd("[ -d \"$MAGISK_MODULE_PATH\" ] && echo \"exists\" || echo \"not exists\"").exec()
        return result.isSuccess && result.out.joinToString("").contains("exists")
    }
    
    /**
     * 检查是否需要更新Magisk模块
     * @return 是否需要更新
     */
    private fun shouldUpdateMagiskModule(): Boolean {
        try {
            // 检查目标目录是否为空
            val lsResult = exec("su -c ls -A $MAGISK_MODULE_PATH 2>/dev/null || echo empty")
            val isEmpty = lsResult.isBlank() || lsResult == "empty"
            
            // 如果目录为空，需要更新
            if (isEmpty) {
                Log.d(TAG, "Magisk module directory is empty, update needed")
                return true
            }
            
            // 检查版本文件
            val sourceVersionFile = "$TARGET_DIR/$VERSION_FILE"
            val targetVersionFile = "$MAGISK_MODULE_PATH/$VERSION_FILE"
            
            // 检查目标版本文件是否存在
            val targetVersionExists = try {
                exec("su -c test -f \"$targetVersionFile\" && echo exists || echo not_exists") == "exists"
            } catch (e: Exception) {
                Log.e(TAG, "Error checking target version file: ${e.message}")
                false
            }
            
            // 如果目标版本文件不存在，需要更新
            if (!targetVersionExists) {
                Log.d(TAG, "Magisk module version file doesn't exist, update needed")
                return true
            }
            
            // 读取两个版本文件并比较
            val sourceVersion = try {
                exec("su -c cat $sourceVersionFile").trim()
            } catch (e: Exception) {
                Log.e(TAG, "Error reading source version: ${e.message}")
                ""
            }
            
            val targetVersion = try {
                exec("su -c cat $targetVersionFile").trim()
            } catch (e: Exception) {
                Log.e(TAG, "Error reading target version: ${e.message}")
                ""
            }
            
            // 如果任一版本文件无法读取，保险起见需要更新
            if (sourceVersion.isBlank() || targetVersion.isBlank()) {
                Log.d(TAG, "Failed to read version files, update needed")
                return true
            }
            
            // 如果版本不同，需要更新
            if (sourceVersion != targetVersion) {
                Log.d(TAG, "Version mismatch (source: $sourceVersion, target: $targetVersion), update needed")
                return true
            }
            
            // 版本相同，不需要更新
            Log.d(TAG, "Magisk module is up to date")
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if module should be updated: ${e.message}")
            // 出错时保险起见返回true，进行更新
            return true
        }
    }
    
    /**
     * 检查并解压模块文件
     * @param context 上下文
     * @return 是否成功解压
     */
    fun extractModuleIfNeeded(context: Context): Boolean {
        try {
            // 确保目标目录存在
            try {
                exec("mkdir -p $TARGET_DIR")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create target directory: ${e.message}")
                return false
            }
            
            // 从assets复制zip文件到临时目录
            val tempFile = copyAssetToTemp(context, ZIP_ASSET_NAME)
            if (tempFile == null) {
                Log.e(TAG, "Failed to copy asset to temp file")
                return false
            }
            
            // 解压文件 - 每次都重新解压
            try {
                Log.d(TAG, "Extracting module files to $TARGET_DIR")
                // 清空目标目录
                exec("rm -rf $TARGET_DIR/*")
                // 重新解压
                exec("unzip -o ${tempFile.absolutePath} -d $TARGET_DIR")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to extract zip file: ${e.message}")
                tempFile.delete()
                return false
            }
            
            // 删除临时文件
            tempFile.delete()
            
            // 标记版本已解压
            saveVersionInfo()
            
            Log.d(TAG, "Successfully extracted module files")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting module: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * 将assets中的文件复制到临时目录
     */
    private fun copyAssetToTemp(context: Context, assetName: String): File? {
        try {
            val inputStream = context.assets.open(assetName)
            val tempFile = File(context.cacheDir, assetName)
            
            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
            
            return tempFile
        } catch (e: IOException) {
            Log.e(TAG, "Error copying asset to temp: ${e.message}")
            return null
        }
    }
    
    /**
     * 检查是否已解压当前版本
     */
    private fun isVersionExtracted(): Boolean {
        try {
            val versionFile = "$TARGET_DIR/$VERSION_FILE"
            
            // 首先检查文件是否存在
            val fileExists = try {
                exec("test -f \"$versionFile\" && echo exists || echo not_exists") == "exists"
            } catch (e: Exception) {
                return false
            }
            
            if (!fileExists) {
                return false
            }
            
            // 读取版本文件内容
            val version = try {
                exec("cat $versionFile").trim()
            } catch (e: Exception) {
                return false
            }
            
            return version == CURRENT_VERSION
        } catch (e: Exception) {
            Log.e(TAG, "Error checking extracted version: ${e.message}")
            return false
        }
    }
    
    /**
     * 保存版本信息文件
     */
    private fun saveVersionInfo() {
        try {
            exec("echo '$CURRENT_VERSION' > $TARGET_DIR/$VERSION_FILE")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving version info: ${e.message}")
        }
    }
}