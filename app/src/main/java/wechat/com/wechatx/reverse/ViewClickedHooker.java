package wechat.com.wechatx.reverse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import wechat.com.wechatx.BuildConfig;
import wechat.com.wechatx.WechatHook;
import wechat.com.wechatx.plugin.PluginHooker;

/**
 * Created by faith on 2018/3/15.
 */

public class ViewClickedHooker {

    private static final String TAG = "ViewClickedHooker";

    public static void handlerViewClick() {
//        Log.e(TAG, View.class.getCanonicalName());
    }

    public static void handlerViewClick(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod(View.class.getCanonicalName(), loadPackageParam.classLoader, "onTouchEvent", new Object[]{MotionEvent.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.thisObject instanceof TextView) {
                    final TextView targetView = (TextView) param.thisObject;
                    if (!targetView.isClickable()) {
                        Log.e(TAG, "ViewClickedHooker-- change text");

                        MotionEvent event = (MotionEvent) param.args[0];
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            String oldContent = targetView.getText().toString();
                            Log.e(TAG, "ViewClickedHooker-- !ACTION_DOWN" + oldContent);
                            Context context = targetView.getContext();
                            final EditText editText = new EditText(context);
                            editText.setText(oldContent);
                            new AlertDialog.Builder(context).setView(editText).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    targetView.setText(editText.getText().toString());
                                }
                            }).show();


                        }
                    }
                }
            }
        }});
    }

    public static void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
//                    ViewClickedHooker.handlerViewClick(param);
            Log.e(TAG, "ViewClickedHooker-- afterHookedMethod");
            //在发布时，直接调用即可。
            if (!BuildConfig.DEBUG) {
                ViewClickedHooker.handlerViewClick(loadPackageParam);
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
            final Class<?> aClass = Class.forName(ViewClickedHooker.class.getCanonicalName(), true, pathClassLoader);
            final Method aClassMethod = aClass.getMethod("handlerViewClick", XC_LoadPackage.LoadPackageParam.class);
            aClassMethod.invoke(aClass.newInstance(), loadPackageParam);
        } catch (final Exception e) {
            XposedBridge.log(e);
        }


    }

}
