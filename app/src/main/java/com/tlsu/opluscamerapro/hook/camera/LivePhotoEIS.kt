package com.tlsu.opluscamerapro.hook.camera

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.safeClassLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.tlsu.opluscamerapro.hook.BaseHook
import com.tlsu.opluscamerapro.utils.DeviceCheck.checkVersionName
import com.tlsu.opluscamerapro.utils.DexKit.dexKitBridge
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Field


object LivePhotoEIS: BaseHook() {
    private lateinit var lpparam: XC_LoadPackage.LoadPackageParam
    private var sVersionName: String = ""

    override fun init() {
        try {
            XposedHelpers.findAndHookMethod(
                Application::class.java,
                "onCreate",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {

                        if (sVersionName.isNotEmpty()) {
                            return
                        }

                        val appContext = param.thisObject as Context

                        try {
                            val pm = appContext.packageManager
                            val packageInfo = pm.getPackageInfo("com.oplus.camera", 0)
                            sVersionName = packageInfo.versionName as String

                            XposedBridge.log("OPCameraPro: get Camera Version Name: $sVersionName")

                            if (checkVersionName(sVersionName, 46)) {
                                XposedBridge.log("OPCameraPro: hook LivePhotoEIS")
                                val bridge = dexKitBridge
                                val livePhotoClass = bridge.findClass {
                                    matcher {
                                        methods {
                                            add {
                                                usingStrings("updateGalleryLivePhotoFovMeta, sGalleryLivePhotoFovMeta:")
                                            }
                                        }
                                    }
                                }.single()

                                XposedBridge.log("OPCameraPro: find LivePhotoEIS Class: ${livePhotoClass.name}")

                                loadClass(livePhotoClass.name)
                                    .methodFinder()
                                    .filterStatic()
                                    .filterByReturnType(Boolean::class.java)
                                    .filterByName("g")
                                    .single()
                                    .createHook {
                                        returnConstant(true)
                                    }
                                XposedBridge.log("OPCameraPro: LivePhotoEIS hooked successfully!")
                            }

                        } catch (e: PackageManager.NameNotFoundException) {
                            XposedBridge.log("OPCameraPro: Failed to get package info: " + e.message)
                        } catch (e: Throwable) {
                            XposedBridge.log("OPCameraPro: find LivePhotoEIS error! ${e.message}")
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Throwable) {
            XposedBridge.log("OPCameraPro: Failed to set up Application.onCreate hook: ${e.message}")
        }

        try {
            XposedHelpers.findAndHookMethod(
                "tc.v2",  // 目标类名
                safeClassLoader,  // 使用应用的类加载器
                "i",  // 目标方法名
                "oc.m",  // 第一个参数的类型
                "oc.m",  // 第二个参数的类型
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        // 3. 在原始方法执行前，获取它的第二个参数 mVar2
                        val mVar2 = param.args[1]
                            ?: return  // 安全检查

                        // 4. 使用 XposedHelpers 反射获取 mVar2 的 'n' 字段 (oc.j 对象)
                        val jVar = XposedHelpers.getObjectField(mVar2, "n")

                        // 5. 核心逻辑：判断 'n' 字段是否为 null
                        if (jVar == null) {
                            // 如果为 null，这就是导致崩溃的场景 (EIS关闭)
                            XposedBridge.log("Camera Hook: Detected null oc.j object. Injecting a default one.")

                            // 6. 创建一个 oc.j 类的实例
                            val jClass = XposedHelpers.findClass("oc.j", safeClassLoader)


                            // ================== [新的对象创建方法] ==================
                            // 1. 获取 Unsafe 类
                            val unsafeClass = Class.forName("sun.misc.Unsafe")

                            // 2. 获取 a private static a field named "theUnsafe"
                            val theUnsafeField: Field = unsafeClass.getDeclaredField("theUnsafe")
                            theUnsafeField.setAccessible(true)
                            val unsafeInstance: Any = theUnsafeField.get(null)

                            // 3. 获取 Unsafe 类中的 a public method named allocateInstance
                            val allocateInstanceMethod = unsafeClass.getMethod(
                                "allocateInstance",
                                Class::class.java
                            )

                            // 4. 调用方法来创建我们的对象，不经过构造函数
                            val newJVar = allocateInstanceMethod.invoke(unsafeInstance, jClass)


                            // =========================================================

                            // 后续逻辑保持不变，为新对象的字段设置默认值
                            XposedHelpers.setObjectField(newJVar, "n", floatArrayOf(1.0f, 1.0f))
                            XposedHelpers.setObjectField(newJVar, "l", floatArrayOf(1.0f, 1.0f))
                            XposedHelpers.setObjectField(newJVar, "m", floatArrayOf(0.0f, 0.0f))
                            XposedHelpers.setObjectField(
                                newJVar,
                                "h",
                                floatArrayOf(1920.0f, 1080.0f)
                            )

                            XposedHelpers.setObjectField(mVar2, "n", newJVar)
                            XposedBridge.log("Camera Hook: Default oc.j object injected successfully using Unsafe.")

                            XposedBridge.log("Camera Hook: Default oc.j object injected successfully.")
                        }
                    }
                }
            )


            XposedBridge.log("CameraFix: Successfully hooked tc.v2.i()")
        } catch (t: Throwable) {
            // 如果因为应用更新导致类名或方法名找不到，会抛出异常
            XposedBridge.log("CameraFix: Failed to hook method. App might have been updated.")
            XposedBridge.log(t)
        }
    }

    fun setLoadPackageParam(param: XC_LoadPackage.LoadPackageParam) {
        lpparam = param
    }
}

object LivePhotoEISHook: BaseHook() {
    override fun init() {
        LivePhotoEIS.init()
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.oplus.camera") {
            LivePhotoEIS.setLoadPackageParam(lpparam)
            init()
        }
    }
}
