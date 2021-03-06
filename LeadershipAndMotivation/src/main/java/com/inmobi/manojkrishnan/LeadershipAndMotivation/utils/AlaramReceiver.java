package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.inmobi.manojkrishnan.LeadershipAndMotivation.LeadershipAndMotivation;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.R;

public class AlaramReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private KeyValueStore mKeyValueStore;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

       /* Intent service1 = new Intent(context, AlarmService.class);
        context.startService(service1);*/
        /*long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, LeadershipAndMotivation.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.share)
                .setContentTitle("Alaram Fired")
                .setContentText("Events To be Performed").setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(Integer.parseInt(String.valueOf(System.currentTimeMillis())), mNotifyBuilder.build());
*/
        mKeyValueStore = KeyValueStore.getInstance(context.getApplicationContext(), "QuotesCounter");
        if(mKeyValueStore.getInt("counter",0) == 0) {
            mKeyValueStore.putInt("counter", 1);
            mKeyValueStore.putLong("timeStamp", System.currentTimeMillis());
        }
        else if(mKeyValueStore.getInt("counter",0) > 0) {
            mKeyValueStore.putInt("counter", mKeyValueStore.getInt("counter", 0) + 1);
        }



        Log.d("alarm","====Broadcast Received=====");

        Context mcontext = context.getApplicationContext();
        notificationManager = (NotificationManager)mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(mcontext, LeadershipAndMotivation.class);
        pendingIntent = PendingIntent.getActivity(mcontext, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext);
        builder.setContentTitle("Quote for the Day");
        builder.setContentText("GoodMorning!!").setLights(Color.GREEN, 300, 300);
        builder.setSmallIcon(R.drawable.leadershiplogo);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        notificationManager = (NotificationManager)mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d("alarm", "====Notification sent=====");
    }

}

class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, LeadershipAndMotivation.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Bananas");
        builder.setContentText("get your bananas");
        builder.setSmallIcon(R.drawable.share);
        builder.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}