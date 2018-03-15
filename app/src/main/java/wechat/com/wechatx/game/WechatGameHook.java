package wechat.com.wechatx.game;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import wechat.com.wechatx.BuildConfig;
import wechat.com.wechatx.WechatHook;
import wechat.com.wechatx.common.FileUtils;

/**
 * Created by faith on 2018/3/13.
 */

public class WechatGameHook {
    private static boolean isHookActivity = false;

    public static void handleLoadPackage4release(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Log.i("JumpHook", "热更新测试aaa");
        try {
            XposedBridge.log("------------5");
            XposedHelpers.findAndHookMethod("com.tencent.tinker.loader.app.TinkerApplication", loadPackageParam.classLoader, "onCreate", new Object[]{new jumpHookCallBack()});
        } catch (Throwable throwable) {
            Log.e("JumpHook", "" + throwable);
        }
    }

    //for test
    public static void handleLoadPackage4release() {
        Log.i("JumpHook", "热更新测试");
    }

    public static void jumpHook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            //在发布时，直接调用即可。
            if (!BuildConfig.DEBUG) {
                handleLoadPackage4release(loadPackageParam);
                return;
            }
            //在调试模式为了不频繁重启，使用反射的方式调用自身的指定函数。

            final String packageName = WechatHook.class.getPackage().getName();
            String filePath = String.format("/data/app/%s-%s.apk", packageName, 1);
            if (!new File(filePath).exists()) {
                filePath = String.format("/data/app/%s-%s.apk", packageName, 2);
                if (!new File(filePath).exists()) {
                    filePath = String.format("/data/app/%s-%s/base.apk", packageName, 1);
                    if (!new File(filePath).exists()) {
                        filePath = String.format("/data/app/%s-%s/base.apk", packageName, 2);
                        if (!new File(filePath).exists()) {
                            XposedBridge.log("Error:在/data/app找不到APK文件" + packageName);
                            return;
                        }
                    }
                }
            }


