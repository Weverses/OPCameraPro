package com.tlsu.opluscamerapro.utils

import android.annotation.SuppressLint
import com.tlsu.opluscamerapro.utils.DeviceCheck.execWithResult

/**
 * 附属模块相关
 */
object SubModule {
    private const val TAG = "SubModule"
    
    const val MAGISK_MODULE_PATH = "/data/adb/modules/OPCameraPro"
    @SuppressLint("SdCardPath")


    fun deleteFrameworkAndLibs(): Boolean {
        return try {
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
}