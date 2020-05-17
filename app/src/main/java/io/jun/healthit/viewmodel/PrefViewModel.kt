package io.jun.healthit.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import io.jun.healthit.model.PrefRepository
import io.jun.healthit.model.Record

class PrefViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PrefRepository = PrefRepository()

    fun getPreviousTimerSetMin(context: Context) = repository.getPreviousTimerSetMin(context)

    fun getPreviousTimerSetSec(context: Context) = repository.getPreviousTimerSetSec(context)

    fun setPreviousTimerSet(min:Int, sec: Int, context: Context) = repository.setPreviousTimerSet(min, sec, context)


    fun getAlertSettings(context: Context) = repository.getAlertSettings(context)

    fun setAlertSettings(context: Context, value :String) = repository.setAlertSettings(context, value)


    fun getRingSettings(context: Context) = repository.getRingSettings(context)

    fun setRingSettings(context: Context, value: String) = repository.setRingSettings(context, value)


    fun getFloatingSettings(context: Context) = repository.getFloatingSettings(context)

    fun setFloatingSettings(context: Context, value:Boolean) = repository.setFloatingSettings(context,value)


    fun getTagSettings(context: Context, forSort: Boolean) = repository.getTagSettings(context, forSort)

    fun getOneOfTagSettings(context: Context, index: Int) = repository.getOneOfTagSettings(context, index)

    fun setTagSettings(context: Context, index: Int, value:String) = repository.setTagSettings(context, index, value)


    fun getTemplate(key: Int, context: Context) = repository.getTemplate(key, context)

    fun setTemplate(key: Int, records: List<Record>, context: Context) = repository.setTemplate(key, records, context)

    fun getTemplateName(key: Int, context: Context) = repository.getTemplateName(key, context)

    fun setTemplateName(key: Int, name: String, context: Context) = repository.setTemplateName(key, name, context)
}