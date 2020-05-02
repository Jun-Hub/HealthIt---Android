package io.jun.healthit.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.jun.healthit.R
import io.jun.healthit.adapter.RecordListAdapter
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.viewmodel.PrefViewModel
import kotlinx.android.synthetic.main.activity_set_template.*
import kotlin.properties.Delegates

class SetTemplateActivity : AppCompatActivity() {

    private lateinit var prefViewModel: PrefViewModel

    private var templateId by Delegates.notNull<Int>()
    private lateinit var recordAdapter: RecordListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_template)

        templateId = intent.getIntExtra("id", 0)

        //툴바 세팅
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = null

        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)

        editText_name.text = SpannableStringBuilder(prefViewModel.getTemplateName(templateId, this))

        val records = prefViewModel.getTemplate(templateId, this)

        val layoutManagerRecord = LinearLayoutManager(this)
        recordAdapter = RecordListAdapter(this, true, onlyForAddNew = false)

        recyclerView_record.apply {
            adapter = recordAdapter
            layoutManager = layoutManagerRecord
        }

        for(i in records.indices) {
            recordAdapter.addRecord(records[i])
        }

        btn_add_record.setOnClickListener {
            recordAdapter.addRecordDefault()
            layoutManagerRecord.scrollToPosition(recordAdapter.itemCount-1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_template, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {  //툴바의 뒤로가기 버튼
                val templateName = if(editText_name.text.toString() == "")
                                        String.format(getString(R.string.routine_num), templateId)
                                    else
                                        editText_name.text.toString()
                DialogUtil.saveTemplateDialog(layoutInflater, this, templateId, templateName, recordAdapter)
                true
            }

            R.id.action_save -> {
                val templateName = if(editText_name.text.toString() == "")
                                        String.format(getString(R.string.routine_num), templateId)
                                    else
                                        editText_name.text.toString()
                //루틴 이름과 루틴템플릿 저장
                prefViewModel.setTemplateName(templateId, templateName, this)
                prefViewModel.setTemplate(templateId, recordAdapter.records, this)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //뒤로가기 버튼 클릭
    override fun onBackPressed() {
        DialogUtil.saveTemplateDialog(layoutInflater, this, templateId, editText_name.text.toString(), recordAdapter)
    }
}
