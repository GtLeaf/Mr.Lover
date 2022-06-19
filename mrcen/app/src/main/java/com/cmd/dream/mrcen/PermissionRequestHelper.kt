package com.cmd.dream.mrcen

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.RequestCallback
import java.lang.Exception

object PermissionRequestHelper {
    fun request(activity: FragmentActivity) {
        val permissionList = mutableListOf (
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionList.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        }

        PermissionX.init(activity)
            .permissions(permissionList)
            .onExplainRequestReason(ExplainReasonCallback { scope, deniedList ->
                //先获取正常的权限，获取完后，走这里获取特殊权限（如悬浮窗等，必须要去系统页面手动设置）
                val message = "需要您开启以下权限才能正常使用"
                scope.showRequestReasonDialog(deniedList, message, "去开启", "拒绝")
            })
            .request(RequestCallback { allGranted, _, deniedList ->
                if (allGranted) {
//                    initEvent()
                    Toast.makeText(activity, "配置成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun requestNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val notificationManager = (context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as? NotificationManager)

        if (notificationManager?.isNotificationPolicyAccessGranted != true) {
            val settingIntent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            //加上这行会崩
            //settingIntent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(settingIntent)
        }
    }

    fun requestIgnoringBatteryOptimization(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val powerManager = (context.getSystemService(Context.POWER_SERVICE) as? PowerManager)?: return
        if (powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            return
        }
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}