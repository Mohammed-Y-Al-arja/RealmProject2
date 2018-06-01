package com.example.tag.realofflineexample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class MobilesService extends Service {

    Timer timer = new Timer();
    public static Boolean serviceRunning = false;
    TimerTask task = new NewDataService(MobilesService.this);
    public MobilesService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serviceRunning = true;
                try {
                    timer.scheduleAtFixedRate(task, 0, 5000);
                } catch (Exception x){}
            }
        }).start();

        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
