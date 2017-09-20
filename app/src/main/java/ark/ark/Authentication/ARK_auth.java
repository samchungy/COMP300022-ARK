package ark.ark.Authentication;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.PresetReverb;


public class ARK_auth extends Activity {
    public static void storeSessionId(String sessionId, Context context) {
        String preferenceName = "system_cache";
        SharedPreferences systemCache = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor cacheEditor = systemCache.edit();
        cacheEditor.putString("session_id", sessionId);

        // commit
        cacheEditor.commit();
    }

    public static void storeUserEmail(String userEmail, Context context) {
        String preferenceName = "system_cache";
        SharedPreferences systemCache = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor cacheEditor = systemCache.edit();
        cacheEditor.putString("user_email", userEmail);

        // commit
        cacheEditor.commit();
    }


    public static String fetchSessionId(Context context) {
        String preferenceName = "system_cache";
        SharedPreferences systemCache = context.getSharedPreferences(preferenceName, 0);

        String sessionId = systemCache.getString("session_id", "no session id");

        return sessionId;
    }


    public static String fetchUserEmail(Context context) {
        String preferenceName = "system_cache";
        SharedPreferences systemCache = context.getSharedPreferences(preferenceName, 0);

        String userEmail = systemCache.getString("user_email", "no user email");

        return userEmail;
    }

    public static void clearUserData(Context context) {
        String preferenceName = "system_cache";
        SharedPreferences systemCache = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor cacheEditor = systemCache.edit();
        cacheEditor.putString("session_id", null);
        cacheEditor.putString("user_email",null);

        // commit
        cacheEditor.commit();

    }


}
