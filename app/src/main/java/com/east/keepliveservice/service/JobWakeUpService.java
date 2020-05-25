package com.east.keepliveservice.service;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 2秒轮询检查服务是否开启
 *                  在Android7.0以后JobService无法正常按照设置的执行周期执行后台任务。
 *                  最小时间周期不能小于15分钟
 *  @author: jamin
 *  @date: 2020/5/25
 * |---------------------------------------------------------------------------------------------------------------|
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobWakeUpService extends JobService {

    private final int jobWakeUpId = 1;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        JobInfo jobInfo = new JobInfo.Builder(jobWakeUpId, new ComponentName(this, JobWakeUpService.class))
                .setPeriodic(2000)//2秒轮询一次
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int schedule = jobScheduler.schedule(jobInfo);
        Log.e("TAG",schedule+"");

        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // 开启定时任务，定时轮寻 ， 看MessageService有没有被杀死
        // 如果杀死了则重新启动  轮寻onStartJob

        boolean alive = serviceAlive(MessageService.class.getCanonicalName());
        if(!alive){
            startActivity(new Intent(this,MessageService.class));
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /**
     * 判断某个服务是否正在运行的方法
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    private boolean serviceAlive(String serviceName){
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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
