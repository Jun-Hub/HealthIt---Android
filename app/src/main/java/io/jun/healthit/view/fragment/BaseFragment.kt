package io.jun.healthit.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import io.jun.healthit.R
import io.jun.healthit.billing.BillingCallback
import io.jun.healthit.billing.BillingManager
import kotlin.properties.Delegates

open class BaseFragment : Fragment() {

    private val TAG = "BaseFragment"

    lateinit var billingManager: BillingManager
    var isProVersion by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingManager = BillingManager(requireActivity(), object : BillingCallback {
            override fun onPurchased(productId: String?) {
                Log.d(TAG, "onPurchased")
                if (productId == getString(R.string.sku_subs))
                    Snackbar.make(requireView(), "구독해주셔서 감사합니다", Snackbar.LENGTH_SHORT).show()
                else
                    Snackbar.make(requireView(), "구매해주셔서 감사합니다", Snackbar.LENGTH_SHORT).show()
            }

            override fun onUpdatePrice(prices: Pair<Double?, Double?>?) {
            }

            override fun onBillingError() {
                //TODO 이쪽에 로그 안찍힘;;
                Log.d(TAG, "onBillingError")
                Snackbar.make(requireView(), "구매가 정상적으로 이루어지지 않았습니다.", Snackbar.LENGTH_SHORT).show()
            }
        })

        isProVersion = billingManager.billingProcessor.isSubscribed(context?.getString(R.string.sku_subs))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkProVersion(isProVersion)
    }

    fun loadBannerAd(adView: AdView) {
        adView.visibility = View.VISIBLE
        MobileAds.initialize(requireContext())
        AdRequest.Builder().build().let {
            adView.loadAd(it)
        }
    }

    open fun checkProVersion(isProVersion: Boolean) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        billingManager.handleActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        billingManager.onDestroy()
    }

}