package io.jun.healthit.adapter

import androidx.recyclerview.widget.RecyclerView

interface AdapterEventListener {

    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
}