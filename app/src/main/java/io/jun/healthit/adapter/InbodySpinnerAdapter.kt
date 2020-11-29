package io.jun.healthit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import io.jun.healthit.R

class InbodySpinnerAdapter(context: Context, val list: List<String>)
    : ArrayAdapter<String>(context, 0, list) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return getView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var mConvertView = convertView
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(context).inflate(
                R.layout.item_tag_spinner, parent, false)
        }

        val imageView: ImageView = mConvertView!!.findViewById(R.id.imageView_color)
        val textViewName: TextView = mConvertView.findViewById(R.id.textView_name)

        adjustView(imageView, textViewName)

        getItem(position)?.let {
            textViewName.text = it
        }

        return mConvertView
    }

    private fun adjustView(imageView: ImageView, textView: TextView) {
        imageView.visibility = View.GONE
        RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).run {
            setMargins(25, 20, 0, 20)
            textView.layoutParams = this
        }
    }

}