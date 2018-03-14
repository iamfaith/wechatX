package wechat.com.wechatgame;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import dalvik.system.DexFile;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("wechat.com.wechatgame", appContext.getPackageName());

        Log.e("JumpHook", ExampleInstrumentedTest.class.getPackage().getName());
        Log.e("JumpHook", ExampleInstrumentedTest.class.getSimpleName());


//        try {
//            Log.i("JumpHook", "begin");
//            String filePath = Environment.getExternalStorageDirectory() + "/WechatGame.dex";
//            File file = new File(filePath);
//            if (!file.exists())
//                System.out.println(file + "不存在");
//            DexFile dexFile = new DexFile(file);
//            Class clazz = dexFile.loadClass("wechat.com.wechatgame.WechatGame", appContext.getClassLoader());
//            clazz.getDeclaredMethod("invoke").invoke(null);
//        } catch (Exception e) {
//            Log.e("JumpHook", "load fail" + e);
//        }
    }
}
