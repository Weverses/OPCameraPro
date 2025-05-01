package com.tlsu.opluscamerapro.utils

import com.tlsu.opluscamerapro.utils.DeviceCheck.isV1501
import com.tlsu.opluscamerapro.utils.ParseConfig.addPresetTag
import de.robv.android.xposed.XposedBridge

// 数据模型类
data class VendorTagInfo(
    val vendorTag: String,
    val type: String,
    val count: String,
    val value: String
)

object AddConfig {
    fun addConfig() {
        // 25MP Turbo RAW Resolution Enhance
        addPresetTag(
            VendorTagInfo(
                "com.oplus.turboraw.re.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // Master Mode (Version 2)
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.master.mode.version",
                "Float",
                "1",
                "2.0"
            ),
            MergeStrategy.OVERRIDE
        )

        // 大师模式 RAW MAX 格式
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.master.hq.raw.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 人像模式变焦
        addPresetTag(
            VendorTagInfo(
                "com.oplus.rear.portrait.zoom.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )
        addPresetTag(
            VendorTagInfo(
                "com.oplus.portrait.photo.ratio.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )
        addPresetTag(
            VendorTagInfo(
                "com.oplus.save.portrait.zoom.value",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 720P 60FPS Video
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.video.720p.60fps.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 慢动作视频超广角480FPS
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.slowvideo.ultra.wide.480fps.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 新版微距模式
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.macro.closeup.max.zoom.value",
                "Float",
                "1",
                "30.0" // 提高变焦倍率至30x
            ),
            MergeStrategy.OVERRIDE
        )

        // 微距模式调用长焦
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.macro.closeup.none.sat.tele.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 微距景深融合
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.macro.depth.of.field.fusion.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // HEIF模式下相册支持编辑背景虚化
        addPresetTag(
            VendorTagInfo(
                "com.oplus.heif.blur.edit.in.gallery.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 后置人像模式预览大小
        addPresetTag(
            VendorTagInfo(
                "com.oplus.portrait.preview.rear.sizes",
                "Int32",
                "8",
                "1280x960x960x960x1664x936x2112x960"
            ),
            MergeStrategy.OVERRIDE
        )
        addPresetTag(
            VendorTagInfo(
                "com.oplus.rear.sub.portrait.previewsize",
                "Int32",
                "8",
                "320x240x240x240x416x234x528x240"
            ),
            MergeStrategy.OVERRIDE
        )

        // 大师模式-滤镜参数预设
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.effect.style.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 大师模式-Pro-放大对焦
        addPresetTag(
            VendorTagInfo(
                "com.oplus.camera.feature.scale.focus",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 实况照片fov优化
        addPresetTag(
            VendorTagInfo(
                "com.oplus.camera.livephoto.support.fov.optimize",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 10bit照片
        addPresetTag(
            VendorTagInfo(
                "com.oplus.10bits.heic.encode.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 实况HEIF照片
        addPresetTag(
            VendorTagInfo(
                "com.oplus.camera.heif.support.livephoto",
                "Byte",
                "1",
                "0"
            ),
            MergeStrategy.OVERRIDE
        )

        // 实况10bit照片
        addPresetTag(
            VendorTagInfo(
                "com.oplus.livephoto.support.10bit",
                "Byte",
                "1",
                "0"
            ),
            MergeStrategy.OVERRIDE
        )

        // 光影有声滤镜
        addPresetTag(
            VendorTagInfo(
                "com.oplus.tol.style.filter.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // grand tour系列滤镜
        addPresetTag(
            VendorTagInfo(
                "com.oplus.support.grand.tour.filter",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 沙漠系列滤镜
        addPresetTag(
            VendorTagInfo(
                "com.oplus.desert.filter.type.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // vignette grain 滤镜
        addPresetTag(
            VendorTagInfo(
                "com.oplus.vignette.grain.filter.type.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // director 滤镜
        addPresetTag(
            VendorTagInfo(
                "com.oplus.director.filter.upgrade.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )
        addPresetTag(
            VendorTagInfo(
                "com.oplus.director.filter.rus",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 贾樟柯滤镜
        addPresetTag(
            VendorTagInfo(
                "com.oplus.support.jzk.movie.filter",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )
        addPresetTag(
            VendorTagInfo(
                "com.oplus.support.filter.watermark.list",
                "String",
                "1",
                "jzk-movie.cube.rgb.bin"
            ),
            MergeStrategy.OVERRIDE
        )

        // 新版美颜菜单
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.face.beauty.custom.menu.version",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 超级文本扫描
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.super.text.scanner.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 照片模式柔光滤镜
        addPresetTag(
            VendorTagInfo(
                "com.ocs.camera.ipu.soft.light.photo.mode.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 夜景模式柔光滤镜
        addPresetTag(
            VendorTagInfo(
                "com.ocs.camera.ipu.soft.light.night.mode.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 大师模式柔光滤镜
        addPresetTag(
            VendorTagInfo(
                "com.ocs.camera.ipu.soft.light.professional.mode.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // meishe 系列滤镜
        addPresetTag(
            VendorTagInfo(
                "com.ocs.camera.ipu.meishe.filter.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // Preview HDR
        // Require OplusRom Version >= V15.0.1
        if (isV1501()) {
            XposedBridge.log("OplusTest: V15.0.1 Device, enable Preview HDR")
            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.ai.perception.detect.support",
                    "Byte",
                    "1",
                    "0"
                ),
                MergeStrategy.OVERRIDE
            )
            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.brightness.ratio",
                    "Float",
                    "1",
                    "5"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.video.support",
                    "Byte",
                    "1",
                    "1"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.video.brightness.ratio",
                    "Float",
                    "1",
                    "5"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.capture.hdr.support",
                    "Byte",
                    "1",
                    "1"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.support",
                    "Byte",
                    "1",
                    "1"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.transform.support",
                    "Byte",
                    "1",
                    "0"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.transform.lut.video.support",
                    "Byte",
                    "1",
                    "0"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.cap.mode.value",
                    "String",
                    "6",
                    "common,night,highPixel,xpan,sticker,idPhoto"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.capture.hdr.cap.mode.value",
                    "String",
                    "7",
                    "common,portrait,night,highPixel,xpan,sticker,idPhoto"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.livephoto.support",
                    "Byte",
                    "1",
                    "1"
                ),
                MergeStrategy.OVERRIDE
            )

            addPresetTag(
                VendorTagInfo(
                    "com.oplus.camera.preview.hdr.front.portrait.support",
                    "Byte",
                    "1",
                    "0"
                ),
                MergeStrategy.OVERRIDE
            )
        }

        // 视频自动帧率
        addPresetTag(
            VendorTagInfo(
                "com.oplus.video.auto.fps.setting.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 双击音量键快捷启动相机
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.quick.launch.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 实况视频码率
        addPresetTag(
            VendorTagInfo(
                "com.oplus.camera.livephoto.video.bitrate",
                "Int32",
                "1",
                "45"
            ),
            MergeStrategy.OVERRIDE
        )

        // 停止录制立即播放提示音
        addPresetTag(
            VendorTagInfo(
                "com.oplus.video.stop.record.sound.play.immediate",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )


        // 第三方app调用官方相机时可以选择人像模式
        addPresetTag(
            VendorTagInfo(
                "com.oplus.force.portrait.when.parse.intent",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

        // 前置拍照变焦
        addPresetTag(
            VendorTagInfo(
                "com.oplus.feature.front.camera.wide.zoom.support",
                "Byte",
                "1",
                "1"
            ),
            MergeStrategy.OVERRIDE
        )

    }
}