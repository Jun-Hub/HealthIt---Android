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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.jun.healthit.R
import io.jun.healthit.databinding.FragmentSettingsBinding
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.util.Setting
import io.jun.healthit.viewmodel.PrefViewModel

class SettingsFragment : BaseFragment(), View.OnClickListener {

    private lateinit var prefViewModel: PrefViewModel
    
    private var viewBinding: FragmentSettingsBinding? = null
    private val binding get() = viewBinding!!

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlayerInit = false

    private lateinit var preferences: SharedPreferences
    private lateinit var prefChangeListener: OnSharedPreferenceChangeListener

    private lateinit var switchFloating:Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)
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
        loadBannerAd(binding.adView)

        setView()
    }

    private fun registerPrefChangeListener() {
        prefChangeListener = OnSharedPreferenceChangeListener {_, key ->
            binding.let {
            when(key) {
                "alert" -> it.prefAlertValue.text = prefEntryConverter(prefViewModel.getAlertSettings(requireContext()))
                "ring" -> {
                    val ring = prefViewModel.getRingSettings(requireContext())
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
                "tag1" -> it.prefTag1Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 1)
                "tag2" -> it.prefTag2Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 2)
                "tag3" -> it.prefTag3Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 3)
                "tag4" -> it.prefTag4Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 4)
                "tag5" -> it.prefTag5Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 5)
                "tag6" -> it.prefTag6Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 6)
            }
            }
        }

        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    private fun prefEntryConverter(prefValue: String): String {
        if(Setting.IN_KOREA) {
            return when (prefValue) {
                "silent" -> "무음"
                "vibrate" -> "진동"
                "ring" -> "알림음"
                "vibrate_and_ring" -> "진동과 알림음"
                "light_weight_babe" -> "Light weight babe"
                "ring1" -> "알림음1"
                "ring2" -> "알림음2"
                "ring3" -> "알림음3"
                "ring4" -> "알림음4"
                "ring5" -> "알림음5"
                "ring6" -> "알림음6"
                else -> "알림음7"
            }
        } else
            return prefValue
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
        registerPrefChangeListener()
    }

    override fun onStop() {
        super.onStop()
        if(isPlayerInit) mediaPlayer.release()
        preferences.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }

    inner class Permission(context: Context, value:Boolean) {

        private var overlayPermission: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {    //permission 허가 상태라면
                prefViewModel.setFloatingSettings(context, value)
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(requireContext(), getString(R.string.deny_overlay), Toast.LENGTH_LONG).show()
                switchFloating.isChecked = false
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

    private fun setView() {
        binding.apply {
            prefAlertValue.text = prefEntryConverter(prefViewModel.getAlertSettings(requireContext()))
            prefRingValue.text = prefEntryConverter(prefViewModel.getRingSettings(requireContext()))

            switchFloating.isChecked = prefViewModel.getFloatingSettings(requireContext())
            switchFloating.setOnCheckedChangeListener { _, isChecked ->
                Permission(requireContext(), isChecked).checkAlertWindowPermission()
            }

            prefTag1Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 1)
            prefTag2Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 2)
            prefTag3Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 3)
            prefTag4Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 4)
            prefTag5Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 5)
            prefTag6Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 6)

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
                R.id.suggestion_board -> {
                    val uri = Uri.parse("https://healthit.modoo.at/?link=2proi1a4")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                R.id.error_board -> {
                    val uri = Uri.parse("https://healthit.modoo.at/?link=bwbyxw2q")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                R.id.rate_app -> { openAppInPlayStore() }
                R.id.pref_alert -> { DialogUtil.showAlertDialog(this@SettingsFragment) }
                R.id.pref_ring -> { DialogUtil.showRingDialog(this@SettingsFragment) }
                R.id.pref_floating -> { switchFloating.toggle() }
                R.id.pref_template -> { DialogUtil.showTemplateDialog(this@SettingsFragment, true) }
                R.id.pref_tag1 -> { DialogUtil.showTagDialog(this@SettingsFragment, 1, layoutInflater) }
                R.id.pref_tag2 -> { DialogUtil.showTagDialog(this@SettingsFragment, 2, layoutInflater) }
                R.id.pref_tag3 -> { DialogUtil.showTagDialog(this@SettingsFragment, 3, layoutInflater) }
                R.id.pref_tag4 -> { DialogUtil.showTagDialog(this@SettingsFragment, 4, layoutInflater) }
                R.id.pref_tag5 -> { DialogUtil.showTagDialog(this@SettingsFragment, 5, layoutInflater) }
                R.id.pref_tag6 -> { DialogUtil.showTagDialog(this@SettingsFragment, 6, layoutInflater) }
            }

    }
}

