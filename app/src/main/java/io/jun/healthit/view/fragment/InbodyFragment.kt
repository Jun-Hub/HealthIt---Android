package io.jun.healthit.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.google.android.material.snackbar.Snackbar
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.jun.healthit.R
import io.jun.healthit.adapter.InbodySpinnerAdapter
import io.jun.healthit.databinding.FragmentInbodyBinding
import io.jun.healthit.decorator.TextDecorator
import io.jun.healthit.decorator.TodayDecorator
import io.jun.healthit.model.data.Inbody
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.util.makeMinusFloat
import io.jun.healthit.util.makePlusFloat
import io.jun.healthit.view.MainActivity
import io.jun.healthit.viewmodel.InbodyViewModel
import io.jun.healthit.viewmodel.PrefViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class InbodyFragment : BaseFragment(), View.OnClickListener {

    private val TAG = "InbodyFragment"

    private val prefViewModel: PrefViewModel by sharedViewModel()
    private val inbodyViewModel: InbodyViewModel by sharedViewModel()
    private val dialogUtil: DialogUtil by inject{ parametersOf(activity) }

    private var viewBinding: FragmentInbodyBinding? = null
    private val binding get() = viewBinding!!

    private lateinit var inbodyList: List<Inbody>
    private lateinit var selectedDate: String

    private var isWeightValid = false
    private var isMuscleValid = false
    private var isPercentFatValid = false

    private val textDecoratorList = mutableListOf<TextDecorator>()

    private lateinit var inbodySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewBinding = FragmentInbodyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBar(binding.appbar.toolbar)
        setClickListener()
        setCalendarView()
        initTextWatcher()
        initObserve()
    }

    override fun checkProVersion(isProVersion: Boolean) {
        super.checkProVersion(isProVersion)
        Log.d(TAG, "checkProVersion")
        if(isProVersion) {
           binding.apply {
               textViewNoticePreview.visibility = View.GONE
               textViewFreeTrial.visibility = View.GONE
               adView.visibility = View.GONE
           }
            return
        }

        binding.run {
            textViewNoticePreview.visibility = View.VISIBLE
            textViewFreeTrial.visibility = View.VISIBLE
            loadBannerAd(binding.adView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    private fun initObserve() {
        inbodyViewModel.allInbodys.observe(viewLifecycleOwner, { inbodies ->
            inbodyList = inbodies

            initDecorator(inbodies, inbodySpinner.selectedItemPosition)
            updateInbodyText()
        })
    }

    private fun setCalendarView() {
        binding.apply {
            calendarView.selectedDate = CalendarDay.today()
            updateSelectedDate(CalendarDay.today())

            calendarView.setOnDateChangedListener { _, date, selected ->

                if (selected) {
                    updateSelectedDate(date)
                    updateInbodyText()
                }
            }
        }
    }

    private fun updateSelectedDate(date: CalendarDay) {
        val day = if(date.day.toString().length==1) "0${date.day}" else "${date.day}"
        selectedDate = "${date.year}/${date.month+1}/${day}"
    }

    private fun updateInbodyText() {
        if(!::selectedDate.isInitialized) return

        inbodyList.find { it.date == selectedDate }.let {
            binding.apply {
                editTextWeight.text = floatToSpannable(it?.weight)
                editTextMuscle.text = floatToSpannable(it?.skeletalMuscle)
                editTextPercentFat.text = floatToSpannable(it?.percentFat)
            }
        }
    }

    private fun initDecorator(inbodyList: List<Inbody>, flag: Int) {
        textDecoratorList.clear()
        context?.let { ctx ->
            inbodyList.forEach {
                textDecoratorList.add(TextDecorator(ctx, it, flag))
            }
        }

        decorateCalendar()
    }

    private fun decorateCalendar() {
        binding.calendarView.apply {
            removeDecorators()
            invalidateDecorators()

            addDecorator(TodayDecorator())
            addDecorators(textDecoratorList)
        }
    }


    private fun initTextWatcher() {
        binding.apply {
            setTextWatcher(editTextWeight, 0)
            setTextWatcher(editTextMuscle, 1)
            setTextWatcher(editTextPercentFat, 2)
        }
    }

    private fun setTextWatcher(editText: EditText, flag: Int) {
        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().startsWith(".")) {
                    when(flag) {
                        0 -> isWeightValid = false
                        1 -> isMuscleValid = false
                        2 -> isPercentFatValid = false
                    }
                    context?.let {
                        inActivateButton(it, binding.btnSave)
                    }
                } else {
                    when(flag) {
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

    private fun activateButton(context: Context, btn: Button) {
        btn.background = ContextCompat.getDrawable(context, R.drawable.button_body_weight)
        btn.setTextColor(getColor(context, R.color.colorLightOrange))
        btn.isEnabled = true
    }

    private fun inActivateButton(context: Context, btn: Button) {
        btn.background = ContextCompat.getDrawable(context, R.drawable.button_body_inactive)
        btn.setTextColor(getColor(context, R.color.colorPopOut))
        btn.isEnabled = false
    }

    private fun checkValidState() {
        context?.let {
            if (isWeightValid || isMuscleValid || isPercentFatValid)
                activateButton(it, binding.btnSave)
            else
                inActivateButton(it, binding.btnSave)
        }
    }

    private fun textToFloat(editText: EditText) =
        if(editText.text.isNullOrEmpty()) null
        else editText.text.toString().toFloat()


    private fun floatToSpannable(float: Float?) =
        if(float==null) SpannableStringBuilder("")
        else SpannableStringBuilder(float.toString())

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_inbody, menu)

        initMemoSortSpinner(menu)
    }

    private fun initMemoSortSpinner(menu: Menu) {
        //메모 정렬 스피너 셋팅
        val sort = menu.findItem(R.id.action_sort)
        sort.setActionView(R.layout.layout_spinner_inbody)
        inbodySpinner = sort.actionView.findViewById(R.id.tag_spinner_inbody)

        val spinnerList = listOf(
            getString(R.string.text_body_weight),
            getString(R.string.text_skeletal_muscle),
            getString(R.string.text_fat_percentage))

        context?.let {
            inbodySpinner.adapter = InbodySpinnerAdapter(it, spinnerList)
            inbodySpinner.setSelection(prefViewModel.getInbodySpinner())

            inbodySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(::inbodyList.isInitialized) {
                        initDecorator(inbodyList, position)
                    }
                    prefViewModel.setInbodySpinner(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setClickListener() {
        binding.apply {
            textViewFreeTrial.setOnClickListener(this@InbodyFragment)
            imageBtnUpWeight.setOnClickListener(this@InbodyFragment)
            imageBtnDownWeight.setOnClickListener(this@InbodyFragment)
            imageBtnUpMuscle.setOnClickListener(this@InbodyFragment)
            imageBtnDownMuscle.setOnClickListener(this@InbodyFragment)
            imageBtnUpPercentFat.setOnClickListener(this@InbodyFragment)
            imageBtnDownPercentFat.setOnClickListener(this@InbodyFragment)
            btnSave.setOnClickListener(this@InbodyFragment)
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v?.id) {
                R.id.textView_free_trial -> {
                    context?.let {
                        dialogUtil.showPurchaseProDialog(
                            { (activity as MainActivity).billingManager.subscribe() },
                            { (activity as MainActivity).billingManager.onPurchaseHistoryRestored() })
                    }
                }
                R.id.imageBtn_up_weight -> {
                    editTextWeight.text = makePlusFloat(editTextWeight, 1f)
                }
                R.id.imageBtn_down_weight -> {
                    editTextWeight.text = makeMinusFloat(editTextWeight, 1f)
                }
                R.id.imageBtn_up_muscle -> {
                    editTextMuscle.text = makePlusFloat(editTextMuscle, 1f)
                }
                R.id.imageBtn_down_muscle -> {
                    editTextMuscle.text = makeMinusFloat(editTextMuscle, 1f)
                }
                R.id.imageBtn_up_percent_fat -> {
                    editTextPercentFat.text = makePlusFloat(editTextPercentFat, 1f)
                }
                R.id.imageBtn_down_percent_fat -> {
                    editTextPercentFat.text = makeMinusFloat(editTextPercentFat, 1f)
                }

                R.id.btn_save -> {
                    if(!(activity as MainActivity).isProVersion) {
                        context?.let {
                            dialogUtil.showPurchaseProDialog(
                                { (activity as MainActivity).billingManager.subscribe() },
                                { (activity as MainActivity).billingManager.onPurchaseHistoryRestored() })
                        }
                        return
                    }
                    inbodyViewModel.insert(Inbody(
                            selectedDate,
                            textToFloat(editTextWeight),
                            textToFloat(editTextMuscle),
                            textToFloat(editTextPercentFat)))

                    Snackbar.make(calendarView, getString(R.string.text_save_completed), 500).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigation.back()
    }
}