package io.jun.healthit.adapter

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, targetPosition: Int)
    fun onItemRemove(position: Int)
}