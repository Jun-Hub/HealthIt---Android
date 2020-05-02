package io.jun.healthit.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.jun.healthit.R
import io.jun.healthit.adapter.PhotoListAdapter
import io.jun.healthit.adapter.RecordListAdapter
import io.jun.healthit.adapter.SpinnerAdapter
import io.jun.healthit.model.Memo
import io.jun.healthit.util.*
import io.jun.healthit.viewmodel.MemoViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.jun.healthit.viewmodel.PrefViewModel
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class AddActivity : AppCompatActivity() {

    private lateinit var memoViewModel: MemoViewModel
    private lateinit var prefViewModel: PrefViewModel

    private lateinit var recordAdapter: RecordListAdapter
    private lateinit var photoAdapter: PhotoListAdapter
    private lateinit var layoutM: LinearLayoutManager
    private lateinit var currentPhotoPath: String
    private lateinit var photoURI: Uri

    private var tag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val templateId = intent.getIntExtra("id", 0)

        //툴바 세팅
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        memoViewModel = ViewModelProvider(this).get(MemoViewModel::class.java)
        prefViewModel = ViewModelProvider(this).get(PrefViewModel::class.java)

        layoutM = LinearLayoutManager(this@AddActivity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerRecord = LinearLayoutManager(this)
        recordAdapter = RecordListAdapter(this, canBeEdited = true, onlyForAddNew = true)
        photoAdapter = PhotoListAdapter(this, true)

        //RecyclerView_photo의 아이템간 구분선
        val itemDecoration = DividerItemDecoration(this@AddActivity, 0).apply {
            setDrawable(this@AddActivity.getDrawable(R.drawable.divider_photo)!!)
        }

        recyclerView_record.apply {
            adapter = recordAdapter
            layoutManager = layoutManagerRecord
        }

        recyclerView_photo.apply {
            adapter = photoAdapter
            layoutManager = layoutM
            addItemDecoration(itemDecoration)
        }

        //템플릿으로 열기로 넘어왔다면
        if(templateId != 0) {
            recordAdapter.clearRecords()    //추가됬었던 레코드 샘플 비우기
            val records = prefViewModel.getTemplate(templateId, this)
            for(i in records.indices) {
                recordAdapter.addRecord(records[i])
            }
        }

        //오늘 날짜로 설정
        textView_date.text = EtcUtil.getCurrentDate()
        btn_edit_date.setOnClickListener {
            DialogUtil.dateDialog(textView_date, this, layoutInflater)
        }

        btn_add_record.setOnClickListener {
            recordAdapter.addRecordDefault()
            layoutManagerRecord.scrollToPosition(recordAdapter.itemCount-1)
        }

        //앨범에서 사진 가져오기 버튼
        btn_gallery.setOnClickListener {
            if(!isFullPhoto())
                //READ_EXTERNAL_STORAGE 권한 체크 후 앨범 접근
                Permission(this).checkReadPermission()
        }

        //사진 촬영 버튼
        btn_take_photo.setOnClickListener {
            if(!isFullPhoto())
                //CAMERA 권한 체크 후 카메라 접근
                Permission(this).checkCameraPermission()
        }

    }


    //업로드 가능한 최대 사진 갯수 넘었는지 체크
    private fun isFullPhoto() =
        if(photoAdapter.itemCount< Setting.MAX_PHOTO) {
            false
        } else {
            Toast.makeText(this, String.format(getString(R.string.notice_max_photo), Setting.MAX_PHOTO)
                            , Toast.LENGTH_LONG).show()
            true
        }


    //앨범 열기
    private fun openGallery() {

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            resolveActivity(packageManager)
        }
        startActivityForResult(galleryIntent, Setting.GALLERY_REQUEST_CODE)
    }


    //카메라 촬영 열기
    @Throws(IOException::class)
    private fun takePhoto() {
        CoroutineScope(Dispatchers.Default).launch {

            //임시 파일 생성
            val photoFile = withContext(Dispatchers.IO) {
                ImageUtil.createImageFile(this@AddActivity)
            }
            currentPhotoPath = photoFile.absolutePath
            photoURI = FileProvider.getUriForFile(this@AddActivity, packageName, photoFile)

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                resolveActivity(packageManager)
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            startActivityForResult(cameraIntent, Setting.TAKE_PHOTO_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK ) {
            CoroutineScope(Dispatchers.Main).launch {

                //갤러리에서 사진 가져왔을 때
                if (requestCode == Setting.GALLERY_REQUEST_CODE && data != null) {
                    data.data?.also {

                        val imageSize = ImageUtil.getImageSize(it, this@AddActivity)

                        if (imageSize < Setting.IMAGE_SIZE_LIMIT) {

                            //sdk 버전에 따라 다르게 비트맵으로 변환
                            val imageBitmap = when {
                                Build.VERSION.SDK_INT > 28 -> {
                                    val source = ImageDecoder.createSource( this@AddActivity.contentResolver, it)
                                    withContext(Dispatchers.IO) { ImageDecoder.decodeBitmap(source) }
                                }
                                else -> withContext(Dispatchers.IO) { MediaStore.Images.Media.getBitmap(this@AddActivity.contentResolver, it)}
                            }

                            //가져온 사진을 원래 각도로 회전
                            val galleryPhotoPath = FilePathUtil.getPath(this@AddActivity, it)
                            if (galleryPhotoPath != null) {
                                val exifOrientation = withContext(Dispatchers.IO) {

                                    ExifInterface(galleryPhotoPath).getAttributeInt(
                                        ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_UNDEFINED
                                    )

                                }

                                ImageUtil.exifOrientationToDegrees(exifOrientation, imageBitmap)
                                    .also { rotatedBitmap ->
                                        //리사이클러뷰에 사진 추가
                                        photoAdapter.addPhoto(rotatedBitmap)
                                        layoutM.scrollToPosition(photoAdapter.itemCount - 1)
                                    }
                            }

                        } else {
                            Toast.makeText(this@AddActivity, String.format(getString(R.string.notice_max_img_capacity), Setting.IMAGE_SIZE_LIMIT_MB), Toast.LENGTH_LONG).show()
                        }

                    }
                    //카메라 촬영으로 사진 가져왔을 때
                } else if (requestCode == Setting.TAKE_PHOTO_REQUEST_CODE) {
                    //사진을 원래 각도로 회전
                    val exifOrientation = withContext(Dispatchers.IO){
                        ExifInterface(currentPhotoPath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                    }

                    BitmapFactory.decodeFile(currentPhotoPath, ImageUtil.decodingOption()).also {
                        ImageUtil.exifOrientationToDegrees(exifOrientation, it).also { rotatedBitmap ->
                            //리사이클러뷰에 사진 추가
                            photoAdapter.addPhoto(rotatedBitmap)
                            layoutM.scrollToPosition(photoAdapter.itemCount - 1)
                        }
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        val item = menu.findItem(R.id.select_tag)
        item.setActionView(R.layout.layout_spinner)

        //스피닝 아이템 선언 후 연결
        val tagSpinner: Spinner = item.actionView.findViewById(R.id.tag_spinner)
        tagSpinner.adapter = SpinnerAdapter(this, prefViewModel.getTagSettings(this, false))

        tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tag = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {  //툴바의 뒤로가기 버튼
                CoroutineScope(Dispatchers.Default).launch {

                    //메모 내용 및 이미지 정리
                    val title = if(EtcUtil.isAllBlank(editText_title.text.toString())) ""
                                else editText_title.text.toString()

                    val content = if(EtcUtil.isAllBlank(editText_content.text.toString())) ""
                                    else editText_content.text.toString()
                    val byteArrayList = ArrayList<ByteArray>()

                    //byteArray로 첨부된 이미지 용량 압축
                    for (i in 0 until photoAdapter.itemCount) {
                        ImageUtil.bitmapToByteArray(photoAdapter.getBitmap(i), 10).also {
                            byteArrayList.add(it)
                        }
                    }


                    withContext(Dispatchers.Main) {
                        DialogUtil.saveMemoDialog(true, this@AddActivity, layoutInflater, memoViewModel,
                            null, title, content, recordAdapter.records, byteArrayList, textView_date.text.toString(), tag, false)
                    }

                }

                true
            }

            R.id.action_save -> {   //저장 버튼
                CoroutineScope(Dispatchers.Default).launch {

                    //메모 내용 및 이미지 정리
                    val title = if(EtcUtil.isAllBlank(editText_title.text.toString())) ""
                                else editText_title.text.toString()

                    val content = if(EtcUtil.isAllBlank(editText_content.text.toString())) ""
                                else editText_content.text.toString()

                    val byteArrayList = ArrayList<ByteArray>()
                    var totalSize = 0


                        //byteArray로 첨부된 이미지 용량 압축
                        for (i in 0 until photoAdapter.itemCount) {
                            ImageUtil.bitmapToByteArray(photoAdapter.getBitmap(i), 10).also {
                                byteArrayList.add(it)
                                totalSize += it.size
                            }
                        }


                    if (totalSize > Setting.TOTAL_SIZE_LIMIT) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddActivity, getString(R.string.notice_max_capacity), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        memoViewModel.insert(Memo(0, title, content, recordAdapter.records, byteArrayList, textView_date.text.toString(), tag, false))
                        finish()
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //뒤로가기 버튼 클릭
    override fun onBackPressed() {
        CoroutineScope(Dispatchers.Default).launch {

            val title = editText_title.text.toString()
            val content = editText_content.text.toString()
            val byteArrayList = ArrayList<ByteArray>()

            //byteArray로 첨부된 이미지 용량 압축
            for (i in 0 until photoAdapter.itemCount) {
                ImageUtil.bitmapToByteArray(photoAdapter.getBitmap(i), 10).also {
                    byteArrayList.add(it)
                }
            }

            withContext(Dispatchers.Main) {
                DialogUtil.saveMemoDialog(true, this@AddActivity, layoutInflater, memoViewModel,
                    null, title, content, recordAdapter.records, byteArrayList, textView_date.text.toString(), tag,false)
            }
        }
    }


    //user permission 외부 라이브러리 : TedPermission
    inner class Permission(private val context: Context) {

        private var cameraPermission: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {    //permission 허가 상태라면
                this@AddActivity.takePhoto()
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(this@AddActivity, getString(R.string.deny_take_photo), Toast.LENGTH_LONG).show()
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
                CoroutineScope(Dispatchers.Default).launch {
                    this@AddActivity.openGallery()
                }
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {  //permission 거부 상태라면
                Toast.makeText(this@AddActivity, getString(R.string.deny_gallery), Toast.LENGTH_LONG).show()
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