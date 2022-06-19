package com.cmd.dream.mrcen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cmd.dream.mrcen.MainActivity

class BootCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?: return
        intent?: return
        //禁止后台启动activity?
//        val mainIntent = Intent(context, MainActivity::class.java)
//        mainIntent.action = Intent.ACTION_MAIN
//        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//        context.startActivity(mainIntent)
    }
}