package wechat.com.wechatx.plugin;

import android.util.Log;

import de.robv.android.xposed.callbacks.XC_LoadPackage;
import wechat.com.wechatx.reverse.ViewClickedHooker;

/**
 * Created by faith on 2018/3/15.
 */

public class PluginHooker {

    private static final String TAG = "PluginHooker";

    public static void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        ViewClickedHooker.hook(loadPackageParam);
    }
}
