package io.jun.healthit.util

import android.app.Activity
import android.content.Context
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import io.jun.healthit.FragmentFactory
import io.jun.healthit.FragmentNavigation
import io.jun.healthit.R
import io.jun.healthit.adapter.RecordListAdapter
import io.jun.healthit.model.data.Memo
import io.jun.healthit.model.data.Record
import io.jun.healthit.service.TimerService
import io.jun.healthit.viewmodel.MemoViewModel
import io.jun.healthit.viewmodel.PrefViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class DialogUtil(private val activity: Activity) : KoinComponent {

    private val TAG = "DialogUtil"

    private val memoViewModel: MemoViewModel by inject()
    private val prefViewModel: PrefViewModel by inject()

    fun dateDialog(textDate: TextView, layoutInflater: LayoutInflater) {
        val inflater = layoutInflater.inflate(R.layout.dialog_date, null as ViewGroup?)

        val datePicker = inflater.findViewById<DatePicker>(R.id.date_picker)

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->

                datePicker.also {
                    val month =
                        if (it.month + 1 < 10) "0${it.month + 1}" else it.month + 1
                    val day =
                        if (it.dayOfMonth < 10) "0${it.dayOfMonth}" else it.dayOfMonth

                    textDate.text = "${it.year}/$month/$day"
                }
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }

    fun saveMemoDialog(forAdd: Boolean, layoutInflater: LayoutInflater, id: Int?,
        title: String, content: String, records: List<Record>, byteArrayList: MutableList<ByteArray>,
        date: String, tag: Int, pin: Boolean, isConflictDate: Boolean, back: () -> Unit) {

        //저장할 이미지 총 용량 계산
        var totalSize = 0
        for (i in byteArrayList.indices) totalSize += byteArrayList[i].size

        val inflater = layoutInflater.inflate(R.layout.dialog_save, null as ViewGroup?)

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                when {
                    totalSize > Setting.TOTAL_SIZE_LIMIT ->
                        Toast.makeText(activity, activity.getString(R.string.notice_max_capacity), Toast.LENGTH_LONG).show()

                    isConflictDate -> Toast.makeText(activity, activity.getString(R.string.notice_conflict_date), Toast.LENGTH_LONG).show()

                    else -> {
                        if (forAdd)  //새로운 메모 추가
                            memoViewModel.insert(Memo(0, title, content, records, byteArrayList, date, tag, pin))
                        else    //기존 메모 수정
                            memoViewModel.update(Memo(id!!, title, content, records, byteArrayList, date, tag, pin))
                        back()
                    }
                }
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
            }
            .setNeutralButton(activity.getString(R.string.dont_save)) { _, _ ->
                back()
            }
            .show()
    }

    fun deleteDialog(
        layoutInflater: LayoutInflater,
        memo: Memo,
        back: () -> Unit
    ) {
        val inflater = layoutInflater.inflate(R.layout.dialog_delete, null as ViewGroup?)

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                memoViewModel.delete(memo)
                back()
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }

    fun showAlertDialog() {

        val alertEntries = activity.resources.getStringArray(R.array.alert_entries)
        val alertValues = activity.resources.getStringArray(R.array.alert_values)
        val curValue = when (prefViewModel.getAlertSettings()) {
            "silent" -> 0
            "vibrate" -> 1
            "ring" -> 2
            else -> 3
        }

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setTitle(activity.resources.getString(R.string.alert_title))
            .setSingleChoiceItems(alertEntries, curValue) { i, which ->
                prefViewModel.setAlertSettings(alertValues[which])
                i.dismiss()
                if (TimerService.isRunning)
                    Toast.makeText(activity, R.string.settings_toast, Toast.LENGTH_SHORT).show() }
            .show()

    }

    fun showRingDialog() {

        val ringEntries = activity.resources.getStringArray(R.array.ring_entries)
        val ringValues = activity.resources.getStringArray(R.array.ring_values)
        val curValue = when (prefViewModel.getRingSettings()) {
            "light_weight_babe" -> 0
            "ring1" -> 1
            "ring2" -> 2
            "ring3" -> 3
            "ring4" -> 4
            "ring5" -> 5
            "ring6" -> 6
            else -> 7
        }

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setTitle(activity.resources.getString(R.string.ring_title))
            .setSingleChoiceItems(ringEntries, curValue) { i, which ->
                prefViewModel.setRingSettings(ringValues[which])
                i.dismiss()
                if (TimerService.isRunning)
                    Toast.makeText(activity, R.string.settings_toast, Toast.LENGTH_SHORT).show() }
            .show()

    }

    fun showTagDialog(index: Int, layoutInflater: LayoutInflater) {

        val inflater = layoutInflater.inflate(R.layout.dialog_set_name, null as ViewGroup?)
        val titleText = inflater.findViewById<TextView>(R.id.textView_title)
        val editText = inflater.findViewById<EditText>(R.id.editText)
        titleText.text = String.format(activity.getString(R.string.tag_name), index)

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                prefViewModel.setTagSettings(index, editText.text.toString())
                closeKeyboard(activity)
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                closeKeyboard(activity)
            }
            .show()

        showKeyboard(activity)
    }

    fun showTemplateDialogForSet(navigation: FragmentNavigation) {

        val templates = arrayOf(
            prefViewModel.getTemplateName(1),
            prefViewModel.getTemplateName(2),
            prefViewModel.getTemplateName(3),
            prefViewModel.getTemplateName(4),
            prefViewModel.getTemplateName(5),
            prefViewModel.getTemplateName(6),
            prefViewModel.getTemplateName(7)
        )

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setTitle(activity.getString(R.string.create_my_routine))
            .setItems(templates) { _, which ->
                when (which) {
                    0 -> navigation.move(FragmentFactory.getSetTemplateFragment(1))
                    1 -> navigation.move(FragmentFactory.getSetTemplateFragment(2))
                    2 -> navigation.move(FragmentFactory.getSetTemplateFragment(3))
                    3 -> navigation.move(FragmentFactory.getSetTemplateFragment(4))
                    4 -> navigation.move(FragmentFactory.getSetTemplateFragment(5))
                    5 -> navigation.move(FragmentFactory.getSetTemplateFragment(6))
                    6 -> navigation.move(FragmentFactory.getSetTemplateFragment(7))
                }
            }.show()
    }

    fun showTemplateDialogForAdd(navigation: FragmentNavigation) {

        val templates = arrayOf(
            prefViewModel.getTemplateName(1),
            prefViewModel.getTemplateName(2),
            prefViewModel.getTemplateName(3),
            prefViewModel.getTemplateName(4),
            prefViewModel.getTemplateName(5),
            prefViewModel.getTemplateName(6),
            prefViewModel.getTemplateName(7)
        )

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setTitle(activity.getString(R.string.create_log_with_routine))
            .setItems(templates) { _, which ->
                when (which) {
                    0 -> navigation.move(FragmentFactory.getAddEditFragment(true, 1))
                    1 -> navigation.move(FragmentFactory.getAddEditFragment(true, 2))
                    2 -> navigation.move(FragmentFactory.getAddEditFragment(true, 3))
                    3 -> navigation.move(FragmentFactory.getAddEditFragment(true, 4))
                    4 -> navigation.move(FragmentFactory.getAddEditFragment(true, 5))
                    5 -> navigation.move(FragmentFactory.getAddEditFragment(true, 6))
                    6 -> navigation.move(FragmentFactory.getAddEditFragment(true, 7))
                }
            }.show()
    }

    fun saveTemplateDialog(
        layoutInflater: LayoutInflater, templateId: Int,
        templateName: String, adapter: RecordListAdapter, back: () -> Unit
    ) {
        val inflater = layoutInflater.inflate(R.layout.dialog_save, null as ViewGroup?)

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                prefViewModel.setTemplateName(templateId, templateName)
                prefViewModel.setTemplate(templateId, adapter.records)
                back()
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
            }
            .setNeutralButton(activity.getString(R.string.dont_save)) { _, _ ->
                back()
            }
            .show()
    }


    /** MemodetailActivity애서 현 기록 내 루틴에 추가하기 start **/

    fun addDirectlyTemplateDialog(
        layoutInflater: LayoutInflater, records: List<Record>) {

        val inflater = layoutInflater.inflate(R.layout.dialog_add_directly, null as ViewGroup?)

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                templateNameDialog(layoutInflater, records)
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }

    private fun templateNameDialog(layoutInflater: LayoutInflater, records: List<Record>) {

        val inflater = layoutInflater.inflate(R.layout.dialog_set_name, null as ViewGroup?)
        val titleText = inflater.findViewById<TextView>(R.id.textView_title)
        val editText = inflater.findViewById<EditText>(R.id.editText)
        titleText.text = activity.getString(R.string.routine_name_to_add)

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                showDirectlyTemplateDialog(activity, editText.text.toString(), records)
                closeKeyboard(activity)
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                closeKeyboard(activity)
            }
            .show()
        showKeyboard(activity)
    }

    private fun showDirectlyTemplateDialog(context: Context, name: String, records: List<Record>) {

        val templates = arrayOf(
            prefViewModel.getTemplateName(1),
            prefViewModel.getTemplateName(2),
            prefViewModel.getTemplateName(3),
            prefViewModel.getTemplateName(4),
            prefViewModel.getTemplateName(5),
            prefViewModel.getTemplateName(6),
            prefViewModel.getTemplateName(7)
        )

        AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setTitle(context.getString(R.string.add_to_my_routine))
            .setItems(templates) { _, which ->
                when (which) {   //루틴 이름과 루틴템플릿 저장
                    0 -> {
                        prefViewModel.setTemplateName(1, name)
                        prefViewModel.setTemplate(1, records)
                        Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        prefViewModel.setTemplateName(2, name)
                        prefViewModel.setTemplate(2, records)
                        Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        prefViewModel.setTemplateName(3, name)
                        prefViewModel.setTemplate(3, records)
                        Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        prefViewModel.setTemplateName(4, name)
                        prefViewModel.setTemplate(4, records)
                        Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }
                    4 -> {
                        prefViewModel.setTemplateName(5, name)
                        prefViewModel.setTemplate(5, records)
                        Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }
                    5 -> {
                        prefViewModel.setTemplateName(6, name)
                        prefViewModel.setTemplate(6, records)
                        Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }
                    6 -> {
                        prefViewModel.setTemplateName(7, name)
                        prefViewModel.setTemplate(7, records)
                        Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    /** MemodetailActivity애서 현 기록 내 루틴에 추가하기 end **/


    fun editRecordDialog(adapter: RecordListAdapter, layoutInflater: LayoutInflater, index: Int, current: Record) {

        val inflater = layoutInflater.inflate(R.layout.dialog_record, null as ViewGroup?)

        val name = inflater.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val weight = inflater.findViewById<EditText>(R.id.editText_weight)
        val set = inflater.findViewById<EditText>(R.id.editText_set)
        val reps = inflater.findViewById<EditText>(R.id.editText_reps)

        val weightUpBtn = inflater.findViewById<ImageButton>(R.id.imageBtn_up_weight)
        val weightDownBtn = inflater.findViewById<ImageButton>(R.id.imageBtn_down_weight)
        val setUpBtn = inflater.findViewById<ImageButton>(R.id.imageBtn_up_set)
        val setDownBtn = inflater.findViewById<ImageButton>(R.id.imageBtn_down_set)
        val repsUpBtn = inflater.findViewById<ImageButton>(R.id.imageBtn_up_reps)
        val repsDownBtn = inflater.findViewById<ImageButton>(R.id.imageBtn_down_reps)

        name.setAdapter(
            ArrayAdapter(activity, android.R.layout.simple_list_item_1, Setting.WORK_OUT_LIST)
        )

        name.text =
            if (current.name == activity.getString(R.string.name_guide))
                SpannableStringBuilder("")
            else
                SpannableStringBuilder(current.name)

        weight.text = SpannableStringBuilder(current.weight.toString())
        set.text = SpannableStringBuilder(current.set.toString())
        reps.text = SpannableStringBuilder(current.reps.toString())

        weightUpBtn.setOnClickListener {
            weight.text = makePlusFloat(weight, 5f)
        }
        weightDownBtn.setOnClickListener {
            weight.text = makeMinusFloat(weight, 5f)
        }

        setUpBtn.setOnClickListener {
            set.text = makePlus(set, 1)
        }
        setDownBtn.setOnClickListener {
            set.text = makeMinus(set, 1)
        }

        repsUpBtn.setOnClickListener {
            reps.text = makePlus(reps, 1)
        }
        repsDownBtn.setOnClickListener {
            reps.text = makeMinus(reps, 1)
        }

        AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            .setView(inflater)
            .setPositiveButton(activity.getString(R.string.action_save)) { _, _ ->
                val weightFloat =
                    if (weight.text.toString() == "" || weight.text.toString() == ".") 0f else weight.text.toString()
                        .toFloat()
                val setInt = if (set.text.toString() == "") 0 else set.text.toString().toInt()
                val repsInt = if (reps.text.toString() == "") 0 else reps.text.toString().toInt()

                adapter.editRecord(
                    index, Record(
                        name.text.toString(),
                        weightFloat,
                        setInt,
                        repsInt
                    )
                )
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }

    fun showPurchaseProDialog(subscribe: () -> Unit, restore: () -> Unit) {
        if (!isInternetConnected(activity)) return

        DialogPlus.newDialog(activity)
            .setOnClickListener { dialog, view ->
                when (view.id) {
                    R.id.button_not_now -> dialog.dismiss()
                    R.id.button_free_trial -> {
                        dialog.dismiss()
                        subscribe.invoke()
                    }
                    R.id.button_restore -> restore.invoke()
                }
            }
            .setContentHolder(ViewHolder(R.layout.dialog_pro_body))
            .setHeader(R.layout.dialog_pro_header)
            .setPadding(50, 50, 50, 50)
            .setContentBackgroundResource(android.R.color.transparent)
            .create()
            .show()
    }

    fun showUseTipDialog(layoutInflater: LayoutInflater) {
        val inflater = layoutInflater.inflate(R.layout.dialog_tip_body, null as ViewGroup?)
        val imageViewGif: ImageView = inflater.findViewById(R.id.imageView_tip)
        Glide.with(activity).load(R.raw.tip_edit_record).into(imageViewGif)

        DialogPlus.newDialog(activity)
            .setOnClickListener { dialog, view ->
                if (view.id == R.id.button_ok) {
                    prefViewModel.setTipChecking(Setting.RECORD_EDIT_TIP_FLAG, true)
                    dialog.dismiss()
                }
            }
            .setContentHolder(ViewHolder(inflater))
            .setFooter(R.layout.dialog_tip_footer)
            .setGravity(Gravity.TOP)
            .setPadding(50, 50, 50, 50)
            .setContentBackgroundResource(android.R.color.transparent)
            .create()
            .show()
    }

}