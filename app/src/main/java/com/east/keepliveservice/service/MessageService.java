package com.east.keepliveservice.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.east.keepliveservice.GuardServiceAIDL;
import com.east.keepliveservice.MainActivity;
import com.east.keepliveservice.R;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *  @description:  自己需要处理逻辑的服务
 *  @author: jamin
 *  @date: 2020/5/25 15:01
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class MessageService extends Service {

    private final int MessageId = 1;
    private static final int PUSH_NOTIFICATION_ID = (0x001);
    private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID_MESSAGE";
    private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME_MESSAGE";

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Log.e("TAG","处理逻辑");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 创建通知渠道
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //兼容8.0
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        //创建通知并显示
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,PUSH_CHANNEL_ID);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        builder.setContentTitle("逻辑通知标题")//设置通知栏标题
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                .setContentText("逻辑通知内容")
                .setNumber(1000)
                .setTicker("逻辑通知内容") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setSmallIcon(R.mipmap.ic_launcher)//设置通知小ICON
                .setChannelId(PUSH_CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManager != null) {
            notificationManager.notify(PUSH_NOTIFICATION_ID, notification);
        }

        //提高进程优先级
        startForeground(MessageId,notification);

        //绑定守护进程
        bindService(new Intent(this,GuardService.class),serviceConnection,BIND_AUTO_CREATE);

        //如果返回START_STICKY，表示Service运行的进程被Android系统强制杀掉之后，Android系统会将该Service依然设置为started状态
        // （即运行状态），但是不再保存onStartCommand方法传入的intent对象
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new GuardServiceAIDL.Stub() {};
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG","跟GuardService连接上了");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //断开的时候重新启动
            startService(new Intent(MessageService.this,GuardService.class));
            bindService(new Intent(MessageService.this,GuardService.class),this,BIND_AUTO_CREATE);
        }
    };

}
