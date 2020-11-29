package io.jun.healthit.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.jun.healthit.R
import io.jun.healthit.adapter.InbodySpinnerAdapter
import io.jun.healthit.databinding.FragmentInbodyBinding
import io.jun.healthit.decorator.TextDecorator
import io.jun.healthit.decorator.TodayDecorator
import io.jun.healthit.model.data.Inbody
import io.jun.healthit.util.EtcUtil
import io.jun.healthit.viewmodel.InbodyViewModel
import io.jun.healthit.viewmodel.PrefViewModel

class InbodyFragment : Fragment() {

    private val TAG = "InbodyFragment"

    private lateinit var prefViewModel: PrefViewModel
    private lateinit var inbodyViewModel: InbodyViewModel

    private var viewBinding: FragmentInbodyBinding? = null
    private val binding get() = viewBinding!!

    private lateinit var inbodyList: List<Inbody>
    private lateinit var selectedDate: String

    private var isWeightValid = false
    private var isMuscleValid = false
    private var isPercentFatValid = false

    private val textDecoratorList = mutableListOf<TextDecorator>()

    private lateinit var inbodySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)
        inbodyViewModel = ViewModelProvider(this).get(InbodyViewModel::class.java)
    }

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

        setAdView()
        setCalendarView()
        setClickListener()
        initTextWatcher()
        initObserve()
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

    private fun setAdView() {
        MobileAds.initialize(requireContext())
        AdRequest.Builder().build().let {
            binding.adView.loadAd(it)
        }
    }

    private fun setClickListener() {
        binding.apply {
            imageBtnUpWeight.setOnClickListener {
                editTextWeight.text = EtcUtil.makePlusFloat(editTextWeight, 1f)
            }
            imageBtnDownWeight.setOnClickListener {
                editTextWeight.text = EtcUtil.makeMinusFloat(editTextWeight, 1f)
            }
            imageBtnUpMuscle.setOnClickListener {
                editTextMuscle.text = EtcUtil.makePlusFloat(editTextMuscle, 1f)
            }
            imageBtnDownMuscle.setOnClickListener {
                editTextMuscle.text = EtcUtil.makeMinusFloat(editTextMuscle, 1f)
            }
            imageBtnUpPercentFat.setOnClickListener {
                editTextPercentFat.text = EtcUtil.makePlusFloat(editTextPercentFat, 1f)
            }
            imageBtnDownPercentFat.setOnClickListener {
                editTextPercentFat.text = EtcUtil.makeMinusFloat(editTextPercentFat, 1f)
            }

            btnSave.setOnClickListener {
                inbodyViewModel.insert(
                    Inbody(
                        selectedDate,
                        textToFloat(editTextWeight),
                        textToFloat(editTextMuscle),
                        textToFloat(editTextPercentFat)
                    )
                )

                Toast.makeText(context, getString(R.string.text_save_completed), Toast.LENGTH_SHORT).show()
            }
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
                    inActivateButton(binding.btnSave)
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
            activateButton(binding.btnSave)
        else
            inActivateButton(binding.btnSave)
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
            inbodySpinner.setSelection(prefViewModel.getInbodySpinner(it))

            inbodySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(::inbodyList.isInitialized) {
                        initDecorator(inbodyList, position)
                    }
                    prefViewModel.setInbodySpinner(position, it)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

}