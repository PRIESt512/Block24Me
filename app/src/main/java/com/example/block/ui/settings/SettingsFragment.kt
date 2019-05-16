package com.example.block.ui.settings

import android.app.admin.DevicePolicyManager
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.example.block.R

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    val TAG = "SettingsFragment"

    private lateinit var viewModel: SettingsViewModel

    private lateinit var mDPM: DevicePolicyManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)
        val switch: Switch = view.findViewById(R.id.switchAdmin)
        //mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        /*toggle.setOnCheckedChangeListener { _, isChecked ->
            while (isChecked) {
                true ->
                false ->
            }
        }*/

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
