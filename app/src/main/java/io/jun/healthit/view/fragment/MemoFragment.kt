package io.jun.healthit.view.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.ghyeok.stickyswitch.widget.StickySwitch
import io.ghyeok.stickyswitch.widget.StickySwitch.OnSelectedChangeListener
import io.jun.healthit.R
import io.jun.healthit.adapter.MemoListAdapter
import io.jun.healthit.adapter.SpinnerAdapter
import io.jun.healthit.decorator.*
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.util.EtcUtil
import io.jun.healthit.view.AddEditActivity
import io.jun.healthit.view.MemoDetailActivity
import io.jun.healthit.viewmodel.MemoViewModel
import io.jun.healthit.viewmodel.PrefViewModel
import kotlinx.android.synthetic.main.fragment_memo.*
import kotlinx.android.synthetic.main.item_memo.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//TODO 파이어베이스 애널리스틱 사용하기 https://salix97.tistory.com/139
class MemoFragment : Fragment() {

    //편집 버튼을 on 했는지 MemoListAdapter 에서 관찰하기 위한 livedata
    companion object {
        var editOn: MutableLiveData<Boolean> = MutableLiveData(false)
    }
    private val TAG = "MemoFragment"

    private lateinit var prefViewModel: PrefViewModel
    private lateinit var memoViewModel: MemoViewModel

    private lateinit var memoList: List<Memo>

    private lateinit var memoDetail: View

    private lateinit var memoAdapter: MemoListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var tagSpinner: Spinner
    private lateinit var viewSwitch: StickySwitch
    private lateinit var editSwitch: StickySwitch

    private lateinit var selectedDate: String

