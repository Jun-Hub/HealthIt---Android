package io.jun.healthit.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.jun.healthit.R
import io.jun.healthit.adapter.PhotoListAdapter
import io.jun.healthit.adapter.RecordListAdapter
import io.jun.healthit.model.Memo
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.viewmodel.MemoViewModel
import com.github.zagum.switchicon.SwitchIconView
import io.jun.healthit.viewmodel.PrefViewModel
import kotlinx.android.synthetic.main.activity_memo_detail.*
import kotlinx.coroutines.*

class MemoDetailActivity : AppCompatActivity() {

    private lateinit var prefViewModel: PrefViewModel

    private lateinit var recordAdapter: RecordListAdapter
    private lateinit var memoViewModel: MemoViewModel
    private lateinit var memoActualLive: LiveData<Memo>
    private lateinit var memo:Memo

    private var memoId: Int? = null
    private var alreadyLoad = false //데이터가 변경됨에 따라 observer에서 계속 리스트가 add되는것을 방지하기위한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_detail)

        //툴바 세팅
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        memoId = intent.getIntExtra("id", 0)

        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)

        memoViewModel = ViewModelProvider(this).get(MemoViewModel::class.java)
        val photoAdapter = PhotoListAdapter(this, false)
        val itemDecoration = DividerItemDecoration(this@MemoDetailActivity, 0).apply {
            this@MemoDetailActivity.getDrawable(R.drawable.divider_photo)?.let { setDrawable(it) } //아이템간 구분선
        }

        val layoutManagerRecord = LinearLayoutManager(this)
        recordAdapter = RecordListAdapter(this, false, onlyForAddNew = false)

        recyclerView_record.apply {
            adapter = recordAdapter
            layoutManager = layoutManagerRecord
        }

        recyclerView_photo.apply {
            adapter = photoAdapter
            addItemDecoration(itemDecoration)
            layoutManager = LinearLayoutManager(this@MemoDetailActivity, LinearLayoutManager.HORIZONTAL, false)   //리사이클러뷰 가로 스크롤
        }

        memoActualLive = memoViewModel.getMemoById(memoId!!)
        memoActualLive.observe(this@MemoDetailActivity, Observer { memo ->

            this.memo = memo

            if(memo.title=="") {
                textView_title.apply {
                    text = getString(R.string.no_title)
                    setTextColor(Color.LTGRAY) }
            } else textView_title.text = memo.title


            if(memo.content=="") {
                textView_content.visibility = View.GONE
            } else {
                textView_content.apply {
                    visibility = View.VISIBLE
                    text = memo.content
                    movementMethod = ScrollingMovementMethod()  //textView 스크롤 만들기
                }
            }

            if(!alreadyLoad) {
                for (i in memo.record!!.indices)
                    recordAdapter.addRecord(memo.record[i])
            }

            textView_date.text = String.format(getString(R.string.create_date), memo.date)
            imageView_tag.setImageResource(when(memo.tag) {
                1 -> R.drawable.ic_circle_red
                2 -> R.drawable.ic_circle_orange
                3 -> R.drawable.ic_circle_yellow
                4 -> R.drawable.ic_circle_green
                5 -> R.drawable.ic_circle_blue
                6 -> R.drawable.ic_circle_purple
                else -> R.drawable.transparent
            })

            textView_tag.text = if(memo.tag==0) "" else memo.tag?.let {
                prefViewModel.getOneOfTagSettings(this,
                    it
                )
            }

            if(!alreadyLoad) {
                if (memo.photo.isNullOrEmpty())
                    recyclerView_photo.visibility = View.GONE
                else {
                    recyclerView_photo.visibility = View.VISIBLE

                    CoroutineScope(Dispatchers.IO).launch {
                        //bitmap으로 디코딩 후 리사이클러뷰에 추가
                        for (i in memo.photo.indices) {
                            val bitmap =
                                BitmapFactory.decodeByteArray(memo.photo[i], 0, memo.photo[i].size, BitmapFactory.Options())
                            withContext(Dispatchers.Main) {
                                photoAdapter.addPhoto(bitmap)
                            }
                        }
                    }

                }
            }

            alreadyLoad = true
        })
    }

    override fun onPause() {
        super.onPause()
        //메모 프래그먼트에서 메모를 삭제 했을때 livedata nullPointException 방지를 위해 observer 제거
        if (memoActualLive.hasObservers())
                memoActualLive.removeObservers(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_memo_detail, menu)
        val pin = menu.findItem(R.id.action_pin)
        pin.setActionView(R.layout.layout_switch_pin)

        val switchPinBtn:SwitchIconView = pin.actionView.findViewById(R.id.switch_toolBar)
        //스위치 버튼 초기상태를 저장 상태에 따라 on / off 해주기
        switchPinBtn.setIconEnabled(intent.getBooleanExtra("pin", false))

        switchPinBtn.setOnClickListener {
            //클릭시 마다 스위치의 상태를 on/off함
            switchPinBtn.switchState()
            if(switchPinBtn.isIconEnabled) {
                memo.pin = true
                memoViewModel.update(memo)
                Toast.makeText(this, getString(R.string.notice_pin), Toast.LENGTH_SHORT).show()
            } else {
                memo.pin = false
                memoViewModel.update(memo)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {  //툴바의 뒤로가기 버튼
                finish()
                true
            }
            R.id.action_edit -> {   //편집 버튼
                startActivity(Intent(this, AddEditActivity::class.java)
                    .putExtra("id", memoId)
                    .putExtra("tag", memo.tag))
                finish()
                true
            }
            R.id.action_delete -> { //삭제 버튼
                val memo = Memo(memoId!!, null, null, null, null, null, null, null)
                DialogUtil.deleteDialog(this, this, layoutInflater, memoViewModel, memoActualLive, memo)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
