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
import io.jun.healthit.billing.BillingCallback
import io.jun.healthit.billing.BillingManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    companion object {
        var inKorea = true
    }

    lateinit var billingManager: BillingManager
    var isProVersion by Delegates.notNull<Boolean>()

    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nav_view.apply {
            itemRippleColor =
                if (Build.VERSION.SDK_INT > 22) getColorStateList(R.color.color_state_list)
                else AppCompatResources.getColorStateList(context, R.color.color_state_list)
            background = if (Build.VERSION.SDK_INT > 22) ContextCompat.getDrawable(
                context,
                R.drawable.bottom_nav
            )
            else resources.getDrawable(R.drawable.bottom_nav)
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

        val language =
            if (Build.VERSION.SDK_INT >= 24)
                resources.configuration.locales.get(0).language
            else
                resources.configuration.locale.language

        inKorea = language == "ko"

        getVersionInfo()
        initBillingManager()
    }

    private fun initBillingManager() {
        billingManager = BillingManager(this, object : BillingCallback {
            override fun onPurchased(productId: String?) {
                if (productId == getString(R.string.sku_subs))
                    recreate()
            }

            override fun onUpdatePrice(price: Double?) {}

            override fun onBillingError() {
                Snackbar.make(nav_view, getString(R.string.billing_error), Snackbar.LENGTH_SHORT).show()
            }
        })

        isProVersion = billingManager.billingProcessor.isSubscribed(getString(R.string.sku_subs))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        billingManager.handleActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "$requestCode // $resultCode")
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - backWait >=2000 ) {
            backWait = System.currentTimeMillis()
            Snackbar.make(nav_view, getString(R.string.notice_exit), 600).show()
        } else {
            finish()
        }
    }

    private fun getVersionInfo() {
        val info = packageManager.getPackageInfo(packageName, 0)
        Log.e(TAG, "" + info.versionName)
    }

    override fun onDestroy() {
        billingManager.onDestroy()
        super.onDestroy()
    }

}