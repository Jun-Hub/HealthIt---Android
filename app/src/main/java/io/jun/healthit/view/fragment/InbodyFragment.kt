package io.jun.healthit.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.jun.healthit.R
import io.jun.healthit.model.data.Inbody
import io.jun.healthit.util.EtcUtil
import io.jun.healthit.viewmodel.InbodyViewModel
import kotlinx.android.synthetic.main.fragment_inbody.*

class InbodyFragment : Fragment() {

    private val TAG = "InbodyFragment"

    private lateinit var inbodyViewModel: InbodyViewModel

    private lateinit var inbodyList: List<Inbody>
    private lateinit var selectedDate: String

    private var isWeightValid = false
    private var isMuscleValid = false
    private var isPercentFatValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inbodyViewModel = ViewModelProvider(this).get(InbodyViewModel::class.java)
        initObserve()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inbody, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdView()
        setCalendarView()
        setClickListener()
        initTextWatcher()
    }

    //TODO 다른 프래그먼트 갓다오면 onCreate 될때마다 중복 생성되는 것 같다. observer 제거 해줘야 할듯?
    private fun initObserve() {
        ViewModelProvider(this).get(InbodyViewModel::class.java)
            .allInbodys.observe(requireActivity(), { inbodies ->
                inbodyList = inbodies

                inbodies.forEach {
                    Log.d(TAG, it.date)
                }
            })
    }

    private fun setCalendarView() {
        calendarView.selectedDate = CalendarDay.today()
        updateSelectedDate(CalendarDay.today())

        calendarView.setOnDateChangedListener { _, date, selected ->

            if(selected) {
                updateSelectedDate(date)
                updateInbodyInfo()
            }
        }
    }

    private fun updateInbodyInfo() {
        if(!::selectedDate.isInitialized) return
        val selectedInbody = inbodyList.find { it.date == selectedDate } ?: return

        editText_weight.text = floatToSpannable(selectedInbody.weight)
        editText_muscle.text = floatToSpannable(selectedInbody.skeletalMuscle)
        editText_percent_fat.text = floatToSpannable(selectedInbody.percentFat)
    }

    private fun updateSelectedDate(date: CalendarDay) {
        val day = if(date.day.toString().length==1) "0${date.day}" else "${date.day}"
        selectedDate = "${date.year}/${date.month+1}/${day}"
    }

    private fun setAdView() {
        MobileAds.initialize(requireContext())
        AdRequest.Builder().build().let {
            adView.loadAd(it)
        }
    }

    private fun setClickListener() {
        imageBtn_up_weight.setOnClickListener {
            editText_weight.text = EtcUtil.makePlusFloat(editText_weight, 1f)
        }
        imageBtn_down_weight.setOnClickListener {
            editText_weight.text = EtcUtil.makeMinusFloat(editText_weight, 1f)
        }
        imageBtn_up_muscle.setOnClickListener {
            editText_muscle.text = EtcUtil.makePlusFloat(editText_muscle, 1f)
        }
        imageBtn_down_muscle.setOnClickListener {
            editText_muscle.text = EtcUtil.makeMinusFloat(editText_muscle, 1f)
        }
        imageBtn_up_percent_fat.setOnClickListener {
            editText_percent_fat.text = EtcUtil.makePlusFloat(editText_percent_fat, 1f)
        }
        imageBtn_down_percent_fat.setOnClickListener {
            editText_percent_fat.text = EtcUtil.makeMinusFloat(editText_percent_fat, 1f)
        }

        btn_save.setOnClickListener {
            //TODO 저장됬다는 커스텀 토스트 띄우고, 데코레이터도 만들기
            inbodyViewModel.insert(Inbody(
                selectedDate,
                textToFloat(editText_weight),
                textToFloat(editText_muscle),
                textToFloat(editText_percent_fat)))
        }
    }

    private fun initTextWatcher() {
        setTextWatcher(editText_weight, 0)
        setTextWatcher(editText_muscle, 1)
        setTextWatcher(editText_percent_fat, 2)
    }

    private fun setTextWatcher(editText: EditText, identifier: Int) {
        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().startsWith(".")) {
                    when(identifier) {
                        0 -> isWeightValid = false
                        1 -> isMuscleValid = false
                        2 -> isPercentFatValid = false
                    }
                    inActivateButton(btn_save)
                } else {
                    when(identifier) {
                        0 -> isWeightValid = !s.isNullOrEmpty()
                        1 -> isMuscleValid = !s.isNullOrEmpty()
                        2 -> isPercentFatValid = !s.isNullOrEmpty()
                    }
                    checkValidState()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun activateButton(btn: Button) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_body_weight)
        btn.setTextColor(getColor(requireContext(), R.color.colorLightOrange))
        btn.isEnabled = true
    }

    private fun inActivateButton(btn: Button) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_body_inactive)
        btn.setTextColor(getColor(requireContext(), R.color.colorPopOut))
        btn.isEnabled = false
    }

    private fun checkValidState() {
        if(isWeightValid || isMuscleValid || isPercentFatValid)
            activateButton(btn_save)
        else
            inActivateButton(btn_save)
    }

    private fun textToFloat(editText: EditText) =
        if(editText.text.isNullOrEmpty()) null
        else editText.text.toString().toFloat()


    private fun floatToSpannable(float: Float?) =
        if(float==null) SpannableStringBuilder("")
        else SpannableStringBuilder(float.toString())

}