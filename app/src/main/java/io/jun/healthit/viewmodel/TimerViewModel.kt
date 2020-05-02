package io.jun.healthit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.jun.healthit.service.TimerService

class TimerViewModel(application: Application): AndroidViewModel(application) {

    fun getLeftTime() = TimerService.leftTime

    fun getProgress() = TimerService.progress

    fun getProgressMax() = TimerService.progressMax

    fun getTimerState() = TimerService.timerState
}