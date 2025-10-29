<div align="center">

# OPCameraPro 相机功能补全

<a href="https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/releases/"><img alt="Github Releases" src="https://img.shields.io/github/v/release/Xposed-Modules-Repo/com.tlsu.opluscamerapro"><a href="https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/releases"><img alt="GitHub all releases" src="https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.tlsu.opluscamerapro/total?label=Downloads"></a> <a href="https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/Xposed-Modules-Repo/com.tlsu.opluscamerapro"></a>
<a href="http://t.me/+awg-7X5Ggrs5NzZl"><img alt="Telegram" src="https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white"></a>
</div>

### 简要介绍：
- 这是一个适用于ColorOS与realmeUI（或许还支持OxygenOS）的相机/相册增强型Xposed模块，建议ColorOS16或RealmeUI7.0，最低向下兼容ColorOS15/realmeUI6.0。
- App是一个第三方的功能模块，使用该模块App造成的任何后果作者均不会负责。

### 下载与支持
- Github Releases: https://github.com/Xposed-Modules-Repo/com.tlsu.opluscamerapro/releases/
- Xposed Modules Repo: https://modules.lsposed.org/module/com.tlsu.opluscamerapro
- Telegram (Recommend): http://t.me/+awg-7X5Ggrs5NzZl

### 功能介绍
#### 相机功能
- 太多了，请下载后自行查看，模块会自动根据你的机型、版本匹配可能可用的功能

#### 相册功能
- AI 灵感成片
- AI 消除
- AI 去拖影
- AI 画质增强
- AI 去反光
- AI 最佳表情
- AI 人像补光
- LUMO 水印
- 哈苏水印
- GR 水印

### 模块功能介绍
- App分为相机设置、相册设置，使用开关来控制功能的启用，
模块无法关掉你机型原本就有的功能，也就是当开关为关的时候，功能也不会关闭，当开关为开的时候，才会添加对应的功能。

- 在App中显示的功能不代表你的机型以及版本开启后就可以正常使用，如果遇到了开启某个功能后，发生了闪退卡死问题则请关闭该功能。如一加13开启4K120FPS后相机卡死，这种请不要Bug Report。
- App内有个配置文件导入导出功能，该功能就是用于降低使用调试的时间成本，你可以在调试出一套无问题的配置后导出分享到频道话题中，给同机型版本的机油快捷上手模块。

- App中的设备默认值功能：这个功能是通过Xposed Hook获取你的设备对该对应功能的默认启用情况，
如果设备默认值为等待Hook，请你正确激活模块后，使用模块设置->重启相机，接着重新打开一次相机App，然后重启模块App即可正常获取

- App的修复闪退功能：如果遇到了开启某个开关但是记不起来了，相机、相册App又不断闪退，或安装附属模块后遇到了问题，请使用这个修复闪退功能来一键解决闪退问题。

### 必要说明
- App在更新了v2.1.30后，不再有具体的系统、相机、相册版本号要求，但是建议还是让设备保持在新版本
- App需要Root权限以及LSPosed激活方可使用
- Bug Report请给我提供LSPosed导出日志
- 模块App请时刻保持在最新版，旧版本不会受到任何的支持
- 如果有开启任何的隐藏Root功能，请将相机、相册App放行，具体方法如排除名单/Shamiko等等开启黑名单模式或者给相机、相册App Root权限等