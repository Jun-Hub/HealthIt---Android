package io.jun.healthit.model.repository

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.jun.healthit.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.jun.healthit.model.data.Record
import io.jun.healthit.model.data.Tag
import io.jun.healthit.util.Setting
import java.lang.reflect.Type

class PrefRepository {

    private val previousTimerSetMinId = "timer.previous_timer_set_min"
    private val previousTimerSetSecId = "timer.previous_timer_set_sec"

    fun getPreviousTimerSetMin(context: Context): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(previousTimerSetMinId, 2)
    }

    fun getPreviousTimerSetSec(context: Context): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(previousTimerSetSecId, 30)
    }

    fun setPreviousTimerSet(min: Int, sec: Int, context: Context) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(previousTimerSetMinId, min)
        editor.putInt(previousTimerSetSecId, sec)
        editor.apply()
    }

    fun getAlertSettings(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(
            "alert",
            context.getString(R.string.pref_vibrate_and_ring)
        )!!
    }

    fun setAlertSettings(context: Context, value: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString("alert", value)
        editor.apply()
    }

    fun getRingSettings(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(
            "ring",
            context.getString(R.string.pref_light_weight_babe)
        )!!
    }

    fun setRingSettings(context: Context, value: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString("ring", value)
        editor.apply()
    }

    fun getFloatingSettings(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("floatingTimer", false)
    }
    fun setFloatingSettings(context: Context, value:Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putBoolean("floatingTimer", value)
        editor.apply()
    }

    fun getTagSettings(context: Context, forSort: Boolean): ArrayList<Tag> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val tag1 = preferences.getString("tag1", context.getString(R.string.red))
        val tag2 = preferences.getString("tag2", context.getString(R.string.orange))
        val tag3 = preferences.getString("tag3", context.getString(R.string.yellow))
        val tag4 = preferences.getString("tag4", context.getString(R.string.green))
        val tag5 = preferences.getString("tag5", context.getString(R.string.blue))
        val tag6 = preferences.getString("tag6", context.getString(R.string.purple))

        val tagList = ArrayList<Tag>()
        if (forSort) tagList.add(
            Tag(
                R.drawable.transparent,
                context.getString(R.string.view_all)
            )
        )
        tagList.add(Tag(R.drawable.transparent, context.getString(R.string.no_tag)))
        tag1?.let { Tag(R.drawable.ic_circle_red, it) }?.let { tagList.add(it) }
        tag2?.let { Tag(R.drawable.ic_circle_orange, it) }?.let { tagList.add(it) }
        tag3?.let { Tag(R.drawable.ic_circle_yellow, it) }?.let { tagList.add(it) }
        tag4?.let { Tag(R.drawable.ic_circle_green, it) }?.let { tagList.add(it) }
        tag5?.let { Tag(R.drawable.ic_circle_blue, it) }?.let { tagList.add(it) }
        tag6?.let { Tag(R.drawable.ic_circle_purple, it) }?.let { tagList.add(it) }

        return tagList
    }

    fun getOneOfTagSettings(context: Context, index: Int): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        return when (index) {
            1 -> preferences.getString("tag1", context.getString(R.string.red))
            2 -> preferences.getString("tag2", context.getString(R.string.orange))
            3 -> preferences.getString("tag3", context.getString(R.string.yellow))
            4 -> preferences.getString("tag4", context.getString(R.string.green))
            5 -> preferences.getString("tag5", context.getString(R.string.blue))
            else -> preferences.getString("tag6", context.getString(R.string.purple))
        }
    }

    fun setTagSettings(context: Context, index: Int, value: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString("tag$index", value)
        editor.apply()
    }

    private val template = "template"
    private val templateName = "template_name"

    fun getTemplate(key: Int, context: Context): List<Record> {
        //defValue가 json 형태가 아니면 error를 일으키므로 defValue를 만들어줌
        val record = Record(context.getString(R.string.touch_to_edit), 40f, 5, 10)
        val defJsonValue = Gson().toJson(listOf(record))

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val type: Type = object : TypeToken<List<Record>>() {}.type
        return Gson().fromJson(
            preferences.getString(
                template + key,
                defJsonValue
            ), type
        )
    }

    fun setTemplate(key: Int, records: List<Record>, context: Context) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        val jsonRecord = Gson().toJson(records)
        editor.putString(template + key, jsonRecord)
        editor.apply()
    }

    fun getTemplateName(key: Int, context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(
            templateName + key,
            String.format(context.getString(R.string.basic_routine_name), key)
        )
    }

    fun setTemplateName(key: Int, name: String, context: Context) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(templateName + key, name)
        editor.apply()
    }

    private val memoViewModeId = "view.mode_id"

    fun getViewMode(context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(memoViewModeId, "LEFT")
    }

    fun setViewMode(mode: String, context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(memoViewModeId, mode)
        }
    }

    private val inbodySpinnerId = "inbody.spinner_id"

    fun getInbodySpinner(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(inbodySpinnerId, 0)

    fun setInbodySpinner(position:Int, context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putInt(inbodySpinnerId, position)
        }
    }

    private val tipCheckingId = "tip.checking_id"

    fun getTipChecking(key: Int, context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(tipCheckingId + key, false)

    fun setTipChecking(key: Int, isChecked: Boolean, context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(tipCheckingId + key, isChecked)
        }
    }
}