package com.example.tag.realofflineexample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.TimerTask;

public class NewDataService extends TimerTask {

    public Context c;
    MySharedPreference sharedPreference;
    String url = MyApplication.base_url + "items.php";

    public NewDataService(Context c) {
        this.c = c;
    }



    @Override
    public void run() {

        sharedPreference = new MySharedPreference(c);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JsonArrayRequest req = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if(response.length()> Integer.parseInt(sharedPreference.getDataString("count"))){
                                    sharedPreference.SetData("count",response.length()+"");
                                    Intent intent1 = new Intent(c, MainActivity.class);
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
                                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(c)
                                                    .setSmallIcon(R.drawable.icon)
                                                    .setContentTitle("تطبيق أدوات كهربائية")
                                                    .setContentText("تم اضافة منجات جديده")
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);
                                    NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                                    mNotificationManager.notify(2, mBuilder.build());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
                MyApplication.getInstance().addToRequestQueue(req, "items");
            }
        }).start();
    }
}
