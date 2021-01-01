package io.jun.healthit.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.jun.healthit.FragmentNavigation
import io.jun.healthit.view.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

open class BaseFragment : Fragment() {

    private val TAG = "BaseFragment"
    private lateinit var backPressedCallback: OnBackPressedCallback
    val navigation: FragmentNavigation by inject { parametersOf(activity) }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(this, backPressedCallback)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //Fragment가 바뀌면 액티비티에 연결해줫던 backPressedCallback disEnable 해주기
        backPressedCallback.isEnabled = !hidden
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "checkProVersion at onViewCreated ${(activity as MainActivity).isProVersion}")
        checkProVersion((activity as MainActivity).isProVersion)
    }

    fun loadBannerAd(adView: AdView) {
        adView.visibility = View.VISIBLE
        context?.let { ctx ->
            MobileAds.initialize(ctx)
            AdRequest.Builder().build().let {
                adView.loadAd(it)
            }
        }
    }

    fun setActionBar(toolBar: Toolbar) {
        (activity as MainActivity).apply {
            setSupportActionBar(null)   //다른 프래그먼트에서 추가됐던 actionBar를 지워주기 위해 null넣고 시작
            setSupportActionBar(toolBar)
            supportActionBar?.title = null
        }
        setHasOptionsMenu(true)
    }

    fun setBackActionBar(toolBar: Toolbar) {
        (activity as MainActivity).apply {
            setSupportActionBar(null)
            setSupportActionBar(toolBar)
            supportActionBar?.title = null
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    @CallSuper
    open fun checkProVersion(isProVersion: Boolean) {}

    @CallSuper
    open fun onBackPressed() {}
}