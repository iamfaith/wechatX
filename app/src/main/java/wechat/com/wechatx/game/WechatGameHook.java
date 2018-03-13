package wechat.com.wechatx.game;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import wechat.com.wechatx.common.FileUtils;

/**
 * Created by faith on 2018/3/13.
 */

public class WechatGameHook {
    private static boolean isHookActivity = false;


    public static void jumpHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod("com.tencent.tinker.loader.app.TinkerApplication", loadPackageParam.classLoader, "onCreate", new Object[]{new jumpHookCallBack()});
        } catch (Throwable throwable) {
            Log.e("JumpHook", "" + throwable);
        }
    }

    static class jumpHookCallBack extends XC_MethodHook {
        jumpHookCallBack() {

        }

        protected void afterHookedMethod(MethodHookParam methodHookParam) {
            Application application = (Application) methodHookParam.thisObject;
            Log.i("JumpHook", "com.tencent.tinker.loader.app.TinkerApplication.onCreate " + application.getClassLoader());
            if (loadClass(application.getClassLoader())) {
                isHookActivity = true;
                hookAppbrand(application.getClassLoader());
                return;
            }
            hookActivity(application.getClassLoader());
        }
    }

    private static void hookActivity(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod("android.app.Activity", classLoader, "onCreate", new Object[]{new C00172()});
        } catch (Throwable th) {
            Log.e("JumpHook", "" + th);
        }
    }

    static class C00172 extends XC_MethodHook {
        C00172() {
        }

        protected void beforeHookedMethod(MethodHookParam methodHookParam) {
            Activity activity = (Activity) methodHookParam.thisObject;
            Log.i("JumpHook", "onCreate " + activity + ", " + activity.getClassLoader());
            if (!isHookActivity && loadClass(activity.getClassLoader())) {
                isHookActivity = true;
                hookAppbrand(activity.getClassLoader());
            }
        }
    }


    static class hookJumpGame extends XC_MethodHook {
        protected void afterHookedMethod(MethodHookParam methodHookParam) {
            Log.i("JumpHook", "a ------------- args " + methodHookParam.args[0] + ", " + methodHookParam.args[1]);
            if (methodHookParam.args[0] != null) {
                Object objectField = XposedHelpers.getObjectField(methodHookParam.args[0], "isR");
                Log.i("JumpHook", "a config " + objectField);
                if (objectField != null) {
                    if ("wx7c8d593b2c3a7703".equals((String) XposedHelpers.getObjectField(objectField, "appId"))) {
                        if (((String) methodHookParam.args[1]).contains("js")) {
                            String str = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wechatJump/" + methodHookParam.args[1];
                            Log.i("JumpHook", "a0 ------------- filePath " + str);
                            FileUtils.writeFile(str, (String) methodHookParam.getResult());
                        }
                        //hook game.js
                        if ("game.js".equals((String) methodHookParam.args[1])) {
                            String content = (String) methodHookParam.getResult();
                            if (!TextUtils.isEmpty(content)) {
                                //modify
                                content = content.replace("t?1===this.double", "if(this.double > 25) t=0; else t=1;t?1===this.double");
                                if (!TextUtils.isEmpty(content)) {
                                    methodHookParam.setResult(content);
                                }
                            }

                        }


                    } else if ("wx2fcf29c354c79113".equals((String) XposedHelpers.getObjectField(objectField, "appId"))) {
                        if (((String) methodHookParam.args[1]).contains("js")) {
                            String str = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tank/" + methodHookParam.args[1];
                            Log.i("JumpHook", "a0 ------------- filePath " + str);
                            FileUtils.writeFile(str, (String) methodHookParam.getResult());
                        }

                        //hook game.js
                        if ("game.js".equals((String) methodHookParam.args[1])) {
                            String content = (String) methodHookParam.getResult();
                            if (!TextUtils.isEmpty(content)) {
                                //modify
                                content = content.replace("this.CurrentBulletLeftNum-1", "this.CurrentBulletLeftNum");
                                //无敌
                                content = content.replace("this.Hp-e", "this.Hp");

                                if (!TextUtils.isEmpty(content)) {
                                    methodHookParam.setResult(content);
                                }
                            }

                        }
                    }

                }
            }
        }

    }


    private static void hookAppbrand(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.appbrand.appcache.ao", classLoader, "a", new Object[]{classLoader.loadClass("com.tencent.mm.plugin.appbrand.e"), String.class, new hookJumpGame()});
        } catch (Throwable th) {
            Log.e("JumpHook", "" + th);
        }
    }


    private static boolean loadClass(ClassLoader classLoader) {
        try {
            classLoader.loadClass("com.tencent.mm.plugin.appbrand.appcache.ao");
            classLoader.loadClass("com.tencent.mm.plugin.appbrand.e");
            return true;
        } catch (Exception e) {
            Log.e("JumpHook", "" + e);
            return false;
        }
    }

}
