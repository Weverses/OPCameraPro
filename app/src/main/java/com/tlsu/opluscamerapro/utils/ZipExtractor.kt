package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.tlsu.opluscamerapro.utils.DeviceCheck.exec
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV15
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
import com.topjohnwu.superuser.Shell
import de.robv.android.xposed.XposedBridge
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * ZIP文件解压工具
 * 用于将应用内资源解压到Magisk模块目录
 */
object ZipExtractor {
    private const val TAG = "ZipExtractor"
    
    private const val MAGISK_MODULE_PATH = "/data/adb/modules/OPCameraPro"
    @SuppressLint("SdCardPath")
    private const val DIR = "/sdcard/Android/OPCameraPro"
    private const val TEMP_DIR = "/data/tmp/OPCameraPro"
    private const val KSUD_PATH = "/data/adb/ksud"
    private const val ZIP_ASSET_NAME = "OPCameraPro.zip"
    private const val VERSION_FILE = "version.txt"
    private const val CURRENT_VERSION = "2.1.00"
    
    /**
     * 检查并解压模块文件到Magisk模块目录
     * @param context 上下文
     * @return 是否成功操作
     */
    fun processModuleFiles(context: Context): Boolean {
        try {
//            // 确保Magisk模块目录存在
//            exec("su -c mkdir -p $MAGISK_MODULE_PATH")
            
            // 检查版本是否需要更新
            if (!shouldUpdateMagiskModule()) {
                Log.d(TAG, "Magisk module is up to date, no need to extract files")
                return true
            }

            // 从assets复制zip文件到临时目录
            val tempFile = copyAssetToTemp(context, ZIP_ASSET_NAME)
            if (tempFile == null) {
                Log.e(TAG, "Failed to copy asset to temp file")
                return false
            }
            
            try {
                Log.d(TAG, "Extracting module files to $MAGISK_MODULE_PATH")

                // 将模块解压到临时目录
                exec("su -c mkdir -p $TEMP_DIR")
                exec("su -c unzip -o ${tempFile.absolutePath} -d $TEMP_DIR")
                if (isV1501()) {
                    exec("su -c cp -rf $TEMP_DIR/HDR/* $TEMP_DIR/Common")
                }
                if (isV15() && !isV1501()) {
                    exec("su -c cp -rf $TEMP_DIR/OS1501/* $TEMP_DIR/Common")
                }
                exec("su -c rm -rf $TEMP_DIR/HDR")
                exec("su -c rm -rf $TEMP_DIR/OS1501")

                exec("su -c chmod 777 $TEMP_DIR/Common/7za && $TEMP_DIR/Common/7za a -tzip $TEMP_DIR/Common/1.zip $TEMP_DIR/Common/*")

                val RootManager = getRootManager()
                exec("su -c touch $TEMP_DIR/Common/$RootManager")

                exec("su -c test -f $KSUD_PATH && /data/adb/ksud module install $TEMP_DIR/Common/1.zip || /data/adb/magisk --install-module $TEMP_DIR/Common/1.zip")

                exec("su -c rm -rf $TEMP_DIR")

                Log.d(TAG, "Successfully extracted module files")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to extract zip file: ${e.message}")
                tempFile.delete()
                return false
            }
            
            // 删除临时文件
            tempFile.delete()
            
            // 标记版本已解压
            saveVersionInfo()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error processing module files: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * 检查是否需要更新Magisk模块
     * @return 是否需要更新
     */
    private fun shouldInstallSubModule(): Boolean {
        Shell.cmd("test -f ").exec()
        return false
    }
    private fun shouldUpdateMagiskModule(): Boolean {
        try {
            // 检查目标目录是否为空
            val lsResult = exec("su -c ls -A $MAGISK_MODULE_PATH 2>/dev/null || echo empty")
            val isEmpty = lsResult.isBlank() || lsResult == "empty"
            
            // 如果目录为空，需要更新
            if (isEmpty) {
                Log.i(TAG, "Magisk module directory is empty, update needed")
                return true
            }
            
            // 检查版本文件
            val versionFile = "$MAGISK_MODULE_PATH/$VERSION_FILE"
            
            // 检查版本文件是否存在
            val versionExists = try {
                exec("su -c test -f \"$versionFile\" && echo exists || echo not_exists") == "exists"
            } catch (e: Exception) {
                Log.i(TAG,"Error checking version file: ${e.message}")
                false
            }
            
            // 如果版本文件不存在，需要更新
            if (!versionExists) {
                Log.i(TAG, "Version file doesn't exist, update needed")
                return true
            }
            
            // 读取版本文件内容
            val version = try {
                exec("su -c cat $versionFile").trim()
            } catch (e: Exception) {
                Log.i(TAG,"Error reading version: ${e.message}")
                ""
            }
            
            // 如果版本文件无法读取，保险起见需要更新
            if (version.isBlank()) {
                Log.i(TAG,"Failed to read version file, update needed")
                return true
            }
            
            // 如果版本不同，需要更新
            if (version != CURRENT_VERSION) {
                Log.i(TAG,"Version mismatch (current: $version, new: $CURRENT_VERSION), update needed")
                return true
            }
            
            // 版本相同，不需要更新
            Log.i(TAG,"Magisk module is up to date")
            return false
        } catch (e: Exception) {
            Log.i(TAG,"Error checking if module should be updated: ${e.message}")
            // 出错时保险起见返回true，进行更新
            return true
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
     * 保存版本信息文件
     */
    private fun saveVersionInfo() {
        try {
            exec("su -c echo '$CURRENT_VERSION' > $MAGISK_MODULE_PATH/$VERSION_FILE")
            exec("su -c echo '$CURRENT_VERSION' > $DIR/$VERSION_FILE")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving version info: ${e.message}")
        }
    }

    private fun getRootManager():String {
        return(exec("su -c test -f $KSUD_PATH && echo ksud || echo magisk"))
    }
}