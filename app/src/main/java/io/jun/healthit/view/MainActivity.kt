package io.jun.healthit.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.snackbar.Snackbar
import io.jun.healthit.FragmentFactory
import io.jun.healthit.FragmentNavigation
import io.jun.healthit.R
import io.jun.healthit.billing.BillingManager
import io.jun.healthit.databinding.ActivityMainBinding
import io.jun.healthit.update.UpdateManager
import io.jun.healthit.util.Setting
import io.jun.healthit.util.isInternetConnected
import io.jun.healthit.viewmodel.InbodyViewModel
import io.jun.healthit.viewmodel.MemoViewModel
import io.jun.healthit.viewmodel.PrefViewModel
import io.jun.healthit.viewmodel.TimerViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val fragmentNavigation: FragmentNavigation by inject{ parametersOf(this) }
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

    private val memoViewModel: MemoViewModel by viewModel()
    private val prefViewModel: PrefViewModel by viewModel()
    private val inbodyViewModel: InbodyViewModel by viewModel()
    private val timerViewModel: TimerViewModel by viewModel()

    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()
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
                    R.id.navigation_routine -> fragmentNavigation.change(FragmentFactory.getRoutineFragment())
                    R.id.navigation_memo -> fragmentNavigation.change(FragmentFactory.getMemoFragment())
                    R.id.navigation_inbody -> fragmentNavigation.change(FragmentFactory.getInbodyFragment())
                    R.id.navigation_timer -> fragmentNavigation.change(FragmentFactory.getTimerFragment())
                    R.id.navigation_settings -> fragmentNavigation.change(FragmentFactory.getSettingsFragment())
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