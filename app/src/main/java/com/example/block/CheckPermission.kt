package com.example.block

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CheckPermission private constructor() {
    companion object {
        @JvmStatic
        private val TAG = "CheckPermission"

        @JvmStatic
        private val PERMISSION = "com.samsung.android.knox.permission.KNOX_FIREWALL"

        val MY_PERMISSION_CODE_KNOX_FIREWALL = 567

        fun check(context: Context) {
            if (ContextCompat.checkSelfPermission(
                    context.applicationContext,
                    PERMISSION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "Необходимые разрешения не предоставлены. Запрашиваю")
                /*   if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, PERMISSION)) {

                   }*/
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(PERMISSION),
                    MY_PERMISSION_CODE_KNOX_FIREWALL
                )
            }
        }
    }
}