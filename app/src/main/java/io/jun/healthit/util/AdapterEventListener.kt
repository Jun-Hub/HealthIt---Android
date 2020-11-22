package io.jun.healthit.util

import androidx.recyclerview.widget.RecyclerView

interface AdapterEventListener {

    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
}