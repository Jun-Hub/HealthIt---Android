package io.jun.healthit.view.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.jun.healthit.R
import io.jun.healthit.adapter.MemoListAdapter
import io.jun.healthit.adapter.SpinnerAdapter
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.view.AddEditActivity
import io.jun.healthit.viewmodel.MemoViewModel
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import io.jun.healthit.viewmodel.PrefViewModel

class MemoFragment : Fragment() {

    //편집 버튼을 on 했는지 MemoListAdapter 에서 관찰하기 위한 livedata
    companion object {
        var editOn: MutableLiveData<Boolean> = MutableLiveData(false)
    }

    private lateinit var prefViewModel: PrefViewModel

    private lateinit var memoAdapter: MemoListAdapter
    private lateinit var addBtn: SpeedDialView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_memo, container, false)

        MobileAds.initialize(this.context)
        val mAdView: AdView = root.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)

        setHasOptionsMenu(true)

        memoAdapter = MemoListAdapter(this)
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        addBtn = root.findViewById(R.id.add_btn)
        val textViewNoMemo: TextView = root.findViewById(R.id.textView_no_memo)
        val itemDecoration = DividerItemDecoration(this.context, 1)
        requireContext().getDrawable(R.drawable.divider_memo)?.let { itemDecoration.setDrawable(it) }

        val layoutM = LinearLayoutManager(this.context)

        recyclerView.apply {
            addItemDecoration(itemDecoration)
            layoutManager = layoutM
            adapter = memoAdapter
        }

        //Observer 로 메모 전체 Livedata 가져오기
        ViewModelProvider(this).get(MemoViewModel::class.java)
            .allMemos.observe(requireActivity(), Observer { memos ->

            //저장된 메모가 하나도 없으면 리사이클러뷰 가리고 안내메시지 띄우기
            if (memos.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                textViewNoMemo.visibility = View.GONE

                //메모 작성 시간순으로 배열 후 pin 메모가 맨 위로 가게끔 정렬
                memoAdapter.setMemos(memos.sortedByDescending { it.date }.sortedByDescending { it.pin })

                //메모 추가시 스크롤 맨 위로 올리기
                layoutM.scrollToPosition(0)
            } else {
                recyclerView.visibility = View.GONE
                textViewNoMemo.visibility = View.VISIBLE
            }
        })


        initFab()

        addBtn.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.new_memo -> {
                    startActivity(Intent(this.context, AddEditActivity::class.java).putExtra("newMemo", true))
                    addBtn.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.open_template -> {
                    DialogUtil.showTemplateDialog(this, false)
                    addBtn.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //툴바의 스위치 버튼 세팅
        inflater.inflate(R.menu.menu_list, menu)
        val edit = menu.findItem(R.id.action_edit)
        edit.setActionView(R.layout.layout_switch)

        val switchBtn: MaterialAnimatedSwitch = edit.actionView.findViewById(R.id.switch_toolBar)
        switchBtn.setOnCheckedChangeListener { isChecked ->
            //매터리얼 스위치 체크 상태에 따라 livedata on, off
            editOn.value = isChecked
        }

        //메모 정렬 스피너 셋팅
        val sort = menu.findItem(R.id.action_sort)
        sort.setActionView(R.layout.layout_spinner)
        val tagSpinner: Spinner = sort.actionView.findViewById(R.id.tag_spinner)
        tagSpinner.adapter =
            context?.let { SpinnerAdapter(it, prefViewModel.getTagSettings(it, true)) }

        tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                memoAdapter.sortMemoList(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initFab() {
        addBtn.apply {
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
    }
}