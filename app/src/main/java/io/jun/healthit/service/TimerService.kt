package io.jun.healthit.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.*
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import io.jun.healthit.R
import io.jun.healthit.util.NotiUtil
import io.jun.healthit.view.MainActivity
import uk.co.barbuzz.beerprogressview.BeerProgressView
import kotlin.properties.Delegates

class TimerService : Service() {

    private lateinit var timer: CountDownTimer

    private var setTime by Delegates.notNull<Int>()
    private var timerLengthSeconds: Long = 0
    private var secondsRemaining: Long = 0
    private lateinit var showTime: String

    private lateinit var alertSetting: String
    private lateinit var ringSetting: String
    private lateinit var vibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlayerInit = false

    //floating view
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var floatingProgress: BeerProgressView
    private lateinit var floatingTextCountDown: TextView
    private lateinit var floatingPlayBtn: ImageButton

    override fun onCreate() {
        super.onCreate()
        vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        isRunning = true
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null) {
            when (intent.action) {
                "PLAY" -> {
                    val tempAlert = intent.getStringExtra("alertSetting")
                    val tempRing = intent.getStringExtra("ringSetting")

                    if(tempAlert != null && tempRing != null) {
                        alertSetting = tempAlert
                        ringSetting = tempRing
                    }
                    playTimer(
                        intent.getIntExtra("setTime", 0),
                        intent.getBooleanExtra("forReplay", false)
                    )
                    if(::floatingView.isInitialized && floatingView.visibility == View.VISIBLE) {
                        floatingProgress.max = intent.getIntExtra("setTime", 0)
                        floatingTextCountDown.visibility = View.VISIBLE
                        floatingPlayBtn.visibility = View.GONE
                    }
                }
                "PAUSE" -> pauseTimer()
                "STOP" -> stopTimer()
                "FLOATING" -> if(!::floatingView.isInitialized) setFloatingView()
                                else {
                                    floatingView.visibility = View.VISIBLE
                                    floatingTextCountDown.visibility = View.VISIBLE
                                    floatingPlayBtn.visibility = View.GONE
                                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isPlayerInit) mediaPlayer.release()
        isRunning = false
        if(::windowManager.isInitialized && ::floatingView.isInitialized)
            windowManager.removeView(floatingView)
    }

    //어플이 종료될 때 발생.
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        if(::timer.isInitialized) {
            timer.cancel()
            timerState.postValue(TimerState.Stopped)
        }
        NotiUtil.removeNotification()
        stopSelf()
    }

    /** method for clients  */
    private fun playTimer(setTime: Int, isReplay: Boolean) {

        if(!isReplay) {
            this.setTime = setTime
            secondsRemaining = setTime.toLong()
            timerLengthSeconds = setTime.toLong()
            progressMax.postValue(setTime)
            startForeground(1, NotiUtil.createNotification(this, setTime))
        }

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                timerState.postValue(TimerState.Stopped)
                finishEffect()
                //초기 세팅됬었던 카운트다운 시간값을 노티에 재세팅
                val minutesUntilFinished = setTime / 60
                val secondsInMinuteUntilFinished = (setTime - minutesUntilFinished * 60)
                val secondsStr = secondsInMinuteUntilFinished.toString()
                val showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
                NotiUtil.updateNotification(this@TimerService, showTime, isPlaying = false, isPausing =  false)
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()

        timerState.postValue(TimerState.Running)
    }

    private fun pauseTimer() {
        if(::timer.isInitialized) {
            timer.cancel()
            timerState.postValue(TimerState.Paused)
            NotiUtil.updateNotification(this, showTime, isPlaying = false, isPausing = true)
        }
    }

