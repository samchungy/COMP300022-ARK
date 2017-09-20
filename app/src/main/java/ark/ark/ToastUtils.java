package ark.ark;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by khtin on 17/09/2017.
 */

public abstract class ToastUtils {
    public static void showToast(String message, Context context) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
