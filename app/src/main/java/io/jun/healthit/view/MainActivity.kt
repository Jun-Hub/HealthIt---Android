package io.jun.healthit.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import io.jun.healthit.R
import io.jun.healthit.billing.BillingManager
import io.jun.healthit.update.UpdateManager
import io.jun.healthit.util.Setting
import io.jun.healthit.util.isInternetConnected
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    val billingManager: BillingManager by inject {
        parametersOf(this@MainActivity, { productId: String ->
            if (productId == getString(R.string.sku_subs))
                recreate() },
            { _: Double -> },
            { showSnackbarForBillingError() })
    }
    var isProVersion = false

    private val updateManager: UpdateManager by inject {
        parametersOf(this@MainActivity, { showSnackbarForCompleteUpdate()}) }

    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isInternetConnected(applicationContext)) {
            isProVersion = billingManager.billingProcessor.isSubscribed(getString(R.string.sku_subs))
        } else {
            Snackbar.make(nav_view, getString(R.string.notice_network_connection), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.ok)) { }
                .show()
        }

        updateManager.run {
            checkUpdate()
            registerStateListener()
            //checkUpdateStaleness()
        }

        nav_view.apply {
            itemRippleColor =
                if (Build.VERSION.SDK_INT > 22) getColorStateList(R.color.color_state_list)
                else AppCompatResources.getColorStateList(context, R.color.color_state_list)
            background = ContextCompat.getDrawable(context, R.drawable.bottom_nav)
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_routine,
                R.id.navigation_memo,
                R.id.navigation_inbody,
                R.id.navigation_timer,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
    }

    private fun showSnackbarForBillingError() {
        Snackbar.make(nav_view, getString(R.string.billing_error), Snackbar.LENGTH_SHORT).show()
    }

    private fun showSnackbarForCompleteUpdate() {
        Snackbar.make(nav_view, getString(R.string.notice_update_complete), Snackbar.LENGTH_INDEFINITE).apply {
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
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - backWait >= 2000) {
            backWait = System.currentTimeMillis()
            Snackbar.make(nav_view, getString(R.string.notice_exit), 600).show()
        } else {
            finish()
        }
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