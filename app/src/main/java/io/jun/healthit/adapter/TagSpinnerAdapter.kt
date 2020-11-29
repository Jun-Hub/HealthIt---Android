package io.jun.healthit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import io.jun.healthit.R
import io.jun.healthit.model.data.Tag


class TagSpinnerAdapter(context: Context, val list: ArrayList<Tag>)
    : ArrayAdapter<Tag>(context, 0, list) {

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

        val imageViewColor: ImageView = mConvertView!!.findViewById(R.id.imageView_color)
        val textViewName: TextView = mConvertView.findViewById(R.id.textView_name)

        val currentItem: Tag? = getItem(position)

        if (currentItem != null) {
            imageViewColor.setImageResource(currentItem.color)
            textViewName.text = currentItem.name
        }

        return mConvertView
    }

}