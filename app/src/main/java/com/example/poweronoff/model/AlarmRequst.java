package com.example.poweronoff.model;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.security.SecureRandom;


public class AlarmRequst {
    Context mContext;
    private static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";

    private static final String ACTION_REQUEST_SHUTDOWN
            = "com.android.internal.intent.action.REQUEST_SHUTDOWN";
    private static final String ACTION_SET_POWEROFF_ALARM =
            "org.codeaurora.poweroffalarm.action.SET_ALARM";
    private static final String ACTION_CANCEL_POWEROFF_ALARM =
            "org.codeaurora.poweroffalarm.action.CANCEL_ALARM";
    private static final String POWER_OFF_ALARM_PACKAGE =
            "com.qualcomm.qti.poweroffalarm";
    private static final String TIME = "time";

    public AlarmRequst(Context mContext) {
        this.mContext = mContext;
    }
    public void setPowerON(Long time) {
        Log.d("Request","powe on intent time:"+time);
        //发送广播
        Intent powerOnIntent = new Intent(ACTION_SET_POWEROFF_ALARM);
        powerOnIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        powerOnIntent.setPackage(POWER_OFF_ALARM_PACKAGE);
        powerOnIntent.putExtra(TIME, time);
        mContext.sendBroadcast(powerOnIntent);
    }
    /**
     * 设备关机. <br/>
     * 需要系统APP：android:sharedUserId="android.uid.system" <br/>
     * 需要权限：uses-permission android:name="android.permission.SHUTDOWN" <br/>
     */
    public void setPowerOFF(Long time) {
        Log.d("Request","powe off intent time:"+time);
        Intent intent = new Intent(ACTION_REQUEST_SHUTDOWN);
        //是否显示关机提示dialog，true显示false不显示直接关机
        intent.putExtra(EXTRA_KEY_CONFIRM, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //获取闹钟的id，给定时设置一个随机id，这样可以设置多个定时器，否则会被覆盖掉
        int alarmId = getIntRandom();
        long intervalMillis = 24 * 3600 * 1000;
        Log.d("Request","powe off alarmId:"+alarmId);
        PendingIntent pi = PendingIntent.getActivity(mContext, alarmId, intent,  PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);

        //设置重复定时，intervalMillis重复定时的间隔时间。根据自己的需求来设置定时
        //manager.setRepeating(AlarmManager.RTC_WAKEUP, time, intervalMillis, pi);

        //设置一次定时，根据自己的需求来设置定时
       manager.set(AlarmManager.RTC_WAKEUP, time, pi);

    }
    private  int  getIntRandom()  {
        SecureRandom secureRandom =new SecureRandom();;
        secureRandom.setSeed(10000L);
        return secureRandom.nextInt(1000);

    }
}
