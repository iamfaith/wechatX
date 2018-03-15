package wechat.com.wechatx.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Created by faith on 2018/3/16.
 */

public class CommonPreference {

    public static SharedPreferences getPreferencesAndKeepItReadable(Context ctx, String prefName) {
        SharedPreferences prefs = ctx.getSharedPreferences(prefName, ctx.MODE_PRIVATE);
        File prefsFile = new File(ctx.getFilesDir() + "/../shared_prefs/" + prefName + ".xml");
        return prefs;
    }

}
