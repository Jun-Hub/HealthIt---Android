package io.jun.healthit.view

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.jun.healthit.R
import io.jun.healthit.adapter.AdapterEventListener
import io.jun.healthit.adapter.ItemTouchHelperCallback
import io.jun.healthit.adapter.RecordListAdapter
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.view.fragment.BaseFragment
import io.jun.healthit.viewmodel.PrefViewModel
import kotlinx.android.synthetic.main.fragment_set_template.*
import kotlinx.android.synthetic.main.include_actionbar.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SetTemplateFragment : BaseFragment(), AdapterEventListener {

    private val prefViewModel: PrefViewModel by sharedViewModel()

    private var templateId:Int? = 0
    private lateinit var recordAdapter: RecordListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        templateId = arguments?.getInt("templateId", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_template, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackActionBar(appbar.toolbar)
        context?.let {
            setView(it)
        }
    }

    private fun setView(context: Context) {
        editText_name.text = SpannableStringBuilder(templateId?.let {
            prefViewModel.getTemplateName(it)
        })

        val records = templateId?.let { prefViewModel.getTemplate(it) }

        val layoutManagerRecord = LinearLayoutManager(context)
        recordAdapter = RecordListAdapter(context, true, onlyForAddNew = false)

        recyclerView_record.apply {
            adapter = recordAdapter
            layoutManager = layoutManagerRecord
        }
        //adapter의 eventlistener를 이 액티비티로 초기화
        recordAdapter.setOnAdapterEventListener(this)
        //아이템 터치 헬퍼 연결
        val callback = ItemTouchHelperCallback(recordAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView_record)

        records?.let {
            for (i in it.indices) {
                recordAdapter.addRecord(it[i])
            }
        }

        btn_add_record.setOnClickListener {
            recordAdapter.addRecordDefault()
            layoutManagerRecord.scrollToPosition(recordAdapter.itemCount-1)
        }
    }

    //RecordAdapter의 viewHolder를 itemTouchHelper에 연결
    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_template, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {  //툴바의 뒤로가기 버튼
                onBackPressed()
                true
            }

            R.id.action_save -> {
                val templateName = if(editText_name.text.toString() == "")
                                        String.format(getString(R.string.routine_num), templateId)
                                    else
                                        editText_name.text.toString()
                //루틴 이름과 루틴템플릿 저장
                templateId?.let {
                    prefViewModel.setTemplateName(it, templateName)
                    prefViewModel.setTemplate(it, recordAdapter.records)
                }
                navigation.back()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //뒤로가기 버튼 클릭
    override fun onBackPressed() {
        super.onBackPressed()

        val templateName = if(editText_name.text.toString() == "")
            String.format(getString(R.string.routine_num), templateId)
        else
            editText_name.text.toString()
        context?.let {
            templateId?.let { it1 ->
                DialogUtil.saveTemplateDialog(layoutInflater, it,
                    it1, templateName, recordAdapter) {
                    navigation.back()
                }
            }
        }
    }
}
