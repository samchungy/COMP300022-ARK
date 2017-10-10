package ark.ark.Groups;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class GroupLocationUpdateService extends Service {
    public GroupLocationUpdateService() {
    }

    private static Timer timer = new Timer();
    private Context ctx;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 10000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            if(CurrentUser.getInstance().isUpdating()){
                toastHandler.sendEmptyMessage(0);
            }
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            UserRequestsUtil.updateActiveGroupLocations(ctx);
        }
    };
}
