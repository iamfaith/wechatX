package wechat.com.wechatx.common;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.FileOutputStream;

/**
 * Created by faith on 2018/3/13.
 */

public class FileUtils {



    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void writeFile(String dest, String content) {
        try(FileOutputStream fileOutputStream = new FileOutputStream(dest)) {
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {

        }
    }
}
