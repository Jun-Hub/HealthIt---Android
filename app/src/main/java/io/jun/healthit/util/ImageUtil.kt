package io.jun.healthit.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.LinearLayoutManager
import io.jun.healthit.adapter.PhotoListAdapter
import java.io.*
import java.net.MalformedURLException
import java.net.URL


class ImageUtil {

    companion object {

        //비트맵 처리과정 중 OutofMemory Exception을 방지해줄 옵션
        //심한 화질 저하로 현재 사용 안함
        fun decodingOption(): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            //options.inSampleSize = 4    //4개의 픽셀을 1개의 픽셀로 간주
            return options
        }

    //불러올 때 회전된 비트맵 본 각도로 반환
    fun exifOrientationToDegrees(exifOrientation: Int, bitmap: Bitmap): Bitmap =
        when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90.0f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180.0f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270.0f)
            else -> bitmap
        }

    //비트맵 회전시키기
    private fun rotate(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        //OutofMemory Exception 방지를 위해 비트맵을 리사이즈 한 후
        val resizedBitmap = resizeBitmap(source)
        //회전시키기
        return Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.width, resizedBitmap.height, matrix, false)
    }

        private fun resizeBitmap(source: Bitmap): Bitmap {
            val maxResolution = 1000    //이미지 리사이즈 한도
            val width = source.width
            val height = source.height
            var newWidth = width
            var newHeight = height
            val rate: Float

            if (width > height) {
                if (maxResolution < width) {
                    rate = maxResolution / width.toFloat()
                    newHeight = (height * rate).toInt()
                    newWidth = maxResolution
                }
            } else {
                if (maxResolution < height) {
                    rate = maxResolution / height.toFloat()
                    newWidth = (width * rate).toInt()
                    newHeight = maxResolution
                }
            }
            return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
        }

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
    fun bitmapToByteArray(bitmap: Bitmap?, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, stream)
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
}

    //외부 url로 이미지 가져오기
    class GetImageFromURL(
        private val layoutM: LinearLayoutManager,
        private val adapter: PhotoListAdapter,
        private val activity: Activity
    ) : AsyncTask<String, Void, Bitmap?>() {

        @Throws(
            java.lang.Exception::class,
            MalformedURLException::class,
            FileNotFoundException::class,
            IOException::class
        )
        override fun doInBackground(vararg imgURL: String): Bitmap? {

            var imageBitmap: Bitmap? = null
            try {
                imageBitmap =
                    BitmapFactory.decodeStream(URL(imgURL[0]).openConnection().getInputStream())
            } catch (e: MalformedURLException) {
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        "'http:' 또는 'https:'로 시작하는 URL 경로를 입력해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: FileNotFoundException) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "해당 경로의 이미지 파일이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "잘못된 URL 경로입니다.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "잘못된 URL 경로입니다.", Toast.LENGTH_LONG).show()
                }
            }
            return imageBitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                adapter.addPhoto(result)
                layoutM.scrollToPosition(adapter.itemCount - 1)
            } else {
                Toast.makeText(activity, "해당 경로의 이미지 파일이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
            }
        }

    }

}

