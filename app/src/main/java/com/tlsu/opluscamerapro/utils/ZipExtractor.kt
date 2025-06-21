package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.tlsu.opluscamerapro.utils.DeviceCheck.execWithResult
import com.tlsu.opluscamerapro.utils.DeviceCheck.isOP13
import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1502
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * ZIP文件解压工具
 * 用于将应用内资源解压到Magisk模块目录
 */
object ZipExtractor {
    private const val TAG = "ZipExtractor"
    
    const val MAGISK_MODULE_PATH = "/data/adb/modules/OPCameraPro"
    @SuppressLint("SdCardPath")
    private const val DIR = "/sdcard/Android/OplusCameraPro"
    private const val TEMP_DIR = "/data/tmp/OPCameraPro"
    private const val KSUD_PATH = "/data/adb/ksud"
    private const val MAGISK_PATH = "/data/adb/magisk"
    private const val OVERLAYFS_PATH = "/data/adb/ksu/modules.img"
    private const val APATCH_PATH = "/data/adb/apd"
    private const val ZIP_ASSET_NAME = "OPCameraPro.zip"
    private const val VERSION_FILE = "version.txt"
    private const val CURRENT_VERSION = "2.1.22"
    
    // 创建固定线程池，避免无限创建线程
    private val executor = Executors.newFixedThreadPool(1)
    
    /**
     * 检查并解压模块文件到Magisk模块目录
     * @param context 上下文
     * @return 是否成功操作
     */
    fun processModuleFiles(context: Context) {
        Log.d(TAG, "开始处理模块文件...")
        try {
            // 确保目录存在
            execWithResult("mkdir -p $DIR")
            Log.d(TAG, "已创建目录: $DIR")
            execWithResult("mkdir -p $MAGISK_MODULE_PATH")
            Log.d(TAG, "已创建目录: $MAGISK_MODULE_PATH")

            // 从assets复制zip文件到临时目录
            val tempFile = copyAssetToTemp(context, ZIP_ASSET_NAME)
            if (tempFile == null) {
                Log.e(TAG, "复制assets文件到临时目录失败")
                return
            }
            Log.d(TAG, "已复制ZIP文件到临时目录: ${tempFile.absolutePath}")

            try {
                Log.d(TAG, "开始解压模块文件到: $MAGISK_MODULE_PATH")

                // 确保临时目录存在
                val mkdirResult = execWithResult("mkdir -p $TEMP_DIR")
                if (!mkdirResult.isSuccess) {
                    Log.e(TAG, "创建临时目录失败: ${mkdirResult.err.joinToString("\n")}")
                    return
                }
                Log.d(TAG, "已创建临时目录: $TEMP_DIR")

                // 解压文件到临时目录
                Log.d(TAG, "开始解压ZIP文件...")
                val unzipResult = execWithResult("unzip -o ${tempFile.absolutePath} -d $TEMP_DIR")
                if (!unzipResult.isSuccess) {
                    Log.e(TAG, "解压文件失败: ${unzipResult.err.joinToString("\n")}")
                    return
                }
                Log.d(TAG, "解压ZIP文件成功")

                // 根据设备版本复制正确的文件
                if (isV1502() && isOP13()) {
                    Log.d(TAG, "检测到V15.0.2系统，使用HDR目录的文件")
                    // 检查HDR目录是否存在
                    val hdrDirExists =
                        execWithResult("test -d $TEMP_DIR/HDR && echo exists").out.joinToString("")
                            .contains("exists")
                    if (hdrDirExists) {
                        execWithResult("cp -rf $TEMP_DIR/HDR/* $TEMP_DIR/Common")
                        Log.d(TAG, "已复制HDR目录内容到Common目录")
                    } else {
                        Log.w(TAG, "HDR目录不存在 (V15.0.1)")
                    }
                } else {
                    Log.d(TAG, "检测到其他系统版本，仅使用Common目录内容")
                }

                // 清理不需要的目录
                execWithResult("rm -rf $TEMP_DIR/HDR")
                execWithResult("rm -rf $TEMP_DIR/OS1501")
                Log.d(TAG, "已清理不需要的目录")

                // 打包文件
                Log.d(TAG, "开始打包文件...")
                execWithResult("test -f $TEMP_DIR/Common/1.zip && rm -rf $TEMP_DIR/Common/1.zip")
                val compressResult = execWithResult("chmod 777 $TEMP_DIR/Common/7za && $TEMP_DIR/Common/7za a -tzip $TEMP_DIR/Common/1.zip $TEMP_DIR/Common/*")
                if (!compressResult.isSuccess) {
                    Log.e(TAG, "打包文件失败: ${compressResult.err.joinToString("\n")}")
                    return
                }
                Log.d(TAG, "文件打包成功")

                // 检测Root管理器类型
                val rootManager = getRootManager()
                execWithResult("touch $TEMP_DIR/Common/$rootManager")
                Log.d(TAG, "已创建Root管理器标记文件: $rootManager")

                // 直接在主线程创建状态文件，确保一定会创建
                val installStartedResult = execWithResult("echo 'started' > $DIR/install_status.txt")
                if (!installStartedResult.isSuccess) {
                    Log.e(TAG, "创建安装状态文件失败: ${installStartedResult.err.joinToString("\n")}")
                } else {
                    Log.d(TAG, "已创建安装状态文件: $DIR/install_status.txt")
                }
                
                // 在后台线程中执行模块安装，避免ANR
                if (isSupportRootManager()) {
                    startModuleInstallation(tempFile)
                } else {
                    copyModuleToMagiskPath()
//                    copyModuleToSdacrd()
                }
            } catch (e: Exception) {
                Log.e(TAG, "解压ZIP文件失败: ${e.message}", e)
                tempFile.delete()
                execWithResult("rm -rf $TEMP_DIR")  // 确保清理临时目录
                return
            }
        } catch (e: Exception) {
            Log.e(TAG, "处理模块文件时出错: ${e.message}", e)
            return
        }
    }


