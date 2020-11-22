package io.jun.healthit.util

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import io.jun.healthit.model.data.Record
import java.text.SimpleDateFormat
import java.util.*

object EtcUtil {

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

    fun showKeyboard(context: Context) {

        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun closeKeyboard(context: Context) {

        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    fun stringToDate(str: String): Date? = SimpleDateFormat("yyyy/MM/dd", Locale.KOREA).parse(str)

    fun calculateSetAndVolume(record:List<Record>): Pair<Int, Int> {
        var setEA = 0
        for(i in record.indices) {
            setEA += record[i].set
        }

        var volume = 0
        for(i in record.indices) {
            volume += (record[i].weight.toInt() * record[i].set * record[i].reps)
        }

        return Pair(setEA, volume)
    }

    fun makePlus(editText: EditText, plusValue: Int) : Editable {

        return SpannableStringBuilder(
            if(editText.text.toString() == "") plusValue.toString()
            else (editText.text.toString().toInt()+plusValue).toString()
        )
    }

    fun makePlusFloat(editText: EditText, plusValue: Float) : Editable {

        return SpannableStringBuilder(
            if(editText.text.toString() == "" || editText.text.toString() == ".") plusValue.toString()
            else (editText.text.toString().toFloat()+plusValue).toString()
        )
    }

    fun makeMinus(editText: EditText, minusValue: Int) : Editable {
        return SpannableStringBuilder(
            if(editText.text.toString() == "" || editText.text.toString().toInt() <= minusValue)
                if(minusValue == 1) "1"
                else "0"
            else (editText.text.toString().toInt()-minusValue).toString())
    }

    fun makeMinusFloat(editText: EditText, minusValue: Float) : Editable {
        return SpannableStringBuilder(
            if(editText.text.toString() == "" ||
                editText.text.toString() == "." ||
                editText.text.toString().toFloat() <= minusValue)
                if(minusValue == 1f) "1"
                else "0"
            else (editText.text.toString().toFloat()-minusValue).toString())
    }

}