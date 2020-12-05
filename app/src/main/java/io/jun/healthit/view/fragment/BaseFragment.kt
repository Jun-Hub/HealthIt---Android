package io.jun.healthit.view.fragment

import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

open class BaseFragment : Fragment() {

    fun loadBannerAd(adView: AdView) {
        MobileAds.initialize(requireContext())
        AdRequest.Builder().build().let {
            adView.loadAd(it)
        }
    }

}