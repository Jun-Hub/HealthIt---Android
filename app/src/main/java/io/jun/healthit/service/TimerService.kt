package io.jun.healthit.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.lifecycle.MutableLiveData
import io.jun.healthit.R
import io.jun.healthit.util.NotiUtil

class TimerService : Service() {

    private lateinit var timer: CountDownTimer

    private var timerLengthSeconds: Long = 0
    private var secondsRemaining: Long = 0
    private lateinit var showTime: String

    private lateinit var alertSetting: String
    private lateinit var ringSetting: String
    private lateinit var vibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlayerInit = false

    override fun onCreate() {
        super.onCreate()
        vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        isRunning = true
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

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
                }
                "PAUSE" -> pauseTimer()
                "STOP" -> stopTimer()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isPlayerInit) mediaPlayer.release()
        isRunning = false
    }

    //어플이 종료될 때 발생. TimerService는 BindService라서 호출이 안됌
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        NotiUtil.removeNotification()
        stopSelf()
    }

    /** method for clients  */
    private fun playTimer(setTime: Int, isReplay: Boolean) {

        if(!isReplay) {
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
        progress.postValue((timerLengthSeconds - secondsRemaining).toInt())
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
