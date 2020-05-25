package com.east.keepliveservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.east.keepliveservice.service.GuardService
import com.east.keepliveservice.service.JobWakeUpService
import com.east.keepliveservice.service.MessageService
import com.east.keepliveservice.service.PeriodicWork
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(Intent(this,MessageService::class.java))
            //启动守护服务
            startForegroundService(Intent(this,GuardService::class.java))
        }else{
            startService(Intent(this,MessageService::class.java))
            //启动守护服务
            startService(Intent(this,GuardService::class.java))
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //启动JobService
            startService(Intent(this,JobWakeUpService::class.java))
        }

        //使用worker进行保活
        val request = PeriodicWorkRequestBuilder<PeriodicWork>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(request)

    }
}
