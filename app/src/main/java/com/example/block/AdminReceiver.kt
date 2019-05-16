package com.example.block

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AdminReceiver : DeviceAdminReceiver() {

    private fun showToast(context: Context?, msg: CharSequence) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onEnabled(context: Context?, intent: Intent?) {
        showToast(context, "Device admin enabled")
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        showToast(context, "Device admin disabled")
    }
}