            final PathClassLoader pathClassLoader = new PathClassLoader(filePath, ClassLoader.getSystemClassLoader());
            final Class<?> aClass = Class.forName(WechatGameHook.class.getCanonicalName(), true, pathClassLoader);
            final Method aClassMethod = aClass.getMethod("handleLoadPackage4release", XC_LoadPackage.LoadPackageParam.class);
            aClassMethod.invoke(aClass.newInstance(), loadPackageParam);

        } catch (final Exception e) {
            XposedBridge.log(e);
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
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            XposedBridge.log("------------beforeHookedMethod");
            Log.i("JumpHook", "beforeHookedMethod");

        }

        public static String b(String str) {
            int indexOf = str.indexOf(".BOTTLE.bodyDepth");
//            g.a("JumpHook", "TTLE.bodyDepth ... " + indexOf);
            if (indexOf < 1) {
                return null;
            }
            String substring = str.substring(indexOf - 1, indexOf);
//            g.a("JumpHook", "confuseClass ..." + substring + "...");
            if (TextUtils.isEmpty(substring)) {
                return null;
            }
            String str2 = "this.combo=new ";
            int indexOf2 = str.indexOf(str2);
            String str3 = substring + ".GAME";
            int indexOf3 = str.indexOf(str3);
            int indexOf4 = str.indexOf("scene.add");
            String str4 = "this.renderer.render(this.scene,this.camera)";
            int indexOf5 = str.indexOf(str4);
            int indexOf6 = str.indexOf("bottle.status");
            int indexOf7 = str.indexOf("mouseDownTime");
            String str5 = substring + ".BOTTLE";
            int indexOf8 = str.indexOf(str5);
            String str6 = substring + ".BLOCK";
            int indexOf9 = str.indexOf(str6);
            substring = null;
            if (indexOf2 != -1) {
                int indexOf10 = str.indexOf(".", str2.length() + indexOf2);
                if (indexOf10 != -1) {
                    substring = str.substring(str2.length() + indexOf2, indexOf10);
//                    g.a("JumpHook", "hookConfusedJs className ..." + substring + "...");
                    substring.trim();
                }
            }
            int i = -1;
            if (!TextUtils.isEmpty(substring)) {
                i = str.indexOf(substring + ".Mesh");
            }
//            g.a("JumpHook", "hookConfusedJs  ---index1 " + indexOf2 + ", " + indexOf3 + ", " + indexOf4 + ", " + indexOf5 + ", " + indexOf6 + ", " + indexOf7 + ", " + indexOf8 + ", " + i + ", " + indexOf9 + ", ..." + substring + "...");
            if (TextUtils.isEmpty(substring) || indexOf2 == -1 || indexOf3 == -1 || indexOf4 == -1 || indexOf5 == -1 || indexOf6 == -1 || indexOf7 == -1 || indexOf8 == -1 || i == -1 || indexOf9 == -1) {
                return null;
            }
            substring = str.substring(0, indexOf2) + ("this.vectorHelperOne=new " + substring + ".Vector2(0, 0);this.vectorHelperTwo=new " + substring + ".Vector2(0, 0);this.helperLine=new " + substring + ".Line();this.helperLine.material.color.setHex(0x0000ff);var pointsOfHelperLine=new Float32Array(6);this.helperLine.geometry.addAttribute(\"position\",new " + substring + ".BufferAttribute(pointsOfHelperLine,3));this.helperLine.geometry.attributes.position.setDynamic(true);this.helperLine.name=\"helper_line\";this.scene.add(this.helperLine);this.helperArrow=new " + substring + ".Mesh(new " + substring + ".CircleGeometry(.6, 50),new " + substring + ".MeshBasicMaterial({color: 255}));this.helperArrow.name=\"helper_arrow\";this.helperArrow.position.x=-500;this.helperArrow.rotation.x=-Math.PI/2;this.scene.add(this.helperArrow);") + str.substring(indexOf2, str.length());
            i = substring.indexOf(str4);
            return substring.substring(0, i) + ("if(\"prepare\" == this.bottle.status) {var i=(Date.now()-this.mouseDownTime)/1e3;var vz=Math.min(i*" + str5 + ".velocityZIncrement,150);vz=+vz.toFixed(2);var vy=Math.min(" + str5 + ".velocityY+i*" + str5 + ".velocityYIncrement,180);vy=+vy.toFixed(2);this.vectorHelperOne.set(this.nextBlock.obj.position.x-this.bottle.obj.position.x,this.nextBlock.obj.position.z-this.bottle.obj.position.z);this.vectorHelperOne.x=+this.vectorHelperOne.x.toFixed(2);this.vectorHelperOne.y=+this.vectorHelperOne.y.toFixed(2);var r=vy/" + str3 + ".gravity*2;var n=this.bottle.obj.position.y.toFixed(2);var a=" + str6 + ".height/2-n;r=+(r-=+((-vy+Math.sqrt(Math.pow(vy,2)-2*" + str3 + ".gravity*a))/-" + str3 + ".gravity).toFixed(2)).toFixed(2);var s=[];this.vectorHelperTwo.set(this.bottle.obj.position.x,this.bottle.obj.position.z);var l=this.vectorHelperOne.setLength(vz * r);this.vectorHelperTwo.add(l);s.push(+this.vectorHelperTwo.x.toFixed(2),+this.vectorHelperTwo.y.toFixed(2));this.helperArrow.position.set(s[0],this.nextBlock.obj.position.y+" + str6 + ".height/2+.15,s[1]);var array=this.helperLine.geometry.attributes.position.array;array[0]=this.currentBlock.obj.position.x;array[1]=this.currentBlock.obj.position.y+" + str6 + ".height/2+.15;array[2]=this.currentBlock.obj.position.z;array[3]=s[0];array[4]=this.nextBlock.obj.position.y+" + str6 + ".height/2+.15;array[5]=s[1];this.helperLine.geometry.computeBoundingSphere();this.helperLine.geometry.attributes.position.needsUpdate=true;}else{this.helperArrow.position.set(-300,0,0);var array=this.helperLine.geometry.attributes.position.array;array[0]=-300;array[1]=0;array[2]=0;array[3]=-500;array[4]=0;array[5]=0;this.helperLine.geometry.computeBoundingSphere();this.helperLine.geometry.attributes.position.needsUpdate = true;}") + substring.substring(i, substring.length());
        }

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
                                content = b(content);
                                content = content.replace("t?1===this.double", "if (Math.random() < 0.6 && t === 0) t=1; t?1===this.double");
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
