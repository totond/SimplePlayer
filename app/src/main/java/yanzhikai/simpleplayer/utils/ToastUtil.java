package yanzhikai.simpleplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/18
 * desc   :
 */

public class ToastUtil {
    public static void makeShortToast(final Context context, final String str){
        if (Looper.getMainLooper() == Looper.myLooper()){
            Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
        }else if (context instanceof Activity){
            Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