    private fun stopTimer() {
        if(::timer.isInitialized) {
            timer.cancel()
            timerState.postValue(TimerState.Stopped)
            NotiUtil.removeNotification()
            stopSelf()
        }
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = (secondsRemaining - minutesUntilFinished * 60)
        val secondsStr = secondsInMinuteUntilFinished.toString()
        showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"

        NotiUtil.updateNotification(this, showTime, isPlaying = true, isPausing =  false)
        leftTime.value = showTime
        if(::floatingTextCountDown.isInitialized)
            floatingTextCountDown.text = showTime

        (timerLengthSeconds - secondsRemaining).toInt().also {
            progress.postValue(it)
            if(::floatingProgress.isInitialized)
                floatingProgress.beerProgress = it
        }
    }

    private fun finishEffect() {

        if(::alertSetting.isInitialized) {
            //타이머 알림 설정값 가져오기
            when (alertSetting) {
                "vibrate" -> vibrate()
                "ring" -> playRing()
                "vibrate_and_ring" -> {
                    playRing()
                    vibrate()
                }
            }
        }
        if(::floatingPlayBtn.isInitialized) {
            floatingPlayBtn.visibility = View.VISIBLE
            floatingTextCountDown.visibility = View.GONE
        }
    }

    private fun playRing() {
        mediaPlayer =  when(ringSetting) {
            "light_weight_babe" -> MediaPlayer.create(this, R.raw.light_weight_babe)
            "ring1" -> MediaPlayer.create(this, R.raw.ring1)
            "ring2" -> MediaPlayer.create(this, R.raw.ring2)
            "ring3" -> MediaPlayer.create(this, R.raw.ring3)
            "ring4" -> MediaPlayer.create(this, R.raw.ring4)
            "ring5" -> MediaPlayer.create(this, R.raw.ring5)
            "ring6" -> MediaPlayer.create(this, R.raw.ring6)
            else -> MediaPlayer.create(this, R.raw.ring7)
        }
        mediaPlayer.start()
        isPlayerInit = true
    }

    private fun vibrate() {
        if(Build.VERSION.SDK_INT > 25) {
            // 배열 패턴만큼 반복 : (0초 대기 -> 0.25초 진동 -> 0.1초 대기 -> 0.25초 진동) / Repeat -1 : 반복 x
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 250, 100, 250, 100, 250), -1))
        } else {
            vibrator.vibrate(longArrayOf(0, 250, 100, 250, 100, 250), -1)
        }
    }

    private fun setFloatingView() {
        //getting the widget layout from xml using layout inflater
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget,null)

        //setting the layout parameters
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if(Build.VERSION.SDK_INT > 25) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        //getting windows services and adding the floating view to it
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        floatingProgress = floatingView.findViewById(R.id.beerProgressView)
        floatingProgress.max = setTime

        floatingTextCountDown = floatingView.findViewById(R.id.textView_countdown)

        floatingView.findViewById<ImageButton>(R.id.btn_extension).setOnClickListener {
            floatingView.visibility = View.GONE
            Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
            }
        }

        floatingView.findViewById<ImageButton>(R.id.btn_close).setOnClickListener {
            floatingView.visibility = View.GONE
        }

        floatingPlayBtn = floatingView.findViewById(R.id.btn_play)
        floatingPlayBtn.setOnClickListener {
            playTimer(setTime, false)
            floatingPlayBtn.visibility = View.GONE
            floatingTextCountDown.visibility = View.VISIBLE
        }

        //adding an touch listener to make drag movement of the floating widget
        floatingView.findViewById<View>(R.id.parent_view)
            .setOnTouchListener(object : View.OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f

                override fun onTouch(
                    v: View,
                    event: MotionEvent
                ): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            v.performClick()
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager.updateViewLayout(floatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
    }

    companion object {
        enum class TimerState {
            Stopped, Paused, Running
        }

        var isRunning = false

        //TimerFragment에서 observe할 LiveDatas
        var timerState: MutableLiveData<TimerState> = MutableLiveData(TimerState.Stopped)
        var leftTime: MutableLiveData<String> = MutableLiveData("")
        var progress: MutableLiveData<Int> = MutableLiveData(0)
        var progressMax: MutableLiveData<Int> = MutableLiveData(0)
    }
}
