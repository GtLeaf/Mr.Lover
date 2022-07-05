package com.cmd.dream.mrcen

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.cmd.dream.mrcen.service.ListenPhoneStateService
import com.cmd.dream.mrcen.utils.SettingStoreManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    var audioManager: AudioManager? = null
    var notificationManager: NotificationManager? = null
    private val settingManager = SettingStoreManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioManager = (getSystemService(Service.AUDIO_SERVICE) as? AudioManager)
        notificationManager = (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
        requestPermissions()
        initView()
        initListener()
//        initService()
//        notificationManager.setAutomaticZenRuleState()
    }

    private fun initView() {
        updateVolText()
    }

    private fun initListener() {
        btn_vol_up.setOnClickListener {
            checkRingCheck()
//            val maxVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_RING)?: 1
            val newVol = runBlocking {
                val vol = settingManager.getIntData(SettingStoreManager.VOL_KEY).first() + 1
                audioManager?.setStreamVolume(AudioManager.STREAM_RING, vol, AudioManager.FLAG_SHOW_UI)
                settingManager.saveInt(SettingStoreManager.VOL_KEY, vol)
                vol
            }
            updateVolText(newVol)
        }

        btn_vol_down.setOnClickListener {
            checkRingCheck()
            val string = runBlocking {
                settingManager.getSetting().first()
            }
            val newVol = runBlocking {
                val vol = settingManager.getIntData(SettingStoreManager.VOL_KEY).first() - 1
                audioManager?.setStreamVolume(AudioManager.STREAM_RING, vol, AudioManager.FLAG_SHOW_UI)
                settingManager.saveInt(SettingStoreManager.VOL_KEY, vol)
                vol
            }
            updateVolText(newVol)
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            PermissionRequestHelper.request(this)
            PermissionRequestHelper.requestIgnoringBatteryOptimization(this)
            PermissionRequestHelper.requestNotificationPermission(this)
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

    private fun updateVolText(volLevel: Int = -1) {
        val currentVol = when {
            (volLevel >= 0) -> volLevel
            //todo 想办法减少调用--cenmingdi
            (getStoreValue() >= 0) -> getStoreValue()
            else -> audioManager?.getStreamVolume(AudioManager.STREAM_RING)?: 0
        }

        tv_vol.text = "当前音量为：$currentVol"
    }

    private fun getStoreValue(): Int {
        return runBlocking {
            settingManager.getIntData(SettingStoreManager.VOL_KEY).first()
        }
    }

    private fun checkRingCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager?.isNotificationPolicyAccessGranted != true) {
            val settingIntent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(settingIntent)
        }
        if(audioManager?.ringerMode == AudioManager.RINGER_MODE_SILENT) {
            audioManager?.ringerMode = AudioManager.RINGER_MODE_NORMAL
        }
    }
}
