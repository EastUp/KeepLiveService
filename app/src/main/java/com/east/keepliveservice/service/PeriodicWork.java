package com.east.keepliveservice.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 使用Worker定时任务
 *  @author: jamin
 *  @date: 2020/5/25
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class PeriodicWork extends Worker {
    private Context mContext;
    public PeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Result doWork() {
        // 开启定时任务，定时轮寻 ， 看MessageService有没有被杀死
        // 如果杀死了则重新启动  轮寻onStartJob
        Log.d("TAG","刷新...");
        boolean alive = serviceAlive(MessageService.class.getCanonicalName());
        if(!alive){
            mContext.startService(new Intent(mContext,MessageService.class));
        }
        return Result.success();
    }

    /**
     * 判断某个服务是否正在运行的方法
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    private boolean serviceAlive(String serviceName){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        if(runningServices.size()<=0)
            return false;
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            String serviceClassName = runningService.service.getClassName();
            if(serviceName.equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
}
