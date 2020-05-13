package io.jun.healthit.view

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.jun.healthit.R
import kotlinx.android.synthetic.main.activity_photo_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        //넘겨받은 byteArray를 비트맵으로 디코딩 후 photoView에 셋팅
        CoroutineScope(Dispatchers.Default).launch {
            val byteArray = intent.getByteArrayExtra("byteArray")
            val bitmap = byteArray?.size?.let { BitmapFactory.decodeByteArray(byteArray, 0, it) }

            withContext(Dispatchers.Main) {
                photoView.setImageBitmap(bitmap)
            }
        }

    }
}
