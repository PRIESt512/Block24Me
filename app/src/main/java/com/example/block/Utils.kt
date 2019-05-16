package com.example.block

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.TextView
import com.samsung.android.knox.EnterpriseDeviceManager

class Utils(private val textView: TextView, private val TAG: String) {

    /** Check Knox API level on device, if it does not meet minimum requirement, end user
     * cannot use the applciation  */
    fun checkApiLevel(apiLevel: Int, context: Context) {
        if (EnterpriseDeviceManager.getAPILevel() < apiLevel) {
            val builder: AlertDialog.Builder
            builder = AlertDialog.Builder(context)
            val msg =
                context.resources.getString(R.string.api_level_message, EnterpriseDeviceManager.getAPILevel(), apiLevel)
            builder.setTitle(R.string.app_name)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(
                    "CLOSE"
                ) { dialog, which -> System.exit(0) }
                .show()

        } else {
            return
        }
    }

    /** Log results to a textView in application UI  */
    fun log(text: String) {
        textView.append(text)
        textView.append("\n\n")
        textView.invalidate()
        Log.d(TAG, text)
    }

    /** Process the exception  */
    fun processException(ex: Exception?, TAG: String) {
        if (ex != null) {
            // present the exception message
            val msg = ex.javaClass.canonicalName + ": " + ex.message
            textView.append(msg)
            textView.append("\n\n")
            textView.invalidate()
            Log.e(TAG, msg)
        }
    }
}
