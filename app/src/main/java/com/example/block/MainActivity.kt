package com.example.block

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.block.db.entity.AppDatabase
import com.example.block.db.entity.Block
import com.example.block.ui.menu.MenuFragment
import com.example.block.ui.report.ReportFragment
import com.example.block.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.net.firewall.DomainFilterRule
import com.samsung.android.knox.net.firewall.Firewall
import com.samsung.android.knox.net.firewall.FirewallResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val DEVICE_ADMIN_ADD_RESULT_ENABLE = 1

    val TAG = "MainActivity"

    private var mToggleAdminBtn: Button? = null

    private var mDPM: DevicePolicyManager? = null

    private var mDeviceAdmin: ComponentName? = null

    private var mUtilsLog: UtilsLog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            val selectedFragment: Fragment?

            when (it.itemId) {
                R.id.navigation_menu -> selectedFragment = MenuFragment.newInstance()
                R.id.navigation_report -> selectedFragment = ReportFragment.newInstance()
                R.id.navigation_settings -> selectedFragment = SettingsFragment.newInstance()
                else -> throw UnsupportedOperationException("Невозможно выбрать фрагмент для отображения")
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, selectedFragment)
            transaction.commit()
            true
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, MenuFragment.newInstance())
        transaction.commit()


        AppDatabase.instanceDatabase(this)
        readTextInDB()

        //mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager?
        /*     mDeviceAdmin = ComponentName(this, AdminReceiver::class.java)
            mUtilsLog = UtilsLog(logView, TAG)


            mToggleAdminBtn = findViewById(R.id.toggleAdmin)

            mToggleAdminBtn!!.setOnClickListener { toggleAdmin() }

            val activateLic = findViewById<Button>(R.id.license)
            activateLic.setOnClickListener { activateLicence() }

            val mLog = findViewById<Button>(R.id.log)
            mLog.setOnClickListener { logInfo() }

            val firewall = findViewById<Button>(R.id.addFirewall)
            firewall.setOnClickListener { callbackFirewall() }

            var requestButton = findViewById<Button>(R.id.reqHttp)
            requestButton.setOnClickListener { requestHttp() }*/
    }

    private fun readTextInDB() {
        val edm = EnterpriseDeviceManager.getInstance(this)

        CheckPermission.check(this)

        GlobalScope.launch {
            val db = AppDatabase.getDatabase()
            val listBlock = db.blockDao().getAll()
            if (listBlock.isNotEmpty()) {

                val firewall = edm.firewall
                val rules = ArrayList<DomainFilterRule>()

                val denyList = ArrayList<String>()

                listBlock.forEach {
                    denyList.add(it.url)
                }
                rules.add(
                    DomainFilterRule(
                        AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null),
                        denyList,
                        emptyList()
                    )
                )

                try {
                    val response = firewall.addDomainFilterRules(rules)

                    val responseEnable = firewall.enableFirewall(true)

                    //firewall.enableDomainFilterOnIptables(true)
                    if (response[0].result == FirewallResponse.Result.SUCCESS) {
                        Log.d(TAG, "SUCCESS add firewall")
                        val isReport = firewall.enableDomainFilterReport(true)
                        if (isReport.result == FirewallResponse.Result.SUCCESS) {
                            Log.d(TAG, "Report YES")
                        }
                    } else {
                        Log.d(TAG, "\nERROR add firewall")
                    }
                } catch (ex: SecurityException) {
                    Log.d(TAG, ex.message)
                }

            } else {
                db.blockDao().deleteAll()

                val stream = resources.openRawResource(R.raw.text)
                val list = ArrayList<String>()

                stream.bufferedReader().lines().forEach {
                    try {
                        db.blockDao().insert(Block(it))
                    } catch (ex: Exception) {
                        Log.e("test", it.toString())
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CheckPermission.MY_PERMISSION_CODE_KNOX_FIREWALL -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.e(TAG, "УРА!!!")
                }
            }
        }
    }

    /*private fun requestHttp() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.applevrn.ru"

        val logView = findViewById<TextView>(R.id.logview_id)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                logView.text = "Response is: ${response.substring(0, 100)}"
                queue.cache.clear()
                queue.cancelAll(this)
                queue.stop()
            },
            Response.ErrorListener { error ->

                val status = if (error.networkResponse != null) error.networkResponse.statusCode else "not"
                logView.text = "That didn't work! $status"

            })
        queue.add(stringRequest)
    }

    private fun toggleAdmin() {
        val adminState = mDPM!!.isAdminActive(mDeviceAdmin)

        if (adminState) {
            mUtilsLog!!.log(getString(R.string.deactivating_admin))
            mDPM!!.removeActiveAdmin(ComponentName(this, AdminReceiver::class.java))
            mToggleAdminBtn!!.text = getString(R.string.activate_admin)
        } else {
            try {
                mUtilsLog!!.log(getString(R.string.activating_admin))
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin)
                startActivityForResult(intent, DEVICE_ADMIN_ADD_RESULT_ENABLE)
            } catch (e: Exception) {
                mUtilsLog!!.processException(e, TAG)
            }
        }
    }

    var isLicense: Boolean = true

    private fun activateLicence() {
        if (!isLicense) {
            deactivateLicense()
            isLicense = true
            return
        }
        val licenseManager = KnoxEnterpriseLicenseManager.getInstance(this)

        try {
            // License Activation TODO Add license key to Constants.java
            licenseManager.activateLicense("KLM06-65D9B-T59GT-41885-TTD52-ZZYWR")
            mUtilsLog!!.log(resources.getString(R.string.license_progress))

        } catch (e: Exception) {
            mUtilsLog!!.processException(e, TAG)
        } finally {
            isLicense = false
        }
    }

    var flag: Boolean = true

    private fun callbackFirewall() {
        if (ContextCompat.checkSelfPermission(
                this,
                "com.samsung.android.knox.permission.KNOX_FIREWALL"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mUtilsLog!!.log("NOOO!")
        }
        val edm = EnterpriseDeviceManager.getInstance(this)
        val firewall = edm.firewall

        val rules = ArrayList<DomainFilterRule>()
        val denyList = listOf(
            "*applevrn.ru",
            "dentry.nichost.ru",
            "arn09s19-in-f3.1e100.net",
            "arn11s02-in-f10.1e100.net",
            "arn11s02-in-f14.1e100.net",
            "arn11s03-in-f4.1e100.net",
            "arn11s04-in-f3.1e100.net",
            "lr-in-f113.1e100.net",
            "arn09s20-in-f13.1e100.net",
            "muc03s13-in-f13.1e100.net"
        )

        rules.add(
            DomainFilterRule(
                AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null),
                denyList,
                emptyList()
            )
        )

        val rulesIP = arrayOfNulls<FirewallRule>(10)

        rulesIP[0] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[0]!!.ipAddress = "172.217.21.142"
        rulesIP[0]!!.portNumber = "443"
        rulesIP[0]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[1] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[1]!!.ipAddress = "216.58.211.13"
        rulesIP[1]!!.portNumber = "443"
        rulesIP[1]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[2] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[2]!!.ipAddress = "209.85.233.138"
        rulesIP[2]!!.portNumber = "443"
        rulesIP[2]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[3] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[3]!!.ipAddress = "172.217.21.164"
        rulesIP[3]!!.portNumber = "443"
        rulesIP[3]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[4] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[4]!!.ipAddress = "216.58.207.227"
        rulesIP[4]!!.portNumber = "443"
        rulesIP[4]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[5] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[5]!!.ipAddress = "172.217.21.164"
        rulesIP[5]!!.portNumber = "443"
        rulesIP[5]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[6] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[6]!!.ipAddress = "172.217.21.163"
        rulesIP[6]!!.portNumber = "443"
        rulesIP[6]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[7] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[7]!!.ipAddress = "178.210.81.251"
        rulesIP[7]!!.portNumber = "443"
        rulesIP[7]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[8] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[8]!!.ipAddress = "209.85.233.101"
        rulesIP[8]!!.portNumber = "443"
        rulesIP[8]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        rulesIP[9] = FirewallRule(RuleType.DENY, AddressType.IPV4)
        rulesIP[9]!!.ipAddress = "209.85.233.101"
        rulesIP[9]!!.portNumber = "443"
        rulesIP[9]!!.application = AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null)

        var responseEnable: FirewallResponse? = null
        if (flag) {
            try {
                val response = firewall.addDomainFilterRules(rules)
                firewall.addRules(rulesIP)

                responseEnable = firewall.enableFirewall(true)

                //firewall.enableDomainFilterOnIptables(true)
                if (response[0].result == FirewallResponse.Result.SUCCESS) {
                    mUtilsLog!!.log("\nSUCCESS add firewall")
                    val isReport = firewall.enableDomainFilterReport(true)
                    if (isReport.result == FirewallResponse.Result.SUCCESS) {
                        mUtilsLog!!.log("Report YES")
                    }
                } else {
                    mUtilsLog!!.log("\nERROR add firewall")
                }
            } catch (ex: SecurityException) {
                mUtilsLog!!.log(ex.toString())
            } finally {
                responseEnable?.message?.let { mUtilsLog!!.log(it) }
            }

            flag = false
        } else {
            try {
                val response = firewall.removeDomainFilterRules(DomainFilterRule.CLEAR_ALL)

                if (response[0].result == FirewallResponse.Result.SUCCESS) {
                    mUtilsLog!!.log("\nSUCCESS remove firewall")
                } else {
                    mUtilsLog!!.log("\nERROR remove firewall")
                }
            } catch (ex: SecurityException) {
                mUtilsLog!!.log(ex.toString())
            }
            val responseDisable = firewall.enableFirewall(false)
            mUtilsLog!!.log(responseDisable.message)
            flag = true
        }
    }

    private fun deactivateLicense() {

        // Instantiate the KnoxEnterpriseLicenseManager class to use the deactivateLicense method
        val licenseManager = KnoxEnterpriseLicenseManager.getInstance(this)

        try {
            // License deactivation
            licenseManager.deActivateLicense("KLM06-65D9B-T59GT-41885-TTD52-ZZYWR")
            mUtilsLog!!.log(resources.getString(R.string.license_deactivation))

        } catch (e: Exception) {
            mUtilsLog!!.processException(e, TAG)
        }
    }

    private fun logInfo() {
        val edm = EnterpriseDeviceManager.getInstance(this)
        val firewall = edm.firewall

        val packageNameList = listOf("com.android.chrome")

        try {
            val reports = firewall.getDomainFilterReport(null)
            if (reports.isNotEmpty()) {
                reports.forEach { mUtilsLog!!.log("\n Домен: ${it.domainUrl}, packageName: ${it.packageName}") }
            }
        } catch (ex: SecurityException) {
            mUtilsLog!!.log("Error log!")
        }
    }*/

}
