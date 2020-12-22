package io.jun.healthit.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.snackbar.Snackbar
import io.jun.healthit.FragmentNavigation
import io.jun.healthit.FragmentProvider
import io.jun.healthit.R
import io.jun.healthit.billing.BillingManager
import io.jun.healthit.databinding.ActivityMainBinding
import io.jun.healthit.update.UpdateManager
import io.jun.healthit.util.Setting
import io.jun.healthit.util.isInternetConnected
import io.jun.healthit.view.fragment.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    val fragmentNavigation = FragmentNavigation(this)
    val billingManager: BillingManager by inject {
        parametersOf(this@MainActivity, { productId: String ->
            if (productId == getString(R.string.sku_subs))
                recreate() },
            { _: Double -> },
            { showSnackbarForBillingError() })
    }
    var isProVersion = false

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val updateManager: UpdateManager by inject {
        parametersOf(this@MainActivity, { showSnackbarForCompleteUpdate()}) }

    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(isInternetConnected(applicationContext)) {
            isProVersion = billingManager.billingProcessor.isSubscribed(getString(R.string.sku_subs))
        } else {
            Snackbar.make(binding.navView, getString(R.string.notice_network_connection), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.ok)) { }
                .show()
        }

        updateManager.run {
            checkUpdate()
            registerStateListener()
            //checkUpdateStaleness()
        }

        initNavigationView()
    }

    private fun initNavigationView() {

        binding.navView.apply {
            itemRippleColor =
                if (Build.VERSION.SDK_INT > 22)
                    getColorStateList(R.color.color_state_list)
                else
                    AppCompatResources.getColorStateList(context, R.color.color_state_list)

            selectedItemId = R.id.navigation_memo

            setOnNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.navigation_routine -> fragmentNavigation.replace(FragmentProvider.ROUTINE_FRAGMENT)
                    R.id.navigation_memo -> fragmentNavigation.replace(FragmentProvider.MEMO_FRAGMENT)
                    R.id.navigation_inbody -> fragmentNavigation.replace(FragmentProvider.INBODY_FRAGMENT)
                    R.id.navigation_timer -> fragmentNavigation.replace(FragmentProvider.TIMER_FRAGMENT)
                    R.id.navigation_settings -> fragmentNavigation.replace(FragmentProvider.SETTINGS_FRAGMENT)
                }
                true
            }
        }
    }

    private fun showSnackbarForBillingError() {
        Snackbar.make(binding.navView, getString(R.string.billing_error), Snackbar.LENGTH_SHORT).show()
    }

    private fun showSnackbarForCompleteUpdate() {
        Snackbar.make(binding.navView, getString(R.string.notice_update_complete), Snackbar.LENGTH_INDEFINITE).apply {
            setAction(getString(R.string.restart)) { updateManager.completeUpdate() }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        billingManager.handleActivityResult(requestCode, resultCode, data)

        if (requestCode == Setting.UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "Update flow failed! Result code: $resultCode")
                //updateManager.checkUpdate()
            }
        }
    }

    override fun onBackPressed() {
        fragmentNavigation.back()
    }

    fun finishApp() =
        if(System.currentTimeMillis() - backWait >= 2000) {
            backWait = System.currentTimeMillis()
            Snackbar.make(binding.navView, getString(R.string.notice_exit), 600).show()
        } else {
            finish()
        }


    override fun onStop() {
        super.onStop()
        updateManager.unregisterStateListener()
    }

    override fun onDestroy() {
        billingManager.onDestroy()
        super.onDestroy()
    }

}