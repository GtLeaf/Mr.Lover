package com.cmd.dream.mrcen

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cmd.dream.mrcen.service.ListenPhoneStateService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var audioManager: AudioManager? = null
    var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioManager = (getSystemService(Service.AUDIO_SERVICE) as? AudioManager)
        notificationManager = (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
        requestPermissions()
        initListener()
//        initService()
//        notificationManager.setAutomaticZenRuleState()
    }

    private fun initListener() {
        btn_vol_up.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager?.isNotificationPolicyAccessGranted != true) {
                val settingIntent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(settingIntent)
            }
            if(audioManager?.ringerMode == AudioManager.RINGER_MODE_SILENT) {
                audioManager?.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
            val maxVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_RING)?: 1
            audioManager?.setStreamVolume(AudioManager.STREAM_RING, maxVol, AudioManager.FLAG_SHOW_UI)
        }

        btn_vol_down.setOnClickListener {
            audioManager?.setStreamVolume(AudioManager.STREAM_RING, 1, AudioManager.FLAG_SHOW_UI)
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            PermissionRequestHelper.requestIgnoringBatteryOptimization(this)
            PermissionRequestHelper.requestNotificationPermission(this)
            PermissionRequestHelper.request(this)
        }else{
            //6.0之前不需要动态申请权限
            initEvent()
        }
    }

    private fun initService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ListenPhoneStateService::class.java))
        } else {
            startService(Intent(this, ListenPhoneStateService::class.java))
        }
    }

    private fun initEvent() {

    }
}
