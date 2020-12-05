package io.jun.healthit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import io.jun.healthit.R
import io.jun.healthit.billing.BillingCallback
import io.jun.healthit.billing.BillingManager
import kotlinx.android.synthetic.main.activity_billing.*

class BillingActivity : AppCompatActivity() {

    private val TAG = "BillingActivty"
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)

        billingManager = BillingManager(this, object: BillingCallback {
            override fun onPurchased(productId: String?) {
                if(productId==getString(R.string.sku_subs))
                    Toast.makeText(baseContext, "구독해주셔서 감사합니다", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(baseContext, "구매해주셔서 감사합니다", Toast.LENGTH_SHORT).show()
            }

            override fun onUpdatePrice(prices: Pair<Double?, Double?>?) {
                prices?.let {
                    inapp_text.text = it.first.toString()
                    sub_text.text = it.second.toString()
                }
            }

        })

        btn_sub.setOnClickListener { billingManager.subscribe() }
        btn_inapp.setOnClickListener { billingManager.purchase() }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        billingManager.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        billingManager.handleActivityResult(requestCode, resultCode, data)
    }
}