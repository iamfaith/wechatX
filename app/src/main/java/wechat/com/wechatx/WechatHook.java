package wechat.com.wechatx;


import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import wechat.com.wechatx.game.WechatGameHook;

public class WechatHook implements IXposedHookLoadPackage {

    private static final String TAG = "XposedMain";
    private static final String WECHAT_PKG_NAME = "com.tencent.mm";


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String currentTime = df.format(new Date());
        XposedBridge.log(currentTime + "  "+ lpparam.packageName + " [WechatHook]" + ", " + Process.myPid() + ", " + lpparam.processName);


        if (WECHAT_PKG_NAME.equals(lpparam.packageName) && lpparam.processName != null && lpparam.processName.startsWith("com.tencent.mm:appbrand")) {
            try {
                String str = ((Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0])).getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
                Log.i(TAG, lpparam.packageName + " " + str);
                if ("6.6.1".equals(str)) {
//					Log.m5a(loadPackageParam);
                } else if ("6.6.2".equals(str)) {
//					Log.m15a(loadPackageParam);
                } else if ("6.6.3".equals(str)) {
//					Log.m25a(loadPackageParam);
                } else if ("6.6.5".equals(str)) {
                    WechatGameHook.jumpHook(lpparam);
                }
            } catch (Throwable th) {
                Log.e(TAG, "" + th);
            }
        }

    }
}
