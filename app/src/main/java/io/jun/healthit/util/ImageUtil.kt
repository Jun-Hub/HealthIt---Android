package io.jun.healthit.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.*

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
}

}

