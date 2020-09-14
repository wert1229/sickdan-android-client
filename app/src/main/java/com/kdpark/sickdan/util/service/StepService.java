package com.kdpark.sickdan.util.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.work.WorkManager;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.view.IntroActivity;
import com.kdpark.sickdan.view.SigninActivity;

import java.util.Calendar;

public class StepService extends Service implements SensorEventListener {

    private MyBinder mMyBinder = new MyBinder();

    class MyBinder extends Binder { //바인드 클래스를 생성
        StepService getService() { // 서비스 객체를 리턴
            return StepService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBinder;
    }

    private final String CHANNEL_ID = "default";
    private final String CHANNEL_NAME = "defaultName";


    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private int todayStep;
    private String today;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        WorkManager.getInstance(this).enqueue(UploadStepWork.getWork());

        today = CalendarUtil.calendarToString(Calendar.getInstance(), "yyyyMMdd");
        sp = getSharedPreferences(SharedDataUtil.STEP_INFO, MODE_PRIVATE);
        todayStep = sp.getInt(today, 0);
        SharedDataUtil.setData(SharedDataUtil.TODAY_COUNT, "" + todayStep);

        runForegroundService();
    }

    private void runForegroundService() {
        Intent notificationIntent = new Intent(this, IntroActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification notification = builder
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_app)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.ticker_text))
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSensors();
        return START_STICKY;
    }

    public void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepCountSensor != null) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unRegistManager();
        return super.onUnbind(intent);
    }


    public void unRegistManager() {
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_STEP_DETECTOR) return;

        todayStep++;
        SharedDataUtil.setData(SharedDataUtil.TODAY_COUNT, "" + todayStep);
        sp.edit().putInt(today, todayStep).apply();

        String newToday = CalendarUtil.calendarToString(Calendar.getInstance(), "yyyyMMdd");
        if (!today.equals(newToday)) {
            today = newToday;
            todayStep = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