    private lateinit var noTagDecorator: Decorator
    private lateinit var tagRedDecorator: Decorator
    private lateinit var tagOrangeDecorator: Decorator
    private lateinit var tagYellowDecorator: Decorator
    private lateinit var tagGreenDecorator: Decorator
    private lateinit var tagBlueDecorator: Decorator
    private lateinit var tagPurpleDecorator: Decorator
    private lateinit var pinDecorator: PinDecorator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_memo, container, false)

        setHasOptionsMenu(true)

        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)
        memoViewModel = ViewModelProvider(this).get(MemoViewModel::class.java)

        memoDetail = root.findViewById(R.id.memo_detail)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdView()
        setCalendarView()
        setMemoRecyclerView()
        initObserve()

        setNewMemoBtn()
    }

    private fun setCalendarView() {
        calendarView.selectedDate = CalendarDay.today()
        updateSelectedDate(CalendarDay.today())
        calendarView.setOnDateChangedListener { _, date, selected ->

            if(selected && ::memoList.isInitialized) {
                updateSelectedDate(date)
                updateCalendarCardView()
            }
        }
    }

    private fun setMemoRecyclerView() {
        memoAdapter = MemoListAdapter(this)
        linearLayoutManager = LinearLayoutManager(this.context)
        val itemDecoration = DividerItemDecoration(this.context, 1)
        ContextCompat.getDrawable(requireContext(), R.drawable.divider_memo)?.let { itemDecoration.setDrawable(it) }

        recyclerView.apply {
            addItemDecoration(itemDecoration)
            layoutManager = linearLayoutManager
            adapter = memoAdapter
        }
    }

    private fun initObserve(){
        //Observer 로 메모 전체 Livedata 가져오기
        memoViewModel.allMemos.observe(viewLifecycleOwner, Observer { memos ->

                memoList = memos

                //저장된 메모가 하나도 없으면 리사이클러뷰 가리고 안내메시지 띄우기
                if (memos.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    textView_no_memo.visibility = View.GONE

                    //메모 작성 시간순으로 배열 후 pin 메모가 맨 위로 가게끔 정렬
                    memoAdapter.setMemos(memos.sortedByDescending { it.date }.sortedByDescending { it.pin })
                    //메모 추가시 스크롤 맨 위로 올리기
                    linearLayoutManager.scrollToPosition(0)

                    initDecorators(memos)
                    updateCalendarCardView()
                }
                else {
                    recyclerView.visibility = View.GONE
                    textView_no_memo.visibility = View.VISIBLE
                }
            })

        editOn.observe(viewLifecycleOwner, { on ->
            Log.d(TAG, "editOn observed $on")
            //TODO memo_detail 계속 null 에러뜸 ㅠ : 두번째 프래그먼트를 계속 터치했을
            memoDetail.delete_btn.visibility =
                if(on) View.VISIBLE else View.GONE
        })
    }

    override fun onPause() {
        super.onPause()
        //보기방식 저장
        prefViewModel.setViewMode(viewSwitch.getDirection().name, requireContext())

        //태그 스피너와 편집 스위치 초기화
        tagSpinner.setSelection(0)
        editSwitch.setDirection(StickySwitch.Direction.LEFT, isAnimate = false, shouldTriggerSelected = true)
        editOn.value = false
        //TODO delete_btn 에러나니까 옵저버들 제거해주기
        Log.d(TAG, "onPause called")
        editOn.removeObservers(this)
    }

    private fun decorateForAllTags() {
        //일지 갱신할 때 마다 매번 데코레이트 오버랩 되니까, 전체 데코레이터 삭제해주고 시작
        calendarView.apply {
            removeDecorators()
            invalidateDecorators()

            addDecorators(
                TodayDecorator(),
                noTagDecorator,
                tagRedDecorator,
                tagOrangeDecorator,
                tagYellowDecorator,
                tagGreenDecorator,
                tagBlueDecorator,
                tagPurpleDecorator,
                pinDecorator
            )
        }
    }

    private fun initDecorators(memos: List<Memo>) {
        noTagDecorator = Decorator(memos.filter { it.tag==0 }, TagColor.DEFAULT)
        tagRedDecorator = Decorator(memos.filter { it.tag==1 }, TagColor.RED)
        tagOrangeDecorator = Decorator(memos.filter { it.tag==2 }, TagColor.ORANGE)
        tagYellowDecorator = Decorator(memos.filter { it.tag==3 }, TagColor.YELLOW)
        tagGreenDecorator = Decorator(memos.filter { it.tag==4 }, TagColor.GREEN)
        tagBlueDecorator = Decorator(memos.filter { it.tag==5 }, TagColor.BLUE)
        tagPurpleDecorator = Decorator(memos.filter { it.tag==6 }, TagColor.PURPLE)
        pinDecorator = PinDecorator(requireContext(), memos.filter { it.pin==true })

        decorateForAllTags()
    }

    private fun decorateByTag(position: Int) {

        if(position==0) {
            decorateForAllTags()
            return
        }
        //적용됬었던 데코레이터들 전부 클리어해주고,
        calendarView.removeDecorators()
        calendarView.invalidateDecorators()

        calendarView.addDecorators(
            TodayDecorator(),
            when(position) {
                2 -> tagRedDecorator
                3 -> tagOrangeDecorator
                4 -> tagYellowDecorator
                5 -> tagGreenDecorator
                6 -> tagBlueDecorator
                7 -> tagPurpleDecorator
                else -> noTagDecorator
            }
        )

    }

    private fun isInitAllTags(): Boolean =
        ::noTagDecorator.isInitialized &&
                ::tagRedDecorator.isInitialized &&
                ::tagOrangeDecorator.isInitialized &&
                ::tagYellowDecorator.isInitialized &&
                ::tagGreenDecorator.isInitialized &&
                ::tagBlueDecorator.isInitialized &&
                ::tagPurpleDecorator.isInitialized &&
                ::pinDecorator.isInitialized

    private fun updateCalendarCardView() {
        if(!::selectedDate.isInitialized) return

        val selectedMemo = memoList.find { it.date ==  selectedDate }

        if(selectedMemo==null) {
            cardView_detail.visibility = View.GONE
        }
        else {
            cardView_detail.visibility = View.VISIBLE

            val setAndVolume = selectedMemo.record?.let { EtcUtil.calculateSetAndVolume(it) }

            memo_detail.textView_title.text = selectedMemo.title
            memo_detail.textView_record.text = String.format(getString(R.string.memo_record), setAndVolume?.first, setAndVolume?.second)
            memo_detail.textView_content.text = selectedMemo.content
            memo_detail.textView_date.text = selectedMemo.date
            selectedMemo.photo?.let { setImageThumbnail(it) }
            selectedMemo.tag?.let { setTag(it) }
            selectedMemo.pin?.let { memo_detail.imageView_pin.visibility =
                if(it) View.VISIBLE else View.GONE }

            memo_detail.delete_btn.setOnClickListener {
                memoViewModel.delete(selectedMemo)
            }

            cardView_detail.setOnClickListener {
                val pinStatus = selectedMemo.pin //val 값으로 셋팅안해주면 원래 형태가 var이라 intent에 넣지 못함
                startActivity(Intent(requireContext(), MemoDetailActivity::class.java).apply {
                    putExtra("id", selectedMemo.id)
                    putExtra("pin", pinStatus)    //pin 상태도 같이 넘겨야 다음 액티비티에서 초기화 null 에러가 안남
                })
            }
        }
    }

    private fun updateSelectedDate(date: CalendarDay) {
        val day = if(date.day.toString().length==1) "0${date.day}" else "${date.day}"
        selectedDate = "${date.year}/${date.month+1}/${day}"
    }

    private fun setImageThumbnail(photo: List<ByteArray>) {
        if (photo.isNotEmpty()) {
            CoroutineScope(Dispatchers.Default).launch {

                Glide.with(this@MemoFragment)
                    .asBitmap()
                    .load(photo[0])
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            memo_detail.imageView.visibility = View.VISIBLE
                            memo_detail.imageView.setImageBitmap(resource)
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })

            }
        }
        else
            memo_detail.imageView.visibility = View.GONE
    }

    private fun setTag(tag: Int) {
        memo_detail.imageView_tag.setImageResource(
            when (tag) {
                1 -> R.drawable.ic_circle_red
                2 -> R.drawable.ic_circle_orange
                3 -> R.drawable.ic_circle_yellow
                4 -> R.drawable.ic_circle_green
                5 -> R.drawable.ic_circle_blue
                6 -> R.drawable.ic_circle_purple
                else -> R.drawable.transparent
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //툴바의 스위치 버튼 세팅
        inflater.inflate(R.menu.menu_list, menu)

        initMemoSortSpinner(menu)
        initViewModeSwitch(menu)
        initEditModeSwitch(menu)
    }

    private fun initMemoSortSpinner(menu: Menu) {
        //메모 정렬 스피너 셋팅
        val sort = menu.findItem(R.id.action_sort)
        sort.setActionView(R.layout.layout_spinner)
        tagSpinner = sort.actionView.findViewById(R.id.tag_spinner)
        tagSpinner.adapter = context?.let { SpinnerAdapter(it, prefViewModel.getTagSettings(it, true)) }

        tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                memoAdapter.changeMemoByTag(position)

                if(isInitAllTags())
                    decorateByTag(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initViewModeSwitch(menu: Menu) {
        //보기방식 스위치 셋팅
        val view = menu.findItem(R.id.action_view)
        view.setActionView(R.layout.layout_switch_view)
        viewSwitch = view.actionView.findViewById(R.id.sticky_switch_view)

        //마지막으로 설정했던 view mode
        val savedViewMode: StickySwitch.Direction
        if(prefViewModel.getViewMode(requireContext())=="LEFT") {
            savedViewMode = StickySwitch.Direction.LEFT
            showListView()
        }
        else {
            savedViewMode = StickySwitch.Direction.RIGHT
            showCalendarView()
        }

        viewSwitch.setDirection(savedViewMode, isAnimate = false, shouldTriggerSelected = true)
        viewSwitch.setLeftIcon(R.drawable.ic_list_bulleted_24)
        viewSwitch.setRightIcon(R.drawable.ic_calendar_24)

        viewSwitch.onSelectedChangeListener = object : OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                if(direction.name == "RIGHT") {
                    showCalendarView()
                }
                else {
                    showListView()
                }
            }
        }
    }

    private fun initEditModeSwitch(menu: Menu) {
        // 편집모드 스위치 셋팅
        val edit = menu.findItem(R.id.action_edit)
        edit.setActionView(R.layout.layout_switch_edit)
        editSwitch = edit.actionView.findViewById(R.id.sticky_switch_edit)
        editSwitch.onSelectedChangeListener = object : OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String
            ) {
                editOn.value = direction.name == "RIGHT"
            }
        }
        editSwitch.setRightIcon(R.drawable.ic_delete_release)
    }

    private fun initAdView() {
        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun showCalendarView() {
        //TODO 메모가 하나도 없을 때도 고려해주기
        fast_scroller.visibility = View.GONE
        recyclerView.visibility = View.GONE
        calendarView.visibility = View.VISIBLE
        cardView_detail.visibility = View.VISIBLE
        scrollView_calendar.visibility = View.VISIBLE
    }

    private fun showListView() {
        fast_scroller.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        calendarView.visibility = View.GONE
        cardView_detail.visibility = View.GONE
        scrollView_calendar.visibility = View.GONE
    }

    private fun setNewMemoBtn() {
        add_memo_btn.apply {
            addActionItem(
                SpeedDialActionItem.Builder(R.id.new_memo, R.drawable.ic_new_memo)
                    .setFabBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorSky,
                            null
                        )
                    )
                    .setLabel(getString(R.string.new_log))
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorSky,
                            null
                        )
                    )
                    .setLabelClickable(false)
                    .create()
            )
            addActionItem(
                SpeedDialActionItem.Builder(R.id.open_template, R.drawable.ic_open_template)
                    .setFabBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorLightOrange,
                            null
                        )
                    )
                    .setLabel(getString(R.string.open_my_routine))
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorLightOrange,
                            null
                        )
                    )
                    .setLabelClickable(false)
                    .create()
            )
        }

        add_memo_btn.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.new_memo -> {
                    startActivity(Intent(this.context, AddEditActivity::class.java).putExtra("newMemo", true))
                    add_memo_btn.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.open_template -> {
                    DialogUtil.showTemplateDialog(this, false)
                    add_memo_btn.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })
    }
}