package com.cmd.dream.mrcen.receiver

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.cmd.dream.mrcen.utils.SettingStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PhoneStateReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?: return
        context?: return
//        Toast.makeText(context, "接收到广播：${intent.action}", Toast.LENGTH_SHORT).show()

        val manager = (context.getSystemService(Service.TELEPHONY_SERVICE) as? TelephonyManager)?: return
        // 来电状态
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        // 来电号码
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

          when(manager.callState) {
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.d(TAG, "**********************监测到挂断电话!!!!*******************")
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                if (incomingNumber != targetPhoneNumber) return
                val audioManager = (context.getSystemService(Service.AUDIO_SERVICE) as? AudioManager)?: return
                if(audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
                val currentVol = runBlocking {
                    val vol = SettingStoreManager(context).getIntData(SettingStoreManager.VOL_KEY).first() - 1
                    vol
                }
//                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
                audioManager.setStreamVolume(AudioManager.STREAM_RING, currentVol, AudioManager.FLAG_SHOW_UI)
                Log.d(TAG, "**********************监测到电话呼入!!!!**************")
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.d(TAG, "**********************监测到接听电话!!!!************")
            }
            else -> {}
        }
    }

    /*override fun onReceive(context: Context?, intent: Intent?) {
        val telephonyManager = (context?.getSystemService(Service.TELEPHONY_SERVICE) as? TelephonyManager)?: return
        telephonyManager.listen(object: PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when(state) {
                    TelephonyManager.CALL_STATE_IDLE -> {}
                    TelephonyManager.CALL_STATE_RINGING -> {
                        Toast.makeText(context, "接收到电话", Toast.LENGTH_SHORT).show()
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {}
                    else -> {}
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }*/

    companion object {
        const val TAG = "PhoneStateReceiver"
        var targetPhoneNumber = "17377523811"
    }
}