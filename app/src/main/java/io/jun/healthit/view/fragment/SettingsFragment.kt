package io.jun.healthit.view.fragment

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.preference.PreferenceManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.jun.healthit.R
import io.jun.healthit.databinding.FragmentSettingsBinding
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.view.MainActivity
import io.jun.healthit.viewmodel.PrefViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "SettingsFragment"
    private val prefViewModel: PrefViewModel by sharedViewModel()
    private val dialogUtil: DialogUtil by inject{ parametersOf(activity) }
    
    private var viewBinding: FragmentSettingsBinding? = null
    private val binding get() = viewBinding!!

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlayerInit = false

    private lateinit var preferences: SharedPreferences
    private lateinit var prefChangeListener: OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            setView(it)
        }
    }

    override fun checkProVersion(isProVersion: Boolean) {
        super.checkProVersion(isProVersion)
        if(isProVersion) {
            binding.apply {
                buttonFreeTrial.visibility = View.GONE
                adView.visibility = View.GONE
            }
            return
        }

        binding.run {
            buttonFreeTrial.visibility = View.VISIBLE
            loadBannerAd(adView)
        }
    }

    private fun registerPrefChangeListener(context: Context) {
        getString(R.string.admob_app_id)
        prefChangeListener = OnSharedPreferenceChangeListener {_, key ->
            binding.let {
                when(key) {
                    "alert" -> it.prefAlertValue.text = prefEntryConverter(prefViewModel.getAlertSettings())
                    "ring" -> {
                        val ring = prefViewModel.getRingSettings()
                        it.prefRingValue.text = prefEntryConverter(ring)

                        mediaPlayer = when(ring) {
                            "light_weight_babe" -> MediaPlayer.create(this.context, R.raw.light_weight_babe)
                            "ring1" -> MediaPlayer.create(context, R.raw.ring1)
                            "ring2" -> MediaPlayer.create(context, R.raw.ring2)
                            "ring3" -> MediaPlayer.create(context, R.raw.ring3)
                            "ring4" -> MediaPlayer.create(context, R.raw.ring4)
                            "ring5" -> MediaPlayer.create(context, R.raw.ring5)
                            "ring6" -> MediaPlayer.create(context, R.raw.ring6)
                            else -> MediaPlayer.create(context, R.raw.ring7)
                        }

                        mediaPlayer.start()
                        isPlayerInit = true
                    }
                    "tag1" -> it.prefTag1Value.text = prefViewModel.getOneOfTagSettings(1)
                    "tag2" -> it.prefTag2Value.text = prefViewModel.getOneOfTagSettings(2)
                    "tag3" -> it.prefTag3Value.text = prefViewModel.getOneOfTagSettings(3)
                    "tag4" -> it.prefTag4Value.text = prefViewModel.getOneOfTagSettings(4)
                    "tag5" -> it.prefTag5Value.text = prefViewModel.getOneOfTagSettings(5)
                    "tag6" -> it.prefTag6Value.text = prefViewModel.getOneOfTagSettings(6)
                }
            }
        }

        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    private fun prefEntryConverter(prefValue: String) =
        when (prefValue) {
            "silent" -> getString(R.string.silent)
            "vibrate" -> getString(R.string.vibrate)
            "ring" -> getString(R.string.ring)
            "vibrate_and_ring" -> getString(R.string.vibrate_and_ring)
            "light_weight_babe" -> getString(R.string.light_weight_babe)
            "ring1" -> getString(R.string.ring1)
            "ring2" -> getString(R.string.ring2)
            "ring3" -> getString(R.string.ring3)
            "ring4" -> getString(R.string.ring4)
            "ring5" -> getString(R.string.ring5)
            "ring6" -> getString(R.string.ring6)
            else -> getString(R.string.ring7)
        }


    private fun openAppInPlayStore() {
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)

        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = if (Build.VERSION.SDK_INT >= 21) {
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            flags or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        goToMarketIntent.addFlags(flags)

        try {
            startActivity(goToMarketIntent, null)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context?.packageName))

            startActivity(intent, null)
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            registerPrefChangeListener(it)
        }
    }

    override fun onStop() {
        super.onStop()
        if(isPlayerInit) mediaPlayer.release()
        preferences.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }

    inner class Permission(context: Context, value:Boolean) {

        private var overlayPermission: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {    //permission 허가 상태라면
                prefViewModel.setFloatingSettings(value)
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(context, getString(R.string.deny_overlay), Toast.LENGTH_LONG).show()
                binding.switchFloating.isChecked = false
            }
        }
        fun checkAlertWindowPermission() {
            TedPermission.with(context)
                .setPermissionListener(overlayPermission)
                .setRationaleMessage(getString(R.string.request_overlay))
                .setDeniedMessage(getString(R.string.deny_request))
                .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check()
        }
    }

    private fun setView(context: Context) {
        binding.apply {
            prefAlertValue.text = prefEntryConverter(prefViewModel.getAlertSettings())
            prefRingValue.text = prefEntryConverter(prefViewModel.getRingSettings())

            switchFloating.isChecked = prefViewModel.getFloatingSettings()
            switchFloating.setOnCheckedChangeListener { _, isChecked ->
                Permission(context, isChecked).checkAlertWindowPermission()
            }

            prefTag1Value.text = prefViewModel.getOneOfTagSettings(1)
            prefTag2Value.text = prefViewModel.getOneOfTagSettings(2)
            prefTag3Value.text = prefViewModel.getOneOfTagSettings(3)
            prefTag4Value.text = prefViewModel.getOneOfTagSettings(4)
            prefTag5Value.text = prefViewModel.getOneOfTagSettings(5)
            prefTag6Value.text = prefViewModel.getOneOfTagSettings(6)

            buttonFreeTrial.setOnClickListener(this@SettingsFragment)
            suggestionBoard.setOnClickListener(this@SettingsFragment)
            errorBoard.setOnClickListener(this@SettingsFragment)
            rateApp.setOnClickListener(this@SettingsFragment)
            prefAlert.setOnClickListener(this@SettingsFragment)
            prefRing.setOnClickListener(this@SettingsFragment)
            prefFloating.setOnClickListener(this@SettingsFragment)
            prefTemplate.setOnClickListener(this@SettingsFragment)
            prefTag1.setOnClickListener(this@SettingsFragment)
            prefTag2.setOnClickListener(this@SettingsFragment)
            prefTag3.setOnClickListener(this@SettingsFragment)
            prefTag4.setOnClickListener(this@SettingsFragment)
            prefTag5.setOnClickListener(this@SettingsFragment)
            prefTag6.setOnClickListener(this@SettingsFragment)
        }
    }

    override fun onClick(v: View?) {
            when (v?.id) {
                R.id.button_free_trial -> {
                    context?.let {
                        dialogUtil.showPurchaseProDialog(
                            { (activity as MainActivity).billingManager.subscribe() },
                            { (activity as MainActivity).billingManager.onPurchaseHistoryRestored() }
                        )
                    }
                }
                R.id.suggestion_board -> {
                    val uri = Uri.parse("https://healthit.modoo.at/?link=2proi1a4")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                R.id.error_board -> {
                    val uri = Uri.parse("https://healthit.modoo.at/?link=bwbyxw2q")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                R.id.rate_app -> { openAppInPlayStore() }
                R.id.pref_alert -> { dialogUtil.showAlertDialog() }
                R.id.pref_ring -> { dialogUtil.showRingDialog() }
                R.id.pref_floating -> { binding.switchFloating.toggle() }
                R.id.pref_template -> { dialogUtil.showTemplateDialogForSet( navigation) }
                R.id.pref_tag1 -> { dialogUtil.showTagDialog( 1, layoutInflater) }
                R.id.pref_tag2 -> { dialogUtil.showTagDialog( 2, layoutInflater) }
                R.id.pref_tag3 -> { dialogUtil.showTagDialog( 3, layoutInflater) }
                R.id.pref_tag4 -> { dialogUtil.showTagDialog( 4, layoutInflater) }
                R.id.pref_tag5 -> { dialogUtil.showTagDialog( 5, layoutInflater) }
                R.id.pref_tag6 -> { dialogUtil.showTagDialog( 6, layoutInflater) }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigation.back()
    }
}

