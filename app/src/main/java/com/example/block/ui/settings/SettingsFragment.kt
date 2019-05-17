package com.example.block.ui.settings

import android.app.admin.DevicePolicyManager
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.example.block.AdminReceiver
import com.example.block.R
import com.example.block.knox.License
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val TAG = "SettingsFragment"

    private val ADMIN_ADD_RESULT_ENABLE = 1

    private lateinit var viewModel: SettingsViewModel

    private lateinit var mDPM: DevicePolicyManager

    private lateinit var mDeviceAdmin: ComponentName

    private lateinit var licenseManager: KnoxEnterpriseLicenseManager

    private lateinit var viewFragment: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewFragment = inflater.inflate(R.layout.settings_fragment, container, false)

        mDPM = activity!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mDeviceAdmin = ComponentName(viewFragment.context, AdminReceiver::class.java)
        licenseManager = KnoxEnterpriseLicenseManager.getInstance(viewFragment.context)

        val switchAdmin = viewFragment.findViewById<SwitchCompat>(R.id.switchAdmin)
        if (mDPM.isAdminActive(mDeviceAdmin)) {
            switchAdmin.toggle()
            Log.i(TAG, "Проверка прав администратора - права администратора предоставлены")
        }
        switchAdmin.setOnCheckedChangeListener(this::switchAdminRules)

        //TODO: сделать запись об успешности активации в SQLite
        val switchKnox = viewFragment.findViewById<SwitchCompat>(R.id.switchKnox)
        switchKnox.setOnCheckedChangeListener(this::switchKnox)

        return viewFragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    /**
     * Активация/деактивация прав администратора приложения
     */
    private fun switchAdminRules(button: CompoundButton, isChecked: Boolean) {
        GlobalScope.launch {
            if (isChecked) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    mDeviceAdmin
                )
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Предоставьте права администратора для корректной работы приложения"
                )
                startActivityForResult(intent, ADMIN_ADD_RESULT_ENABLE)
                Log.d(TAG, "Включение предоставления прав администратора")
            } else {
                mDPM.removeActiveAdmin(ComponentName(viewFragment.context, AdminReceiver::class.java))
            }

        }
    }

    /**
     * Активация/деактивация KNOX-лицензии на конкретном устройстве
     */
    private fun switchKnox(button: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            Log.i(TAG, "Активация лицензии KNOX")
            licenseManager.activateLicense(License.getLicenseCode())
        } else {
            Log.i(TAG, "Деактивация лицензии KNOX")
            licenseManager.deActivateLicense(License.getLicenseCode())
        }
    }
}
