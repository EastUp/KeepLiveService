package com.east.keepliveservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.east.keepliveservice.service.GuardService
import com.east.keepliveservice.service.JobWakeUpService
import com.east.keepliveservice.service.MessageService

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

    }
}
