package com.cmd.dream.mrcen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.cmd.dream.mrcen.PermissionRequestHelper

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?: return
        Toast.makeText(context, "接收到广播：${intent.action}", Toast.LENGTH_SHORT).show()

        (context as? FragmentActivity)?.apply {
            PermissionRequestHelper.request(this)
        }
    }
}