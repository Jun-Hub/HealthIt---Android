package io.jun.healthit.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.jun.healthit.R
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.*
import io.jun.healthit.viewmodel.MemoViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.jun.healthit.adapter.*
import io.jun.healthit.databinding.FragmentAddEditBinding
import io.jun.healthit.view.fragment.BaseFragment
import io.jun.healthit.viewmodel.PrefViewModel
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.IOException
import kotlin.properties.Delegates

class AddEditFragment : BaseFragment(), AdapterEventListener {

    private val TAG = "AddEditFragment"

    private val prefViewModel: PrefViewModel by sharedViewModel()
    private val memoViewModel: MemoViewModel by sharedViewModel()

    private var viewBinding: FragmentAddEditBinding? = null
    private val binding get() = viewBinding!!

    private var memoId:Int? = null

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var recordAdapter: RecordListAdapter
    private lateinit var photoAdapter: PhotoListAdapter
    private lateinit var layoutManagerRecord: LinearLayoutManager
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var currentPhotoPath: String

    private var isNewMemo: Boolean? = null
    private var templateId: Int? = null
    private var tag by Delegates.notNull<Int>()
    private var pin by Delegates.notNull<Boolean>()
    private lateinit var dateOfOriginMemo: String

    private val byteArrayList = ArrayList<ByteArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNewMemo = arguments?.getBoolean("newMemo", false)
        templateId = arguments?.getInt("templateId", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackActionBar(binding.appbar.toolbar)

        context?.let {
            if (!prefViewModel.getTipChecking(Setting.RECORD_EDIT_TIP_FLAG)) {
                DialogUtil.showUseTipDialog(it, layoutInflater)
            }
            isNewMemo?.let { isNew ->
                setRecyclerView(it, isNew)
            }
            setClickListener(it)
        }

        isNewMemo?.let {
            Log.d(TAG, "isNewMemo : $isNewMemo")
            if(it) {
                //오늘 날짜로 설정
                binding.textViewDate.text = getCurrentDate()
                //새일지 쓰기로 넘어왔다면 여기서 Return
                if (templateId == 0) return

                templateId?.let { id ->
                    Log.d(TAG, "11")
                    recordAdapter.clearRecords()    //추가됬었던 레코드 샘플 비우기
                    val records = prefViewModel.getTemplate(id)
                    for (i in records.indices) {
                        recordAdapter.addRecord(records[i])
                    }
                    return
                }
            }
        }

        memoId = arguments?.getInt("id", 0)
        memoViewModel.getMemoById(memoId!!) { memo ->

            Log.d(TAG, "memoId : $memoId")
            binding.apply {
                editTextTitle.setText(memo.title)
                editTextContent.setText(memo.content)
            }

            for (i in memo.record!!.indices)
                recordAdapter.addRecord(memo.record[i])
            memo.date?.let {
                binding.textViewDate.text = it
                dateOfOriginMemo = it
            }
            tag = memo.tag!!
            pin = memo.pin!!

            CoroutineScope(Dispatchers.IO).launch {
                //DB에서 불러온 byteArray를 Bitmap으로 변환 및 리사이클러뷰에 저장
                memo.photo?.forEach {
                    val bitmap =
                        BitmapFactory.decodeByteArray(it, 0, it.size, BitmapFactory.Options())
                    withContext(Dispatchers.Main) {
                        photoAdapter.addPhoto(bitmap)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    //RecordAdapter의 viewHolder를 itemTouchHelper에 연결
    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    private fun setRecyclerView(context: Context, isNewMemo: Boolean) {
        Log.d(TAG, "22")
        layoutManagerRecord = LinearLayoutManager(context)
        photoAdapter = PhotoListAdapter(context, true)
        recordAdapter = RecordListAdapter(context, true, isNewMemo)
        layoutManager =  LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        //리사이클러뷰의 아이템간 구분선
        val itemDecoration = DividerItemDecoration(context, 0).apply {
            ContextCompat.getDrawable(context, R.drawable.divider_photo)?.let { setDrawable(it) }
        }

        binding.recyclerViewRecord.apply {
            adapter = recordAdapter
            layoutManager = layoutManagerRecord
        }

        binding.recyclerViewPhoto.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(itemDecoration)
        }

        //adapter의 eventlistener를 이 액티비티로 초기화
        recordAdapter.setOnAdapterEventListener(this)
        //아이템 터치 헬퍼 연결
        val callback = ItemTouchHelperCallback(recordAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewRecord)
    }

    private fun setClickListener(context: Context) {

        binding.apply {
            btnEditDate.setOnClickListener {
                DialogUtil.dateDialog(textViewDate, context, layoutInflater)
            }

            btnAddRecord.setOnClickListener {
                recordAdapter.addRecordDefault()
                layoutManagerRecord.scrollToPosition(recordAdapter.itemCount - 1)
            }

            //앨범에서 사진 가져오기 버튼
            btnGallery.setOnClickListener {
                if (!isFullPhoto(context))
                //READ_EXTERNAL_STORAGE 권한 체크 후 앨범 접근
                    Permission(context).checkReadPermission()
            }

            //사진 촬영 버튼
            btnTakePhoto.setOnClickListener {
                if (!isFullPhoto(context))
                //CAMERA 권한 체크 후 카메라 접근
                    Permission(context).checkCameraPermission()
            }
        }
    }


    //업로드 가능한 최대 사진 갯수 넘었는지 체크
    private fun isFullPhoto(context: Context) =
        if(photoAdapter.itemCount< Setting.MAX_PHOTO) {
            false
        } else {
            Toast.makeText(context, String.format(getString(R.string.notice_max_photo), Setting.MAX_PHOTO), Toast.LENGTH_LONG).show()
            true
        }

    private fun openGallery() {

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            activity?.packageManager?.let { resolveActivity(it) }
        }
        startActivityForResult(galleryIntent, Setting.GALLERY_REQUEST_CODE)
    }

    @Throws(IOException::class)
    private fun takePhoto(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {

            //임시 파일 생성
            val photoFile = withContext(Dispatchers.IO) {
                ImageUtil.createImageFile(context)
            }
            currentPhotoPath = photoFile.absolutePath

            activity?.let {
                val photoURI = FileProvider.getUriForFile(context, it.packageName, photoFile)

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    resolveActivity(it.packageManager)
                    putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
                startActivityForResult(cameraIntent, Setting.TAKE_PHOTO_REQUEST_CODE)
            }
        }
    }

    private fun addPhoto(uri: Uri?, filePath: String?) {
        Glide.with(this@AddEditFragment)
            .asBitmap()
            .load(uri ?: filePath)
            .into(object : CustomTarget<Bitmap>(800, 600) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    photoAdapter.addPhoto(resource)
                    layoutManager.scrollToPosition(photoAdapter.itemCount - 1)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK ) {
            CoroutineScope(Dispatchers.Main).launch {

                //갤러리에서 사진 가져왔을 때
                if (requestCode == Setting.GALLERY_REQUEST_CODE && data != null) {
                    data.data?.also {

                        context?.let { ctx ->
                            if (ImageUtil.getImageSize(it, ctx) < Setting.IMAGE_SIZE_LIMIT)
                                addPhoto(it, null)
                            else
                                Toast.makeText(ctx, String.format(getString(R.string.notice_max_img_capacity), Setting.IMAGE_SIZE_LIMIT_MB), Toast.LENGTH_LONG).show()
                        }
                    }
                    //카메라 촬영으로 사진 가져왔을 때
                } else if (requestCode == Setting.TAKE_PHOTO_REQUEST_CODE) {
                    addPhoto(null, currentPhotoPath)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_edit, menu)
        val item = menu.findItem(R.id.select_tag)
        item.setActionView(R.layout.layout_spinner)

        //스피닝 아이템 선언 후 연결
        val tagSpinner: Spinner = item.actionView.findViewById(R.id.tag_spinner)
        tagSpinner.adapter = context?.let { TagSpinnerAdapter(it, prefViewModel.getTagSettings(false)) }
        if(isNewMemo != null && !isNewMemo!!)
            arguments?.getInt("tag", 0)?.let { tagSpinner.setSelection(it) }

        tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tag = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            android.R.id.home -> {  //툴바의 뒤로가기 버튼
                onBackPressed()
                true
            }

            R.id.action_save -> {   //저장 버튼

            //TODO 코루틴 공부해서 여기좀 고치자
                CoroutineScope(Dispatchers.Default).launch {
                    isNewMemo?.let {
                        if (isConflictDate(it)) {
                            withContext(Dispatchers.Main) { showConflictToast() }
                            return@launch
                        }
                    }

                    addByteArray()

                    var totalSize = 0
                    for (i in byteArrayList.indices) totalSize += byteArrayList[i].size

                    if (totalSize > Setting.TOTAL_SIZE_LIMIT) {
                        withContext(Dispatchers.Main) {
                            context?.let {
                                Toast.makeText(it, getString(R.string.notice_max_capacity), Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        isNewMemo?.let {
                            if(it)
                                memoViewModel.insert(Memo(0, titleToString(), contentToString(), recordAdapter.records, byteArrayList, binding.textViewDate.text.toString(), tag, false))
                            else
                                memoViewModel.update(Memo(memoId!!, titleToString(), contentToString(), recordAdapter.records, byteArrayList, binding.textViewDate.text.toString(), tag, pin))
                        }
                        navigation.back()
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    //뒤로가기 버튼 클릭
    override fun onBackPressed() {
        super.onBackPressed()

        CoroutineScope(Dispatchers.Default).launch {
            isNewMemo?.let {
            val isConflict = isConflictDate(it)
            addByteArray()

            withContext(Dispatchers.Main) {

                    context?.let { ctx ->
                        DialogUtil.saveMemoDialog(
                            it,
                            ctx,
                            layoutInflater,
                            if (it) null else memoId,
                            titleToString(),
                            contentToString(),
                            recordAdapter.records,
                            byteArrayList,
                            binding.textViewDate.text.toString(),
                            tag,
                            if (it) false else pin,
                            isConflict) {
                            navigation.back()
                        }
                    }
                }
            }
        }
    }

    //해당 날짜에 이미 저장된 기록이 있나 체크
    private fun isConflictDate(isNewMemo: Boolean): Boolean {
        val selectedDate = binding.textViewDate.text.toString()
        if(!isNewMemo && dateOfOriginMemo==selectedDate) return false   //편집하고 있는 메모의 원래 날짜와 같다면 return false

        return memoViewModel.isExist(selectedDate)
    }

    private fun showConflictToast() =
        context?.let { Toast.makeText(it, getString(R.string.notice_conflict_date), Toast.LENGTH_LONG).show() }


    private fun titleToString() = if(isAllBlank(binding.editTextTitle.text.toString())) ""
    else binding.editTextTitle.text.toString()

    private fun contentToString() = if(isAllBlank(binding.editTextContent.text.toString())) ""
    else binding.editTextContent.text.toString()

    private fun addByteArray() {
        //byteArray로 첨부된 이미지 용량 압축
        for (i in 0 until photoAdapter.itemCount) {
            ImageUtil.bitmapToByteArray(photoAdapter.getBitmap(i), 50).also {
                byteArrayList.add(it)
            }
        }
    }

    //user permission 외부 라이브러리 : TedPermission
    inner class Permission(private val context: Context) {

        private var cameraPermission: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {    //permission 허가 상태라면
                this@AddEditFragment.takePhoto(context)
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(context, getString(R.string.deny_take_photo), Toast.LENGTH_LONG).show()
            }
        }
        fun checkCameraPermission() {
            TedPermission.with(context)
                .setPermissionListener(cameraPermission)
                .setRationaleMessage(getString(R.string.request_take_photo))
                .setDeniedMessage(getString(R.string.deny_request))
                .setPermissions(Manifest.permission.CAMERA)
                .check()
        }

        private var readPermission: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {    //permission 허가 상태라면
                this@AddEditFragment.openGallery()
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(context, getString(R.string.deny_gallery), Toast.LENGTH_LONG).show()
            }
        }
        fun checkReadPermission() {
            TedPermission.with(context)
                .setPermissionListener(readPermission)
                .setRationaleMessage(getString(R.string.request_gallery))
                .setDeniedMessage(getString(R.string.deny_request))
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
        }
    }
}
