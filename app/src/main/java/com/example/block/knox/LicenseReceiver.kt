package com.example.block.knox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.block.R
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager

class LicenseReceiver : BroadcastReceiver() {

    private val DEFAULT_ERROR_CODE = -1

    private fun showToast(context: Context, msg_res: Int) {
        Toast.makeText(context, context.resources.getString(msg_res), Toast.LENGTH_SHORT).show()
    }

    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onReceive(context: Context, intent: Intent?) {

        var msg_res = -1

        if (intent == null) {
            // No intent action is available
            showToast(context, R.string.no_intent)
            return
        } else {
            val action = intent.action
            if (action == null) {
                // No intent action is available
                showToast(context, R.string.no_intent_action)
                return
            } else if (action == KnoxEnterpriseLicenseManager.ACTION_LICENSE_STATUS) {
                // Key activation result Intent is obtained
                val errorCode =
                    intent.getIntExtra(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, DEFAULT_ERROR_CODE)

                if (errorCode == KnoxEnterpriseLicenseManager.ERROR_NONE) {
                    // Key activated or deactivated successfully
                    showToast(context, R.string.klm_action_successful)
                    Log.i(
                        "LicenseReceiver", context.getString(
                            R.string.klm_action_successful
                        )
                    )
                    return
                } else {
                    // activation failed
                    when (errorCode) {
                        KnoxEnterpriseLicenseManager.ERROR_INTERNAL -> msg_res =
                            R.string.err_klm_internal
                        KnoxEnterpriseLicenseManager.ERROR_INTERNAL_SERVER -> msg_res =
                            R.string.err_klm_internal_server
                        KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE -> msg_res =
                            R.string.err_klm_licence_invalid_license
                        KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME -> msg_res =
                            R.string.err_klm_invalid_package_name
                        KnoxEnterpriseLicenseManager.ERROR_LICENSE_TERMINATED -> msg_res =
                            R.string.err_klm_licence_terminated
                        KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED -> msg_res =
                            R.string.err_klm_network_disconnected
                        KnoxEnterpriseLicenseManager.ERROR_NETWORK_GENERAL -> msg_res =
                            R.string.err_klm_network_general
                        KnoxEnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE -> msg_res =
                            R.string.err_klm_not_current_date
                        KnoxEnterpriseLicenseManager.ERROR_NULL_PARAMS -> msg_res =
                            R.string.err_klm_null_params
                        KnoxEnterpriseLicenseManager.ERROR_UNKNOWN -> msg_res =
                            R.string.err_klm_unknown
                        KnoxEnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT -> msg_res =
                            R.string.err_klm_user_disagrees_license_agreement

                        else -> {
                            // Unknown error code
                            val errorStatus = intent.getStringExtra(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_STATUS)
                            val msg = context.resources.getString(
                                R.string.err_klm_code_unknown,
                                Integer.toString(errorCode),
                                errorStatus
                            )
                            showToast(context, msg)
                            Log.e("LicenseReceiver", msg)
                            return
                        }
                    }

                    // Display KLM error message
                    showToast(context, msg_res)
                    Log.i("LicenseReceiver", context.getString(msg_res))
                    return
                }
            }
        }
    }
}