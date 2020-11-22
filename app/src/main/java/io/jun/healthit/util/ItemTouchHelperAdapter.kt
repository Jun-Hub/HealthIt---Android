package io.jun.healthit.util

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, targetPosition: Int)
    fun onItemRemove(position: Int)
}