package io.jun.healthit.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import io.jun.healthit.R
import io.jun.healthit.adapter.RecordListAdapter
import io.jun.healthit.model.data.Memo
import io.jun.healthit.model.data.Record
import io.jun.healthit.service.TimerService
import io.jun.healthit.view.AddEditActivity
import io.jun.healthit.view.SetTemplateActivity
import io.jun.healthit.viewmodel.MemoViewModel
import io.jun.healthit.viewmodel.PrefViewModel

object DialogUtil {

    private val TAG = "DialogUtil"

        fun dateDialog(textDate:TextView, activity: Activity, layoutInflater: LayoutInflater) {
            val inflater = layoutInflater.inflate(R.layout.dialog_date, null as ViewGroup?)

            val datePicker = inflater.findViewById<DatePicker>(R.id.date_picker)

            AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar).setView(inflater)
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

        fun saveMemoDialog(
            forAdd: Boolean,
            activity: Activity,
            layoutInflater: LayoutInflater,
            memoViewModel: MemoViewModel,
            id: Int?,
            title: String,
            content: String,
            records: List<Record>,
            byteArrayList: ArrayList<ByteArray>,
            date: String,
            tag: Int,
            pin: Boolean,
            isConflictDate: Boolean
        ) {

            //저장할 이미지 총 용량 계산
            var totalSize = 0
            for (i in byteArrayList.indices) totalSize += byteArrayList[i].size

            val inflater = layoutInflater.inflate(R.layout.dialog_save, null as ViewGroup?)

            AlertDialog.Builder(
                    activity,
                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar
                )
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
                            activity.finish()
                        }
                    }
                }
                .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                }
                .setNeutralButton(activity.getString(R.string.dont_save)) { _, _ ->
                    activity.finish()
                }
                .show()
        }

        fun deleteDialog(
            owner: LifecycleOwner, activity: Activity, layoutInflater: LayoutInflater,
            memoViewModel: MemoViewModel, liveData: LiveData<Memo>, memo: Memo
        ) {
            val inflater = layoutInflater.inflate(R.layout.dialog_delete, null as ViewGroup?)

            AlertDialog.Builder(
                    activity,
                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar
                ).setView(inflater)
                .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                    //에러 방지하기 위해 LiveData가 활성중이라면 종료 후 디비 제거
                    if (liveData.hasObservers()) liveData.removeObservers(owner)

                    memoViewModel.delete(memo)
                    activity.finish()
                }
                .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                }
                .show()
        }

        fun showAlertDialog(fragment: Fragment) {

            val prefViewModel = ViewModelProvider(fragment).get(PrefViewModel::class.java)

            val alertEntries = fragment.resources.getStringArray(R.array.alert_entries)
            val alertValues = fragment.resources.getStringArray(R.array.alert_values)
            val curValue = when(fragment.context?.let { prefViewModel.getAlertSettings(it) }) {
                "silent" -> 0
                "vibrate" -> 1
                "ring" -> 2
                else -> 3
            }

            fragment.context?.let {
                AlertDialog.Builder(it, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                    .setTitle(fragment.resources.getString(R.string.alert_title))
                    .setSingleChoiceItems(alertEntries, curValue) { i, which ->
                        prefViewModel.setAlertSettings(it, alertValues[which])
                        i.dismiss()
                        if(TimerService.isRunning)
                            Toast.makeText(fragment.context, R.string.settings_toast, Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }

        fun showRingDialog(fragment:Fragment) {

            val prefViewModel = ViewModelProvider(fragment).get(PrefViewModel::class.java)

            val ringEntries = fragment.resources.getStringArray(R.array.ring_entries)
            val ringValues = fragment.resources.getStringArray(R.array.ring_values)
            val curValue = when(fragment.context?.let { prefViewModel.getRingSettings(it) }) {
                "light_weight_babe" -> 0
                "ring1" -> 1
                "ring2" -> 2
                "ring3" -> 3
                "ring4" -> 4
                "ring5" -> 5
                "ring6" -> 6
                else -> 7
            }

            fragment.context?.let {
                AlertDialog.Builder(it, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                    .setTitle(fragment.resources.getString(R.string.ring_title))
                    .setSingleChoiceItems(ringEntries, curValue) { i, which ->
                        prefViewModel.setRingSettings(it, ringValues[which])
                        i.dismiss()
                        if(TimerService.isRunning)
                            Toast.makeText(fragment.context, R.string.settings_toast, Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }

        fun showTagDialog(fragment: Fragment, index: Int, layoutInflater: LayoutInflater) {

            val prefViewModel = ViewModelProvider(fragment).get(PrefViewModel::class.java)

            val inflater = layoutInflater.inflate(R.layout.dialog_set_name, null as ViewGroup?)
            val titleText = inflater.findViewById<TextView>(R.id.textView_title)
            val editText = inflater.findViewById<EditText>(R.id.editText)
            titleText.text = String.format(fragment.getString(R.string.tag_name), index)

            fragment.context?.let {
                AlertDialog.Builder(it, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                    .setView(inflater)
                    .setPositiveButton(fragment.getString(R.string.ok)) { _, _ ->
                        prefViewModel.setTagSettings(it, index, editText.text.toString())
                        closeKeyboard(it)
                    }
                    .setNegativeButton(fragment.getString(R.string.cancel)) { _, _ ->
                        closeKeyboard(it)
                    }
                    .show()
            }
            fragment.context?.let { showKeyboard(it) }
        }

        fun showTemplateDialog(fragment: Fragment, settingsMode: Boolean) {

            val prefViewModel =  ViewModelProvider(fragment).get(PrefViewModel::class.java)

            val templates = arrayOf(
                fragment.context?.let { prefViewModel.getTemplateName(1, it) },
                fragment.context?.let { prefViewModel.getTemplateName(2, it) },
                fragment.context?.let { prefViewModel.getTemplateName(3, it) },
                fragment.context?.let { prefViewModel.getTemplateName(4, it) },
                fragment.context?.let { prefViewModel.getTemplateName(5, it) },
                fragment.context?.let { prefViewModel.getTemplateName(6, it) },
                fragment.context?.let { prefViewModel.getTemplateName(7, it) }
            )

            val title =
                if(settingsMode)
                    fragment.getString(R.string.create_my_routine)
                else
                    fragment.getString(R.string.create_log_with_routine)

            val intent = if(settingsMode)
                            Intent(fragment.context, SetTemplateActivity::class.java)
                        else
                            Intent(fragment.context, AddEditActivity::class.java).putExtra("newMemo", true)

            fragment.context?.let {
                AlertDialog.Builder(it, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                    .setTitle(title)
                    .setItems(templates) { _, which ->
                        when(which) {
                            0 -> { intent.putExtra("templateId",1)
                                fragment.startActivity(intent) }
                            1 -> { intent.putExtra("templateId",2)
                                fragment.startActivity(intent) }
                            2 -> { intent.putExtra("templateId",3)
                                fragment.startActivity(intent) }
                            3 -> { intent.putExtra("templateId",4)
                                fragment.startActivity(intent) }
                            4 -> { intent.putExtra("templateId",5)
                                fragment.startActivity(intent) }
                            5 -> { intent.putExtra("templateId",6)
                                fragment.startActivity(intent) }
                            6 -> { intent.putExtra("templateId",7)
                                fragment.startActivity(intent) }
                        }
                    }.show()
            }
        }

        fun saveTemplateDialog(layoutInflater: LayoutInflater, activity: AppCompatActivity, templateId: Int,
                               templateName: String, adapter: RecordListAdapter) {

            val prefViewModel = ViewModelProvider(activity).get(PrefViewModel::class.java)

            val inflater = layoutInflater.inflate(R.layout.dialog_save, null as ViewGroup?)

            AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                .setView(inflater)
                .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                    prefViewModel.setTemplateName(templateId, templateName, activity)
                    prefViewModel.setTemplate(templateId, adapter.records, activity)
                    activity.finish()
                }
                .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                }
                .setNeutralButton(activity.getString(R.string.dont_save)) { _, _ ->
                    activity.finish()
                }
                .show()
        }


        /** MemodetailActivity애서 현 기록 내 루틴에 추가하기 start **/

        fun addDirectlyTemplateDialog(layoutInflater: LayoutInflater, activity: AppCompatActivity,
                                    records: List<Record>) {

            val inflater = layoutInflater.inflate(R.layout.dialog_add_directly, null as ViewGroup?)

            AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                .setView(inflater)
                .setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                    templateNameDialog(activity, layoutInflater, records)
                }
                .setNegativeButton(activity.getString(R.string.cancel)) { _, _ ->
                }
                .show()
        }

        private fun templateNameDialog(activity: AppCompatActivity, layoutInflater: LayoutInflater, records: List<Record>) {

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

        private fun showDirectlyTemplateDialog(activity: AppCompatActivity, name: String, records: List<Record>) {

            val prefViewModel = ViewModelProvider(activity).get(PrefViewModel::class.java)

            val templates = arrayOf(
                prefViewModel.getTemplateName(1, activity),
                prefViewModel.getTemplateName(2, activity),
                prefViewModel.getTemplateName(3, activity),
                prefViewModel.getTemplateName(4, activity),
                prefViewModel.getTemplateName(5, activity),
                prefViewModel.getTemplateName(6, activity),
                prefViewModel.getTemplateName(7, activity)
            )

            AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                .setTitle(activity.getString(R.string.add_to_my_routine))
                .setItems(templates) { _, which ->
                    when (which) {   //루틴 이름과 루틴템플릿 저장
                        0 -> {
                            prefViewModel.setTemplateName(1, name, activity)
                            prefViewModel.setTemplate(1, records, activity)
                            Toast.makeText(activity,R.string.add_success ,Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            prefViewModel.setTemplateName(2, name, activity)
                            prefViewModel.setTemplate(2, records, activity)
                            Toast.makeText(activity,R.string.add_success ,Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            prefViewModel.setTemplateName(3, name, activity)
                            prefViewModel.setTemplate(3, records, activity)
                            Toast.makeText(activity,R.string.add_success ,Toast.LENGTH_SHORT).show()
                        }
                        3 -> {
                            prefViewModel.setTemplateName(4, name, activity)
                            prefViewModel.setTemplate(4, records, activity)
                            Toast.makeText(activity,R.string.add_success ,Toast.LENGTH_SHORT).show()
                        }
                        4 -> {
                            prefViewModel.setTemplateName(5, name, activity)
                            prefViewModel.setTemplate(5, records, activity)
                            Toast.makeText(activity,R.string.add_success ,Toast.LENGTH_SHORT).show()
                        }
                        5 -> {
                            prefViewModel.setTemplateName(6, name, activity)
                            prefViewModel.setTemplate(6, records, activity)
                            Toast.makeText(activity,R.string.add_success ,Toast.LENGTH_SHORT).show()
                        }
                        6 -> {
                            prefViewModel.setTemplateName(7, name, activity)
                            prefViewModel.setTemplate(7, records, activity)
                            Toast.makeText(activity,R.string.add_success ,Toast.LENGTH_SHORT).show()
                        }
                    }
                }.show()
        }
        /** MemodetailActivity애서 현 기록 내 루틴에 추가하기 end **/


        fun editRecordDialog(adapter: RecordListAdapter, context: Context,
                             layoutInflater: LayoutInflater, index:Int, current: Record) {

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

            name.setAdapter(ArrayAdapter(context,
                android.R.layout.simple_list_item_1, Setting.WORK_OUT_LIST))

            name.text =
                if(current.name == context.getString(R.string.name_guide))
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

            AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar).setView(inflater)
                .setPositiveButton(context.getString(R.string.action_save)) { _, _ ->
                    val weightFloat = if(weight.text.toString()=="" || weight.text.toString()==".") 0f else weight.text.toString().toFloat()
                    val setInt = if(set.text.toString()=="") 0 else set.text.toString().toInt()
                    val repsInt = if(reps.text.toString()=="") 0 else reps.text.toString().toInt()

                    adapter.editRecord(index, Record(name.text.toString(),
                        weightFloat,
                        setInt,
                        repsInt)
                    )
                }
                .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
                }
                .show()
        }

   fun showPurchaseProDialog(context: Context, subscribe: () -> Unit, restore: () -> Unit) {
       if(!isInternetConnected(context)) return

       DialogPlus.newDialog(context)
           .setOnClickListener { dialog, view ->
               when(view.id) {
                   R.id.button_not_now -> dialog.dismiss()
                   R.id.button_free_trial -> {
                       dialog.dismiss()
                       subscribe.invoke()
                   }
                   R.id.button_restore ->  restore.invoke()
               }
           }
           .setContentHolder(ViewHolder(R.layout.dialog_pro_body))
           .setHeader(R.layout.dialog_pro_header)
           .setPadding(50, 50, 50, 50)
           .setContentBackgroundResource(android.R.color.transparent)
           .create()
           .show()
   }

    fun showUseTipDialog(context: Context, layoutInflater: LayoutInflater, viewModel: PrefViewModel) {
        val inflater = layoutInflater.inflate(R.layout.dialog_tip_body, null as ViewGroup?)
        val imageViewGif: ImageView = inflater.findViewById(R.id.imageView_tip)
        Glide.with(context).load(R.raw.tip_edit_record).into(imageViewGif)

        DialogPlus.newDialog(context)
            .setOnClickListener { dialog, view ->
                if(view.id == R.id.button_ok) {
                    viewModel.setTipChecking(Setting.RECORD_EDIT_TIP_FLAG, true, context)
                    dialog.dismiss()
                }
            }
            .setContentHolder(ViewHolder(inflater))
            .setFooter(R.layout.dialog_tip_footer)
            .setGravity(Gravity.TOP)
            .setPadding(50,50,50,50)
            .setContentBackgroundResource(android.R.color.transparent)
            .create()
            .show()
    }

}