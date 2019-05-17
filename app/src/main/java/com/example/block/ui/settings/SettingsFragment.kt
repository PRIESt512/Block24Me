package com.example.block.ui.settings

import android.app.admin.DevicePolicyManager
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.example.block.AdminReceiver
import com.example.block.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val TAG = "SettingsFragment"

    private val DEVICE_ADMIN_ADD_RESULT_ENABLE = 1

    private lateinit var viewModel: SettingsViewModel

    private lateinit var mDPM: DevicePolicyManager

    private lateinit var mDeviceAdmin: ComponentName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        mDPM = activity!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mDeviceAdmin = ComponentName(view.context, AdminReceiver::class.java)

        val switch: SwitchCompat = view!!.findViewById(R.id.switchAdmin)
        if (mDPM.isAdminActive(mDeviceAdmin)) {
            switch.toggle()
        }
        switch.setOnCheckedChangeListener(this::switchAdminRules)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

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
                startActivityForResult(intent, DEVICE_ADMIN_ADD_RESULT_ENABLE)
            } else {
                mDPM.removeActiveAdmin(ComponentName(view!!.context, AdminReceiver::class.java))
            }
        }
    }
}
