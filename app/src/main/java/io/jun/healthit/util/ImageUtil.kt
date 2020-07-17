package io.jun.healthit.util

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.jun.healthit.R
import java.io.*
import java.io.File.separator
import java.text.SimpleDateFormat
import java.util.*


class ImageUtil {

    companion object {

        //촬영사진 썸네일이 아닌 풀사이즈로 가져오기
        @Throws(IOException::class)
        fun createImageFile(context: Context): File {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).also {
                return File.createTempFile(
                    "JPEG_temp", // prefix
                    ".jpg", // suffix
                    it // directory
                )
            }
        }

        //비트맵 byteArray로 압축하기
        fun bitmapToByteArray(bitmap: Bitmap, quality: Int): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            stream.close()

            return stream.toByteArray()
        }

        //이미지 사이즈 가져오기 : 대용량 이미지의 저장 방지
        fun getImageSize(imageUri: Uri, activity: Activity): Int {
            var dataSize = 0
            val scheme = imageUri.scheme
            if (scheme == ContentResolver.SCHEME_CONTENT) {
                try {
                 val fileInputStream: InputStream? =
                     activity.contentResolver.openInputStream(imageUri)
                    if (fileInputStream != null) dataSize = fileInputStream.available()
                } catch (e: java.lang.Exception) {
                   e.printStackTrace()
               }
            }
             return dataSize
        }

        fun getBitmapFromRecyclerView(recyclerView: RecyclerView): Bitmap {
            recyclerView.measure(
                View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

            val bitmap = Bitmap.createBitmap(
                recyclerView.width,
                recyclerView.measuredHeight,
                Bitmap.Config.ARGB_8888)
            recyclerView.draw(Canvas(bitmap))

            return bitmap
        }

        fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                val values = contentValues().apply {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
                    put(MediaStore.Images.Media.IS_PENDING, true)
                }
                // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    context.contentResolver.update(uri, values, null, null)
                }
            } else {
                val directory = File(Environment.getExternalStorageDirectory().toString() + separator + folderName)
                // getExternalStorageDirectory is deprecated in API 29

                if (!directory.exists()) directory.mkdirs()

                val fileName = System.currentTimeMillis().toString() + ".jpg"
                val file = File(directory, fileName)
                saveImageToStream(bitmap, FileOutputStream(file))
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }

        private fun contentValues() : ContentValues {
            val values = ContentValues()
            values.apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            }
            return values
        }

        private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
            if (outputStream != null) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}