    private fun copyModuleToMagiskPath() {
        execWithResult("test -f $MAGISK_MODULE_PATH && rm -rf $MAGISK_MODULE_PATH")
        execWithResult("mkdir -p $MAGISK_MODULE_PATH")
        execWithResult("cp -rf $TEMP_DIR/Common/* $MAGISK_MODULE_PATH")
        execWithResult("rm -rf $MAGISK_MODULE_PATH/META-INF")
        execWithResult("rm -rf $MAGISK_MODULE_PATH/1.zip")
        execWithResult("rm -rf $MAGISK_MODULE_PATH/customize.sh")
        execWithResult("rm -rf $MAGISK_MODULE_PATH/7za")
    }

    /**
     * 在后台执行器中启动模块安装
     * @return 操作是否成功启动
     */
    private fun startModuleInstallation(tempFile: File): Boolean {
        try {
            Log.d(TAG, "准备在后台启动模块安装...")
            
            // 使用Executors执行后台任务，更可靠
            executor.execute {
                Log.d(TAG, "模块安装后台任务开始执行")
                try {
                    // 在后台执行实际安装
                    executeModuleInstallation(tempFile)
                } catch (e: Exception) {
                    Log.e(TAG, "后台模块安装执行失败: ${e.message}", e)
                    try {
                        execWithResult("echo 'error: ${e.message}' > $DIR/install_status.txt")
                    } catch (e2: Exception) {
                        Log.e(TAG, "无法写入安装错误状态: ${e2.message}")
                    }
                }
            }
            
            Log.d(TAG, "模块安装任务已提交到后台执行")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "启动后台安装失败: ${e.message}", e)
            return false
        }
    }
    
    /**
     * 在后台线程中执行实际模块安装
     */
    private fun executeModuleInstallation(tempFile: File) {
        Log.d(TAG, "开始执行实际的模块安装操作...")
        
        try {
            // 检测安装命令
            val hasKsud = execWithResult("test -f $KSUD_PATH && echo true || echo false")
            val ksudAvailable = hasKsud.out.joinToString("").contains("true")
            
            val hasMagisk = execWithResult("test -f $MAGISK_PATH && echo true || echo false")
            val magiskAvailable = hasMagisk.out.joinToString("").contains("true")

            val hasOverlayFS = execWithResult("test -f $OVERLAYFS_PATH && echo true || echo false")
            val overlayfsAvailable = hasOverlayFS.out.joinToString("").contains("true")

            val hasApatch = execWithResult("test -f $APATCH_PATH && echo true || echo false")
            val apatchAvailable = hasApatch.out.joinToString("").contains("true")

            Log.d(TAG, "安装环境检测: KSUD可用=$ksudAvailable, Magisk可用=$magiskAvailable")
            
            // 执行实际安装
            val installResult = if (ksudAvailable && overlayfsAvailable) {
                Log.d(TAG, "使用KernelSU安装模块...")
                execWithResult("$KSUD_PATH module install $TEMP_DIR/Common/1.zip")
            } else if (magiskAvailable) {
                Log.d(TAG, "使用Magisk安装模块...")
                execWithResult("$MAGISK_PATH --install-module $TEMP_DIR/Common/1.zip")
            }
            else if (apatchAvailable) {
                Log.d(TAG, "使用Apatch安装模块...")
                execWithResult("$APATCH_PATH module install $TEMP_DIR/Common/1.zip")
            } else {
                Log.d(TAG, "MagicMount?直接复制文件...")
                val MODULE_EXIST = execWithResult("test -f $MAGISK_MODULE_PATH && echo true || echo false")
                val moduleExist = MODULE_EXIST.out.joinToString("").contains("true")
                if (moduleExist) {
                    execWithResult("rm -rf $MAGISK_MODULE_PATH")
                }
                execWithResult("mkdir $MAGISK_MODULE_PATH")
                execWithResult("unzip -o $TEMP_DIR/Common/1.zip -d $MAGISK_MODULE_PATH")
            }
            
            // 记录安装结果
            if (installResult.isSuccess) {
                Log.d(TAG, "模块安装成功完成")
                execWithResult("echo 'success' > $DIR/install_status.txt")
                // 标记版本已解压
                Log.d(TAG, "开始保存版本信息...")
                saveVersionInfo()
            } else {
                Log.e(TAG, "模块安装失败: ${installResult.err.joinToString("\n")}")
                execWithResult("echo 'failed: ${installResult.err.joinToString(" ")}' > $DIR/install_status.txt")
                copyModuleToSdacrd()
            }
            
            // 清理临时目录
            val cleanupResult = execWithResult("rm -rf $TEMP_DIR")
            if (!cleanupResult.isSuccess) {
                Log.w(TAG, "清理临时目录失败: ${cleanupResult.err.joinToString("\n")}")
            } else {
                Log.d(TAG, "已清理临时目录")
            }
            
            // 删除临时文件
            if (tempFile.exists()) {
                val deleted = tempFile.delete()
                Log.d(TAG, "删除临时ZIP文件 ${if (deleted) "成功" else "失败"}")
            }

            Log.d(TAG, "模块安装全部完成")
        } catch (e: Exception) {
            Log.e(TAG, "后台安装过程中出现异常: ${e.message}", e)
            try {
                execWithResult("echo 'error: ${e.message}' > $DIR/install_status.txt")
                execWithResult("rm -rf $TEMP_DIR")
                tempFile.delete()
            } catch (e2: Exception) {
                Log.e(TAG, "清理资源失败: ${e2.message}")
            }
        }
    }
    
    /**
     * 检查是否需要更新Magisk模块
     * @return 是否需要更新
     */
    fun shouldInstallSubModule(): Boolean {
//        val isVersionTxtExist = execWithResult("test -f $MAGISK_MODULE_PATH/$VERSION_FILE").isSuccess
//        if (isVersionTxtExist) {
//            val versionResult = execWithResult("cat $MAGISK_MODULE_PATH/$VERSION_FILE")
//            val version = if (versionResult.isSuccess && versionResult.out.isNotEmpty()) {
//                versionResult.out[0].trim()
//            } else {
//                ""
//            }
//            Log.d(TAG, "当前版本: $version, 目标版本: $CURRENT_VERSION")
//            return version != CURRENT_VERSION
//        }
//        Log.d(TAG, "版本文件不存在，需要安装")
//        return true
        return false
    }

    private fun copyModuleToSdacrd() {
        execWithResult("mv $TEMP_DIR/Common/1.zip $TEMP_DIR/Common/OPCameraPro_submodule.zip")
        execWithResult("test -f /sdcard/OPCameraPro_submodule.zip && rm /sdcard/OPCameraPro_submodule.zip")
        execWithResult("cp -f $TEMP_DIR/Common/OPCameraPro_submodule.zip /sdcard")
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
            
            // 确保文件被正确创建且非空
            if (tempFile.exists() && tempFile.length() > 0) {
                Log.d(TAG, "成功复制asset到临时文件: ${tempFile.absolutePath}, 大小: ${tempFile.length()} 字节")
                return tempFile
            } else {
                Log.e(TAG, "创建临时文件失败或文件为空")
                return null
            }
        } catch (e: IOException) {
            Log.e(TAG, "复制asset到临时目录时出错: ${e.message}", e)
            return null
        }
    }
    
    /**
     * 保存版本信息文件
     */
    private fun saveVersionInfo() {
        try {
            val moduleResult = execWithResult("echo '$CURRENT_VERSION' > $MAGISK_MODULE_PATH/$VERSION_FILE")
            val dirResult = execWithResult("echo '$CURRENT_VERSION' > $DIR/$VERSION_FILE")
            
            if (!moduleResult.isSuccess || !dirResult.isSuccess) {
                Log.e(TAG, "保存版本信息到一个或多个位置失败")
            } else {
                Log.d(TAG, "成功保存版本信息到所有位置")
            }
        } catch (e: Exception) {
            Log.e(TAG, "保存版本信息时出错: ${e.message}", e)
        }
    }

    private fun getRootManager(): String {
        val result = execWithResult("test -f $KSUD_PATH && echo ksud || echo magisk")
        val manager = result.out.firstOrNull()?.trim() ?: "magisk"
        Log.d(TAG, "检测到Root管理器: $manager")
        return manager
    }

    fun isSupportRootManager(): Boolean {
        val hasKsud = execWithResult("test -f $KSUD_PATH && echo true || echo false")
        val ksudAvailable = hasKsud.out.joinToString("").contains("true")

        val hasMagisk = execWithResult("test -f $MAGISK_PATH && echo true || echo false")
        val magiskAvailable = hasMagisk.out.joinToString("").contains("true")

        val hasOverlayFS = execWithResult("test -f $OVERLAYFS_PATH && echo true || echo false")
        val overlayfsAvailable = hasOverlayFS.out.joinToString("").contains("true")

        val hasApatch = execWithResult("test -f $APATCH_PATH && echo true || echo false")
        val apatchAvailable = hasApatch.out.joinToString("").contains("true")
        return (ksudAvailable && overlayfsAvailable) || magiskAvailable || apatchAvailable
    }

    fun deleteFrameworkAndLibs(): Boolean {
        return try {
//            execWithResult("mkdir -p $MAGISK_MODULE_PATH/tmp")
//            execWithResult("cp -rf $MAGISK_MODULE_PATH/odm/etc/camera/config $MAGISK_MODULE_PATH/tmp")
//            execWithResult("cp -rf $MAGISK_MODULE_PATH/odm/etc/camera/meishe_lut $MAGISK_MODULE_PATH/tmp")
//            execWithResult("cp -rf $MAGISK_MODULE_PATH/odm/etc/camera/filters_lut $MAGISK_MODULE_PATH/tmp")
//            execWithResult("rm -rf $MAGISK_MODULE_PATH/odm/")
//            execWithResult("rm -rf $MAGISK_MODULE_PATH/product/")
//            execWithResult("mkdir -p $MAGISK_MODULE_PATH/odm/etc/camera")
//            execWithResult("cp -rf $MAGISK_MODULE_PATH/tmp/config $MAGISK_MODULE_PATH/odm/etc/camera")
//            execWithResult("cp -rf $MAGISK_MODULE_PATH/tmp/meishe_lut $MAGISK_MODULE_PATH/odm/etc/camera")
//            execWithResult("cp -rf $MAGISK_MODULE_PATH/tmp/filters_lut $MAGISK_MODULE_PATH/odm/etc/camera")
//            execWithResult("rm -rf $MAGISK_MODULE_PATH/tmp")
            execWithResult("rm -rf $MAGISK_MODULE_PATH")
            deleteCameraData()
            deleteModuleData()
            true
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Failed to delete libs ${e.message}")
            false
        }
    }

    fun deleteCameraData() {
        execWithResult("rm -rf /data/user/0/com.oplus.camera/*")
        execWithResult("killall com.oplus.camera")
    }

    fun deleteModuleData() {
        execWithResult("rm -rf /sdcard/Android/OplusCameraPro/*")
    }

    fun switchToDefaultMount() {
        if (execWithResult("test -f $MAGISK_MODULE_PATH/is_mountbind && echo true || echo false")
                .out.joinToString("").contains("true")) {

            execWithResult("rm -rf $MAGISK_MODULE_PATH/is_mountbind")
            execWithResult("cp -rf $MAGISK_MODULE_PATH/Common/* $MAGISK_MODULE_PATH/")
            execWithResult("rm -rf $MAGISK_MODULE_PATH/Common")

            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data.sh $MAGISK_MODULE_PATH/post-fs-data-mountbind.sh")
            execWithResult("touch $MAGISK_MODULE_PATH/is_default")

        } else if (execWithResult("test -f $MAGISK_MODULE_PATH/is_overlayfs && echo true || echo false")
                .out.joinToString("").contains("true")){
            execWithResult("rm -rf $MAGISK_MODULE_PATH/is_overlayfs")
            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data.sh $MAGISK_MODULE_PATH/post-fs-data-overlayfs.sh")
            execWithResult("touch $MAGISK_MODULE_PATH/is_default")
        }
    }

    fun switchToOverlayFS() {
        if (execWithResult("test -f $MAGISK_MODULE_PATH/is_mountbind && echo true || echo false")
                .out.joinToString("").contains("true")){
            // 此时代表肯定有post-fs-data.sh为mount bind, 故需要将目录已有的overlayfs重命名即可
            execWithResult("rm -rf $MAGISK_MODULE_PATH/is_mountbind")

            execWithResult("cp -rf $MAGISK_MODULE_PATH/Common/* $MAGISK_MODULE_PATH/")
            execWithResult("rm -rf $MAGISK_MODULE_PATH/Common")

            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data.sh $MAGISK_MODULE_PATH/post-fs-data-mountbind.sh")
            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data-overlayfs.sh $MAGISK_MODULE_PATH/post-fs-data.sh")

            execWithResult("touch $MAGISK_MODULE_PATH/is_overlayfs")

        } else if (execWithResult("test -f $MAGISK_MODULE_PATH/is_default && echo true || echo false")
                .out.joinToString("").contains("true")){
            execWithResult("rm -rf $MAGISK_MODULE_PATH/is_default")
            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data-overlayfs.sh $MAGISK_MODULE_PATH/post-fs-data.sh")
            execWithResult("touch $MAGISK_MODULE_PATH/is_overlayfs")
        }
    }

    fun switchToMountBind() {
        if (execWithResult("test -f $MAGISK_MODULE_PATH/is_default && echo true || echo false")
                .out.joinToString("").contains("true")) {

            execWithResult("rm -rf $MAGISK_MODULE_PATH/is_default")
            execWithResult("mkdir -p $MAGISK_MODULE_PATH/Common")
            execWithResult("mv -f $MAGISK_MODULE_PATH/odm $MAGISK_MODULE_PATH/Common")
            execWithResult("mv -f $MAGISK_MODULE_PATH/product $MAGISK_MODULE_PATH/Common")

            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data-mountbind.sh $MAGISK_MODULE_PATH/post-fs-data.sh")

            execWithResult("touch $MAGISK_MODULE_PATH/is_mountbind")

        } else if (execWithResult("test -f $MAGISK_MODULE_PATH/is_overlayfs && echo true || echo false")
                .out.joinToString("").contains("true")){
            execWithResult("rm -rf $MAGISK_MODULE_PATH/is_overlayfs")
            execWithResult("mkdir -p $MAGISK_MODULE_PATH/Common")
            execWithResult("mv -f $MAGISK_MODULE_PATH/odm $MAGISK_MODULE_PATH/Common")
            execWithResult("mv -f $MAGISK_MODULE_PATH/product $MAGISK_MODULE_PATH/Common")

            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data.sh $MAGISK_MODULE_PATH/post-fs-data-overlayfs.sh")
            execWithResult("mv -f $MAGISK_MODULE_PATH/post-fs-data-mountbind.sh $MAGISK_MODULE_PATH/post-fs-data.sh")

            execWithResult("touch $MAGISK_MODULE_PATH/is_mountbind")

        }
    }

    fun getCurrentMountMethod(): String {
        if (execWithResult("test -f $MAGISK_MODULE_PATH/is_mountbind && echo true || echo false")
                .out.joinToString("").contains("true")) {
            return "Mount --bind"
        } else if (execWithResult("test -f $MAGISK_MODULE_PATH/is_overlayfs && echo true || echo false")
                .out.joinToString("").contains("true")) {
            return "OverlayFS"
        } else {
            return "default"
        }
    }

    /**
     * 关闭执行器，释放资源
     */
    fun shutdown() {
        try {
            executor.shutdown()
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: Exception) {
            executor.shutdownNow()
        }
    }
}