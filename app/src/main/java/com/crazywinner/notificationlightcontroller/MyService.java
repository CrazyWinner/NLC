package com.crazywinner.notificationlightcontroller;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.PowerManager;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;



/**
 * mertcan tarafindan 16.01.2017 tarihinde olusturuldu.
 */

public class MyService extends NotificationListenerService {
ArrayList<String> packages;

SqlController SQL;
boolean isScreenOn=true;
int notId=1;
Notification noti;
NotificationManager nm;
    BroadcastReceiver myRecevier1;
    PendingIntent pi;
    AlarmManager amg;

int suanki=0;
BroadcastReceiver receiver;

   //listen for notifications
    @Override
    public void onNotificationPosted(StatusBarNotification statusBar) {


        //only do necessary things if notification is not from this application
        if(!statusBar.getPackageName().equals(getPackageName())){

        if(SQL.contains(statusBar.getPackageName())){
            if(!packages.contains(statusBar.getPackageName()))
            packages.add(statusBar.getPackageName());
            //if screen is not on set current showing light to 0 and call step function
            if(!isScreenOn){
                suanki=0;
                 step();
            }

        }}


    }
    @Override
    public void onNotificationRemoved(StatusBarNotification statusBar){
        //cancel current alarm
        amg.cancel(pi);
        if(packages.contains(statusBar.getPackageName())){
            packages.remove(statusBar.getPackageName());
        }
        if(packages.isEmpty()){
            //if we have no notification to show just dismiss the notification
            nm.cancel(notId);
        }else if(!isScreenOn){
            //if screen is not on and we have a notification to show set current light to 0 and call step function
            suanki=0;
            step();
        }
        super.onNotificationRemoved(statusBar);

    }

private void startNotification(){
    suanki=0;
    isScreenOn=false;
    boolean sa=getSharedPreferences(getPackageName(),MODE_PRIVATE).getBoolean("showReaded",false);
    //if readed notification setting is ticked get notifications if it's not just clear the cache
    if(sa){
        setColors();
        if(packages.size()>0)
         step();

    }else{
        packages.clear();

    }
    Log.i("selam",""+sa);





}

private void stopNotification(){
    //stop everything
    amg.cancel(pi);
    nm.cancel(notId);
    Log.i("str","stop");
    isScreenOn=true;

}

    private void setColors(){
        //clear current notifications
          packages.clear();
        //get active notifications
        StatusBarNotification[] notifications=getActiveNotifications();
        //set their colors
        for(int i =0;i<notifications.length;i++){
            String packageName = notifications[i].getPackageName();
            if(SQL.contains(packageName))
                if(!packages.contains(packageName))
            packages.add(packageName);
        }


    }


@Override
    public void onListenerConnected(){
    //create an intent for broadcast receiver
    Intent i=new Intent();
    i.setAction("KEEP_ALIVE_SERVICE_NLC");
    //create pending intent
    pi=PendingIntent.getBroadcast(this,0,i,0);
     myRecevier1=new MyReceiver();
    IntentFilter filter=new IntentFilter();
    filter.addAction("KEEP_ALIVE_SERVICE_NLC");
    //register receiver
    registerReceiver(myRecevier1,filter);
    packages=new ArrayList<>();
    filter=new IntentFilter();
    amg=(AlarmManager)getSystemService(ALARM_SERVICE);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    filter.addAction(Intent.ACTION_SCREEN_ON);
    SQL=new SqlController(this);
    //create another broadcast receiver for screen on and off actions
    receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Intent.ACTION_SCREEN_OFF:
                    startNotification();

                    break;
                case Intent.ACTION_SCREEN_ON:
                    stopNotification();

                    break;

            }

        }
    };
    //register it too
    registerReceiver(receiver,filter);
    nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    //create persistent notification
    noti= new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notification")
            .setLights(0xFFff0000,800,800)


            .setPriority(Notification.PRIORITY_HIGH)
            .setAutoCancel(true)
            //.setDefaults(Notification.DEFAULT_ALL)
            .setVibrate(new long[] {0, 0, 0,0 })
            .build();


    noti.flags= Notification.FLAG_SHOW_LIGHTS;


}

    void setLedColor(int color,int dur){
         //change persistent notification's variables and notify it again

        if(!isScreenOn)
        {
            noti.ledARGB=color;
            noti.ledOnMS=dur;
            noti.ledOffMS=dur;
        nm.notify(notId,noti);}
    }

    private void alarmKur(long delay){
        //create an alarm for next step
         amg.cancel(pi);
          Log.i("int","alarm"+delay);
        amg.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+delay*1000,pi);


    }
    public void step(){
        //create an wakelock we need it for just milliseconds so it wont effect battery life
        PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
        PowerManager.WakeLock myWakelock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakelock");
        myWakelock.acquire();


        //if current notification is in our list just set light for it and increment it to next notification
        if(suanki<packages.size()){

            ColorPackage colorPackage=SQL.getColor(packages.get(suanki));
            setLedColor(colorPackage.color,colorPackage.duration);
            if(packages.size()>1){
                alarmKur(Integer.parseInt(getSharedPreferences(getPackageName(),MODE_PRIVATE).getString("notificationDuration","20")));
            }
            suanki++;

        }
        else{
            if(suanki!=0){
                suanki=0;
                ColorPackage colorPackage=SQL.getColor(packages.get(suanki));
                setLedColor(colorPackage.color,colorPackage.duration);
                if(packages.size()>1){
                    alarmKur(Integer.parseInt(getSharedPreferences(getPackageName(),MODE_PRIVATE).getString("notificationDuration","20")));
                }

            }
        }
        //release wakelock do not forget it ever
        myWakelock.release();
    }
    @Override
    public void onDestroy(){
        //unregister receivers
        unregisterReceiver(receiver);
          unregisterReceiver(myRecevier1);
    }

    public class MyReceiver extends BroadcastReceiver{

        //listen intent from alarmmanager
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("step","sss");
         step();
        }
    }
}
