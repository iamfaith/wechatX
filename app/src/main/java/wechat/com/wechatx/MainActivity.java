package wechat.com.wechatx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import wechat.com.wechatx.common.CommonPreference;
import wechat.com.wechatx.game.WechatGameHook;
import wechat.com.wechatx.reverse.ViewClickedHooker;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton toggleButton = findViewById(R.id.toggleButton);
//        final SharedPreferences mySharedPreferences = CommonPreference.getPreferencesAndKeepItReadable(this, "config");
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPreferences.Editor editor = mySharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean("changText", true);
                } else {
                    editor.putBoolean("changText", false);
                }
                editor.commit();
            }
        });


        final File dex = new File(Environment.getExternalStorageDirectory().toString(), "classes.apk");
        if (dex.exists()) {
            File dexOutputDir = getDir("dex", Activity.MODE_PRIVATE);
            try {
                DexClassLoader dexClassLoader = new DexClassLoader(dex.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, getApplicationContext().getClassLoader());
                Class clazz = dexClassLoader.loadClass("wechat.com.wechatgame.WechatGame");
                for (Field field : clazz.getFields()) {
                    Log.e(TAG, field.getName());
                }
            } catch (Exception e) {
                Log.e(TAG, "error", e);
            }
        }



//        final String packageName = MainActivity.class.getPackage().getName();
//        String filePath = String.format("/data/app/%s-%s.apk", packageName, 1);
//        if (!new File(filePath).exists()) {
//            filePath = String.format("/data/app/%s-%s.apk", packageName, 2);
//            if (!new File(filePath).exists()) {
//                filePath = String.format("/data/app/%s-%s/base.apk", packageName, 1);
//                if (!new File(filePath).exists()) {
//                    filePath = String.format("/data/app/%s-%s/base.apk", packageName, 2);
//                    if (!new File(filePath).exists()) {
//                        Log.e("MainActivity","Error:在/data/app找不到APK文件" + packageName);
//                        return;
//                    }
//                }
//            }
//        }
//        try {
//
//            final PathClassLoader pathClassLoader = new PathClassLoader(filePath, ClassLoader.getSystemClassLoader());
//            final Class<?> aClass = Class.forName(ViewClickedHooker.class.getCanonicalName(), true, pathClassLoader);
//            final Method aClassMethod = aClass.getMethod("handlerViewClick");
//            aClassMethod.invoke(aClass.newInstance());
//        } catch (Exception e) {
//            Log.e("MainActivity", "", e);
//        }
    }
}
