package io.jun.healthit.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import io.jun.healthit.model.repository.PrefRepository
import io.jun.healthit.model.data.Record

class PrefViewModel(application: Application, private val context: Context) : AndroidViewModel(application) {

    private val repository: PrefRepository = PrefRepository()

    fun getPreviousTimerSetMin() = repository.getPreviousTimerSetMin(context)

    fun getPreviousTimerSetSec() = repository.getPreviousTimerSetSec(context)

    fun setPreviousTimerSet(min:Int, sec: Int) = repository.setPreviousTimerSet(min, sec, context)


    fun getAlertSettings() = repository.getAlertSettings(context)

    fun setAlertSettings(value :String) = repository.setAlertSettings(context, value)


    fun getRingSettings() = repository.getRingSettings(context)

    fun setRingSettings(value: String) = repository.setRingSettings(context, value)


    fun getFloatingSettings() = repository.getFloatingSettings(context)

    fun setFloatingSettings(value:Boolean) = repository.setFloatingSettings(context,value)


    fun getTagSettings(forSort: Boolean) = repository.getTagSettings(context, forSort)

    fun getOneOfTagSettings(index: Int) = repository.getOneOfTagSettings(context, index)

    fun setTagSettings(index: Int, value:String) = repository.setTagSettings(context, index, value)


    fun getTemplate(key: Int) = repository.getTemplate(key, context)

    fun setTemplate(key: Int, records: List<Record>) = repository.setTemplate(key, records, context)

    fun getTemplateName(key: Int) = repository.getTemplateName(key, context)

    fun setTemplateName(key: Int, name: String) = repository.setTemplateName(key, name, context)


    fun getViewMode() = repository.getViewMode(context)

    fun setViewMode(mode:String) = repository.setViewMode(mode, context)

    fun getInbodySpinner() = repository.getInbodySpinner(context)

    fun setInbodySpinner(position: Int) = repository.setInbodySpinner(position, context)


    fun getTipChecking(key: Int) = repository.getTipChecking(key, context)
    fun setTipChecking(key: Int, isChecked: Boolean) = repository.setTipChecking(key, isChecked, context)
}