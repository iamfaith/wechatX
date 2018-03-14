package wechat.com.wechatgame;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by faith on 2018/3/14.
 */

public class WechatGame {

    private static final String TAG = "JumpHook";

    public static void invoke(XC_MethodHook.MethodHookParam methodHookParam) {
//        Activity activity = (Activity)methodHookParam.thisObject;
//        Toast.makeText(activity, "xposed也要热更新", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "热更新测试");
    }

    public static void invoke() {
        Log.i(TAG, "热更新测试");
    }
}
