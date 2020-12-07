package io.jun.healthit.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.jun.healthit.view.MainActivity

open class BaseFragment : Fragment() {

    private val TAG = "BaseFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkProVersion((activity as MainActivity).isProVersion)
        Log.d(TAG, "checkProVersion at onViewCreated")
    }

    fun loadBannerAd(adView: AdView) {
        adView.visibility = View.VISIBLE
        MobileAds.initialize(requireContext())
        AdRequest.Builder().build().let {
            adView.loadAd(it)
        }
    }

    @CallSuper
    open fun checkProVersion(isProVersion: Boolean) {
        Log.d(TAG, "checkProVersion")
    }

}