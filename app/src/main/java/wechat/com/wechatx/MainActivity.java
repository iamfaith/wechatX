package wechat.com.wechatx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import wechat.com.wechatx.game.WechatGameHook;
import wechat.com.wechatx.reverse.ViewClickedHooker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String packageName = MainActivity.class.getPackage().getName();
        String filePath = String.format("/data/app/%s-%s.apk", packageName, 1);
        if (!new File(filePath).exists()) {
            filePath = String.format("/data/app/%s-%s.apk", packageName, 2);
            if (!new File(filePath).exists()) {
                filePath = String.format("/data/app/%s-%s/base.apk", packageName, 1);
                if (!new File(filePath).exists()) {
                    filePath = String.format("/data/app/%s-%s/base.apk", packageName, 2);
                    if (!new File(filePath).exists()) {
                        Log.e("MainActivity","Error:在/data/app找不到APK文件" + packageName);
                        return;
                    }
                }
            }
        }
        try {

            final PathClassLoader pathClassLoader = new PathClassLoader(filePath, ClassLoader.getSystemClassLoader());
            final Class<?> aClass = Class.forName(ViewClickedHooker.class.getCanonicalName(), true, pathClassLoader);
            final Method aClassMethod = aClass.getMethod("handlerViewClick");
            aClassMethod.invoke(aClass.newInstance());
        } catch (Exception e) {
            Log.e("MainActivity", "", e);
        }
    }
}
