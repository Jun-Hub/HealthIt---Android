package io.jun.healthit.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.jun.healthit.R
import io.jun.healthit.service.TimerService
import io.jun.healthit.viewmodel.PrefViewModel
import io.jun.healthit.viewmodel.TimerViewModel
import uk.co.barbuzz.beerprogressview.BeerProgressView

class TimerFragment : Fragment() {

    private lateinit var prefViewModel : PrefViewModel

    private lateinit var btnStart: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnStop: ImageButton
    private lateinit var progressCountdown: BeerProgressView
    private lateinit var textViewCountdown: TextView
    private lateinit var numberPickerMin: NumberPicker
    private lateinit var numberPickerSec: NumberPicker
    private lateinit var numberPickerLayout: LinearLayout

    private var isRunning = false
    private var isReplay = false
    private var mBound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_timer, container, false)

        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)

        initView(root)
        initNumberPicker()

        MobileAds.initialize(this.context)
        val mAdView: AdView = root.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        //TimerService로부터 countDown 현황을 실시간으로 보여줄 LiveData 관찰
        val timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        timerViewModel.getLeftTime().observe(requireActivity(), Observer { leftTime ->
            textViewCountdown.text = leftTime
        })

        timerViewModel.getProgress().observe(requireActivity(), Observer { progress ->
            progressCountdown.beerProgress = progress
        })

        timerViewModel.getProgressMax().observe(requireActivity(), Observer { max ->
            progressCountdown.max = max
        })

        timerViewModel.getTimerState().observe(requireActivity(), Observer { state ->
            updateButtons(state)

            when(state) {
                TimerService.Companion.TimerState.Running -> {
                    isReplay = false
                    isRunning = true
                }
                TimerService.Companion.TimerState.Paused -> {
                    isReplay = true
                    isRunning = false
                }
                else -> {
                    isReplay = false
                    isRunning = false
                    progressCountdown.apply {
                        max = 0
                        beerProgress = 0
                    }
                }
            }
        })


        return root
    }

    override fun onStop() {
        super.onStop()
        mBound = false
        //마지막으로 설정했던 시간을 저장
        prefViewModel.setPreviousTimerSet(numberPickerMin.value, numberPickerSec.value, requireContext())
        if(isRunning && prefViewModel.getFloatingSettings(requireContext())) {
            Intent(requireContext(), TimerService::class.java).let {
                it.action = "FLOATING"
                startForegroundService(requireContext(), it)
            }
        }
    }

    private fun initView(root: View) {
        btnStart = root.findViewById(R.id.btn_start)
        btnPause = root.findViewById(R.id.btn_pause)
        btnStop = root.findViewById(R.id.btn_stop)
        progressCountdown = root.findViewById(R.id.beerProgressView)
        textViewCountdown = root.findViewById(R.id.textView_countdown)
        numberPickerMin = root.findViewById(R.id.numberPicker_min)
        numberPickerSec = root.findViewById(R.id.numberPicker_sec)
        numberPickerLayout = root.findViewById(R.id.numberPicker_layout)

        btnStart.setOnClickListener{

            val setTime = numberPickerMin.value * 60 + numberPickerSec.value

            Intent(this.context, TimerService::class.java).apply {
                action = "PLAY"
                putExtra("setTime", setTime)
                putExtra("forReplay", isReplay)
                putExtra("alertSetting", prefViewModel.getAlertSettings(requireContext()))
                putExtra("ringSetting", prefViewModel.getRingSettings(requireContext()))
            }.let {
                startForegroundService(requireContext(), it)
            }
        }

        btnPause.setOnClickListener {
            val serviceIntent = Intent(requireContext(), TimerService::class.java)
            serviceIntent.action = "PAUSE"
            startForegroundService(requireContext(), serviceIntent)
        }

        btnStop.setOnClickListener {
            val serviceIntent = Intent(requireContext(), TimerService::class.java)
            serviceIntent.action = "STOP"
            startForegroundService(requireContext(), serviceIntent)
        }
    }

    private fun initNumberPicker() {
        numberPickerMin.apply {
            minValue = 0
            maxValue = 15
            value = prefViewModel.getPreviousTimerSetMin(requireContext())
        }
        numberPickerSec.apply {
            minValue = 0
            maxValue = 59
            value = prefViewModel.getPreviousTimerSetSec(requireContext())
        }
    }

    private fun updateButtons(state: TimerService.Companion.TimerState) {
        when (state) {
            TimerService.Companion.TimerState.Running ->{
                btnStart.visibility = View.GONE
                btnPause.visibility = View.VISIBLE
                btnStop.visibility = View.VISIBLE
                numberPickerLayout.visibility = View.GONE
                textViewCountdown.visibility = View.VISIBLE
            }
            TimerService.Companion.TimerState.Stopped -> {
                btnStart.visibility = View.VISIBLE
                btnPause.visibility = View.GONE
                btnStop.visibility = View.GONE
                numberPickerLayout.visibility = View.VISIBLE
                textViewCountdown.visibility = View.GONE
            }
            TimerService.Companion.TimerState.Paused -> {
                btnStart.visibility = View.VISIBLE
                btnPause.visibility = View.GONE
                btnStop.visibility = View.VISIBLE
                numberPickerLayout.visibility = View.GONE
                textViewCountdown.visibility = View.VISIBLE
            }
        }
    }
}