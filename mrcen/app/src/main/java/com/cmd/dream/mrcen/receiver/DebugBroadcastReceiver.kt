package com.cmd.dream.mrcen.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.cmd.dream.mrcen.BuildConfig
import com.cmd.dream.mrcen.MainActivity

class DebugBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (!BuildConfig.DEBUG) {
            return
        }
        when(intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                Log.d(TAG, "当前电量：$level")
            }
            TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                // 来电状态
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                // 来电号码
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Toast.makeText(context, "静态接收器,电话状态改变：$state, 号码：$incomingNumber", Toast.LENGTH_SHORT).show()
            }
            else -> {
//                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//                Log.d(TAG, "当前电量：$level")
                Log.d(TAG, "静态接收器，接收到广播：${intent.action}")
                Toast.makeText(context, "静态接收器，接收到广播：${intent.action}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "BootCompleteReceiver"
    }
}
