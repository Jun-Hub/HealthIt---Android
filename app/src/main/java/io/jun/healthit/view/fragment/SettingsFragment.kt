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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.jun.healthit.R
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.util.Setting
import io.jun.healthit.viewmodel.PrefViewModel

class SettingsFragment : Fragment() {

    private lateinit var prefViewModel: PrefViewModel

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlayerInit = false

    private lateinit var preferences: SharedPreferences
    private lateinit var prefChangeListener: OnSharedPreferenceChangeListener

    private lateinit var prefAlertValue: TextView
    private lateinit var prefRingValue: TextView
    private lateinit var prefTag1Value: TextView
    private lateinit var prefTag2Value: TextView
    private lateinit var prefTag3Value: TextView
    private lateinit var prefTag4Value: TextView
    private lateinit var prefTag5Value: TextView
    private lateinit var prefTag6Value: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)

        MobileAds.initialize(context)
        val mAdView: AdView = root.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        prefAlertValue = root.findViewById(R.id.pref_alert_value)
        prefAlertValue.text = prefEntryConverter(prefViewModel.getAlertSettings(requireContext()))

        val prefAlert = root.findViewById<LinearLayout>(R.id.pref_alert)
        prefAlert.setOnClickListener {
            DialogUtil.showAlertDialog(this)
        }


        prefRingValue = root.findViewById(R.id.pref_ring_value)
        prefRingValue.text = prefEntryConverter(prefViewModel.getRingSettings(requireContext()))

        val prefRing = root.findViewById<LinearLayout>(R.id.pref_ring)
        prefRing.setOnClickListener {
            DialogUtil.showRingDialog(this)
        }


        val prefTemplate = root.findViewById<TextView>(R.id.pref_template)
        prefTemplate.setOnClickListener {
            DialogUtil.showTemplateDialog(this, true)
        }


        val prefTag1 = root.findViewById<LinearLayout>(R.id.pref_tag1)
        prefTag1.setOnClickListener {
            DialogUtil.showTagDialog(this, 1, layoutInflater)
        }
        prefTag1Value = root.findViewById(R.id.pref_tag1_value)
        prefTag1Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 1)

        val prefTag2 = root.findViewById<LinearLayout>(R.id.pref_tag2)
        prefTag2.setOnClickListener {
            DialogUtil.showTagDialog(this, 2,layoutInflater)
        }
        prefTag2Value = root.findViewById(R.id.pref_tag2_value)
        prefTag2Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 2)

        val prefTag3 = root.findViewById<LinearLayout>(R.id.pref_tag3)
        prefTag3.setOnClickListener {
            DialogUtil.showTagDialog(this, 3, layoutInflater)
        }
        prefTag3Value = root.findViewById(R.id.pref_tag3_value)
        prefTag3Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 3)

        val prefTag4 = root.findViewById<LinearLayout>(R.id.pref_tag4)
        prefTag4.setOnClickListener {
            DialogUtil.showTagDialog(this, 4, layoutInflater)
        }
        prefTag4Value = root.findViewById(R.id.pref_tag4_value)
        prefTag4Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 4)

        val prefTag5 = root.findViewById<LinearLayout>(R.id.pref_tag5)
        prefTag5.setOnClickListener {
            DialogUtil.showTagDialog(this, 5, layoutInflater)
        }
        prefTag5Value = root.findViewById(R.id.pref_tag5_value)
        prefTag5Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 5)

        val prefTag6 = root.findViewById<LinearLayout>(R.id.pref_tag6)
        prefTag6.setOnClickListener {
            DialogUtil.showTagDialog(this, 6, layoutInflater)
        }
        prefTag6Value = root.findViewById(R.id.pref_tag6_value)
        prefTag6Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 6)


        val sugBoard = root.findViewById<TextView>(R.id.suggestion_board)
        sugBoard.setOnClickListener {
            val uri = Uri.parse("https://healthit.modoo.at/?link=2proi1a4")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        val errorBoard = root.findViewById<TextView>(R.id.error_board)
        errorBoard.setOnClickListener {
            val uri = Uri.parse("https://healthit.modoo.at/?link=bwbyxw2q")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        val rateApp = root.findViewById<TextView>(R.id.rate_app)
        rateApp.setOnClickListener {
            openAppInPlayStore()
        }

        return root
    }

    private fun registerPrefChangeListener() {
        prefChangeListener = OnSharedPreferenceChangeListener {_, key ->
            when(key) {
                "alert" -> prefAlertValue.text = prefEntryConverter(prefViewModel.getAlertSettings(requireContext()))
                "ring" -> {
                    val ring = prefViewModel.getRingSettings(requireContext())
                    prefRingValue.text = prefEntryConverter(ring)

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
                "tag1" -> prefTag1Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 1)
                "tag2" -> prefTag2Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 2)
                "tag3" -> prefTag3Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 3)
                "tag4" -> prefTag4Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 4)
                "tag5" -> prefTag5Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 5)
                "tag6" -> prefTag6Value.text = prefViewModel.getOneOfTagSettings(requireContext(), 6)
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

    inner class Permission(private val context: Context) {

        private var overlayPermission: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {    //permission 허가 상태라면

            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(requireContext(), getString(R.string.deny_take_photo), Toast.LENGTH_LONG).show()
            }
        }
        fun checkAlertPermission() {
            TedPermission.with(context)
                .setPermissionListener(overlayPermission)
                .setRationaleMessage("오버레이")
                .setDeniedMessage("거부됨")
                .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check()
        }
    }
}

