package com.example.duan1_nhom8.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.duan1_nhom8.views.OTPActivity;
import com.example.duan1_nhom8.R;
;

public class OtpIntentService extends Service {
    public OtpIntentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Integer opt1 = intent.getIntExtra("opt1", 0);
        Integer opt2 = intent.getIntExtra("opt2", 0);
        Integer opt3 = intent.getIntExtra("opt3", 0);
        Integer opt4 = intent.getIntExtra("opt4", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notifly", "Thông báo", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent notifylIntent = new Intent(this, OTPActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notifylIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat
                .Builder(this, "notifly")
                .setContentTitle("BOOK STORE")
                .setContentText("Mã OTP của bạn là: " + opt1 + opt2 + opt3 + opt4)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}