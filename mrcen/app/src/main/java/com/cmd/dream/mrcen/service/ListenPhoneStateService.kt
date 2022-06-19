package com.cmd.dream.mrcen.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

class ListenPhoneStateService: Service() {
    var telephonyManager: TelephonyManager? = null
    private val telephoneListener by lazy(LazyThreadSafetyMode.NONE) {
        createPhoneStateListener()
    }

    override fun onCreate() {
        Log.d(TAG, "**********************service onCreate!!!!*******************")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = createNotification()
            startForeground(ID, notification)
        }
        registerSystemServiceListener()
        registerBroadcastReceiver()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
//        telephonyManager?: (getSystemService(TELEPHONY_SERVICE) as? TelephonyManager)?.listen(telephoneListener, PhoneStateListener.LISTEN_NONE)
    }

    private fun createPhoneStateListener() = object: PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when(state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d(TAG, "**********************监测到挂断电话!!!!*******************")
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    Toast.makeText(this@ListenPhoneStateService, "正在振铃， 电话号码：$phoneNumber", Toast.LENGTH_SHORT).show()
                    val audioManager = (this@ListenPhoneStateService.getSystemService(AUDIO_SERVICE) as? AudioManager)?: return
                    if(audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    }
                    val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVol/2, AudioManager.FLAG_SHOW_UI)
                    Log.d(TAG, "**********************监测到电话呼入!!!!**************")
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d(TAG, "**********************监测到接听电话!!!!************")
                }
                else -> {}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(channelId: String, channelName: String): String? {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        val notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?: return ""
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification? {
        val channelId = createNotificationChannel(CHANNLE_ID, CHANNLE_NAME)?: return null

        val notification = Notification.Builder(applicationContext, channelId)
            .setContentTitle("Mr.Cen")
            .setContentText("Mr.Cen is running")
            .setWhen(System.currentTimeMillis())
            .build()
//        notification.defaults = Notification.DEFAULT_SOUND
        return notification
    }

    private fun registerBroadcastReceiver() {

    }

    private fun registerSystemServiceListener() {
        telephonyManager = (getSystemService(TELEPHONY_SERVICE) as? TelephonyManager)?: return
        telephonyManager?.listen(telephoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    companion object {
        const val TAG = "ListenPhoneStateService"
        const val ID = 110
        const val CHANNLE_ID = "Mr.Cen"
        const val CHANNLE_NAME = "Mr.Cen"
    }
}