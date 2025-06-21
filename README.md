<div align="center">

# OPCameraPro 相机功能补全

<a href="https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/releases/"><img alt="Github Releases" src="https://img.shields.io/github/v/release/Xposed-Modules-Repo/com.tlsu.opluscamerapro"><a href="https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/releases"><img alt="GitHub all releases" src="https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.tlsu.opluscamerapro/total?label=Downloads"></a> <a href="https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/Xposed-Modules-Repo/com.tlsu.opluscamerapro"></a>
<a href="http://t.me/+awg-7X5Ggrs5NzZl"><img alt="Telegram" src="https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white"></a>
</div>

### 简要介绍：
- 这是一个适用于ColorOS与realmeUI（或许还支持OxygenOS）的相机/相册增强型Xposed模块，建议ColorOS15/realmeUI6.0++，最低向下兼容ColorOS14/realmeUI5.0。
- App是一个第三方的功能模块，使用该模块App造成的任何后果作者均不会负责。

### 下载与支持
- Github Releases: https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/releases/
- Xposed Modules Repo: https://modules.lsposed.org/module/com.tlsu.opluscamerapro
- Telegram (Recommend): http://t.me/+awg-7X5Ggrs5NzZl

### 功能介绍
#### 相机功能
- 25MP

- 新版大师模式

- 大师模式 RAW MAX

- 人像模式变焦

- 720P 60FPS视频

- 前置4K视频

- 慢动作视频超广角480FPS

- 视频自动帧率

- 实况视频高码率

- 录制结束立即播放提示音

- 新版微距模式

- 微距模式调用长焦

- 微距景深融合

- 大师模式滤镜与参数预设

- 大师滤镜

- 光影有声滤镜

- Grand Tour滤镜

- 沙漠系列滤镜

- Vignette Grain滤镜

- 姜文滤镜

- 贾樟柯滤镜

- Meishe系列滤镜

- 柔光滤镜(照片模式)

- 柔光滤镜(夜景模式)

- 柔光滤镜(大师模式)

- HEIF模式编辑背景虚化

- 10bit照片

- 实况HEIF照片

- 实况10bit照片

- 实况照片FOV优化

- 预览HDR

- 大师模式放大对焦

- 新版美颜菜单

- 超级文本扫描

- 双击音量键快捷启动相机

- 第三方APP调用人像模式

- 前置拍照变焦

- 人像模式后置闪光灯

- AI 超清望远算法

- 超清长焦算法

- 杜比视频支持

- 杜比视频60fps

- 长焦杜比视频

- 前置杜比视频

- 视频录制锁定镜头

- 视频录制检测麦克风状态

- 哈苏水印指导

- 自定义哈苏水印

- 哈苏水印默认开启

- EV调节功能

- ColorOS15滤镜

- 点击变焦切换焦距

- 运动模式

- 4K 120FPS视频

- 1080P 120FPS视频

- 杜比视界120FPS视频

- 无影抓拍

- 声音聚焦

- 实况照片时长

- AI场景预设


#### 相册功能
- AI 灵感成片
- AI 消除
- AI 去拖影
- AI 画质增强
- AI 去反光
- AI 最佳表情

### 模块功能介绍
- App分为相机设置、相册设置，使用开关来控制功能的启用，
模块无法关掉你机型原本就有的功能，也就是当开关为关的时候，功能也不会关闭，当开关为开的时候，才会添加对应的功能。

- 在App中显示的功能不代表你的机型以及版本开启后就可以正常使用，如果遇到了开启某个功能后，发生了闪退卡死问题则请关闭该功能。如一加13开启4K120FPS后相机卡死，这种请不要Bug Report。
- App内有个配置文件导入导出功能，该功能就是用于降低使用调试的时间成本，你可以在调试出一套无问题的配置后导出分享到频道话题中，给同机型版本的机油快捷上手模块。

- App中的设备默认值功能：这个功能是通过Xposed Hook获取你的设备对该对应功能的默认启用情况，如果提示设备默认值为开即设备默认就支持该功能，不需要再开启开关。
如果设备默认值为等待Hook，请你正确激活模块后，使用模块设置->重启相机，接着重新打开一次相机App，然后重启模块App即可正常获取

- App的附属模块功能：对于部分功能，需要安装附属模块才能正常启用，如[预览HDR][大师模式滤镜与参数预设]。安装完整版附属模块有一定概率的闪退风险。

- 安装附属模块的方法是打开模块设置->安装附属模块->选择安装精简版或完整版（如果不清楚安装哪个版本那就请你安装精简版）->跳转到浏览器下载附属模块->到Root管理器安装模块，并重启手机

- App的附属模块切换挂载模式功能：如果出现了模块挂载的问题，可以通过这个切换挂载模式，附属模块默认为Mount--bind模式。如果你不知道这是什么，那就不需要更改。

- App的修复闪退功能：如果遇到了开启某个开关但是记不起来了，相机、相册App又不断闪退，或安装附属模块后遇到了问题，请使用这个修复闪退功能来一键解决闪退问题。

### 必要说明
- App在更新了v2.1.30后，不再有具体的系统、相机、相册版本号要求，但是建议还是让设备保持在新版本
- App需要Root权限以及LSPosed激活方可使用
- Bug Report请给我提供/data/system/dropbox内的日志，先删除这个目录中的所有文件，然后复现一次闪退，重新回到目录将里面的txt文件发给我
- 模块App请时刻保持在最新版，旧版本不会受到任何的支持。
- 如果有开启任何的隐藏Root功能，请将相机、相册App放行，具体方法如排除名单/Shamiko等等开启黑名单模式，给相机、相册App Root权限等。