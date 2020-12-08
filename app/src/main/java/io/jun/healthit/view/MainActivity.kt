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
import io.jun.healthit.update.UpdateManager
import io.jun.healthit.util.Setting
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var billingManager: BillingManager
    var isProVersion by Delegates.notNull<Boolean>()

    private val updateManager: UpdateManager by lazy { UpdateManager(this) }
    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        initBillingManager()
        updateManager.checkUpdate()
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

        if (requestCode == Setting.UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "Update flow failed! Result code: $resultCode")
                updateManager.checkUpdate()
            }
        }
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

    override fun onDestroy() {
        billingManager.onDestroy()
        super.onDestroy()
    }

}