package io.jun.healthit.view.fragment

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zagum.switchicon.SwitchIconView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.jun.healthit.FragmentFactory
import io.jun.healthit.R
import io.jun.healthit.adapter.PhotoListAdapter
import io.jun.healthit.adapter.RecordListAdapter
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.util.ImageUtil
import io.jun.healthit.viewmodel.MemoViewModel
import io.jun.healthit.viewmodel.PrefViewModel
import kotlinx.android.synthetic.main.fragment_memo_detail.*
import kotlinx.android.synthetic.main.include_actionbar.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class MemoDetailFragment(private val memoId:Int, private val pinState:Boolean) : BaseFragment() {

    private val TAG = javaClass.simpleName
    private lateinit var recordAdapter: RecordListAdapter

    private val prefViewModel: PrefViewModel by sharedViewModel()
    private val memoViewModel: MemoViewModel by sharedViewModel()
    private val dialogUtil: DialogUtil by inject{ parametersOf(activity) }

    private lateinit var memo: Memo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_memo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackActionBar(appbar.toolbar)

        context?.let {
            setView(it)
        }
    }

    private fun setView(context: Context) {
        val photoAdapter = PhotoListAdapter(context, false)
        val itemDecoration = DividerItemDecoration(context, 0).apply {
            ContextCompat.getDrawable(context, R.drawable.divider_photo)?.let { setDrawable(it) } //아이템간 구분선
        }

        val layoutManagerRecord = LinearLayoutManager(context)
        recordAdapter = RecordListAdapter(context, false, onlyForAddNew = false) { index, record ->
            dialogUtil.editRecordDialog(recordAdapter, layoutInflater, index, record)
        }

        recyclerView_record.apply {
            adapter = recordAdapter
            layoutManager = layoutManagerRecord
        }

        recyclerView_photo.apply {
            adapter = photoAdapter
            addItemDecoration(itemDecoration)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)   //리사이클러뷰 가로 스크롤
        }

        scope.launch {
            memo = memoViewModel.getMemoById(memoId)

            if(memo.title=="") {
                textView_title.visibility = View.GONE
            } else {
                textView_title.visibility = View.VISIBLE
                textView_title.text = memo.title
            }

            if(memo.content=="") {
                textView_content.visibility = View.GONE
            } else {
                textView_content.apply {
                    visibility = View.VISIBLE
                    text = memo.content
                    movementMethod = ScrollingMovementMethod()  //textView 스크롤 만들기
                }
            }

            memo.record?.forEach { recordAdapter.addRecord(it) }

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
                prefViewModel.getOneOfTagSettings(it)
            }

            if (memo.photo.isNullOrEmpty())
                recyclerView_photo.visibility = View.GONE
            else {
                recyclerView_photo.visibility = View.VISIBLE

                //bitmap으로 디코딩 후 리사이클러뷰에 추가
                memo.photo?.forEach {
                    val bitmap =
                        withContext(Dispatchers.Default) {
                            BitmapFactory.decodeByteArray(it, 0, it.size, BitmapFactory.Options())
                        }
                    photoAdapter.addPhoto(bitmap)
                }
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_memo_detail, menu)
        val pin = menu.findItem(R.id.action_pin)
        pin.setActionView(R.layout.layout_switch_pin)

        val switchPinBtn:SwitchIconView = pin.actionView.findViewById(R.id.switch_toolBar)
        //스위치 버튼 초기상태를 저장 상태에 따라 on / off 해주기
        switchPinBtn.setIconEnabled(pinState)

        switchPinBtn.setOnClickListener {
            //클릭시 마다 스위치의 상태를 on/off함
            switchPinBtn.switchState()
            if(switchPinBtn.isIconEnabled) {
                memo.pin = true
                memoViewModel.update(memo)
                Toast.makeText(context, getString(R.string.notice_pin), Toast.LENGTH_SHORT).show()
            } else {
                memo.pin = false
                memoViewModel.update(memo)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {  //툴바의 뒤로가기 버튼
                onBackPressed()
                true
            }
            R.id.action_add_template -> {   //현 루틴 템플릿에 추가하기 버튼
                memo.record?.let {
                    dialogUtil.addDirectlyTemplateDialog(layoutInflater, it)
                }
                true
            }
            R.id.action_save_as_image -> {
                context?.let { Permission(it).checkWritePermission() }
                true
            }
            R.id.action_edit -> {   //편집 버튼
                memo.tag?.let {
                    navigation.finishAndMove(FragmentFactory.getAddEditFragment(false, null, it, memoId))
                }
                true
            }
            R.id.action_delete -> { //삭제 버튼
                val memo = Memo(memoId, null, null, null, null, null, null, null)
                dialogUtil.deleteDialog(layoutInflater, memo) { navigation.back() }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigation.back()
    }

    inner class Permission(private val context: Context) {

        private var writePermission: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {    //permission 허가 상태라면
                //운동기록 이미지로 저장하기
                ImageUtil.saveImage(ImageUtil.getBitmapFromRecyclerView(recyclerView_record), context, getString(R.string.app_name))
                Toast.makeText(context, R.string.screenshot_success, Toast.LENGTH_SHORT).show()
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(context, getString(R.string.deny_screenshot), Toast.LENGTH_LONG).show()
            }
        }
        fun checkWritePermission() {
            TedPermission.with(context)
                .setPermissionListener(writePermission)
                .setRationaleMessage(getString(R.string.request_screenshot))
                .setDeniedMessage(getString(R.string.deny_request))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        }
    }
}
