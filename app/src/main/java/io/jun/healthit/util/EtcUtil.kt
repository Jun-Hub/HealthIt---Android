package io.jun.healthit.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*

class EtcUtil {

    companion object {

        fun getCurrentDate() = "${SimpleDateFormat("yyyy", Locale.KOREA).format(Date())}/" +
                    "${SimpleDateFormat("MM", Locale.KOREA).format(Date())}/" +
                    SimpleDateFormat("dd", Locale.KOREA).format(Date())

        //내용 전체가 공백인지 체크
        fun isAllBlank(str: String): Boolean {
            var allBlank = true
            for (i in str.indices) {
                if (str[i] != ' ') {
                    allBlank = false
                    break
                }
            }
            return allBlank
        }

        fun showKeyboard(context: Context){

            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun closeKeyboard(context: Context){

            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

    }
}