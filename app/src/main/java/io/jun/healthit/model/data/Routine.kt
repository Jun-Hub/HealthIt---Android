package io.jun.healthit.model.data

import com.chad.library.adapter.base.entity.MultiItemEntity
import io.jun.healthit.adapter.ExpListAdapter

data class Routine (val content: String,
                    val notice: String?,
                    val link: String?) : MultiItemEntity {
    override fun getItemType(): Int {
        return ExpListAdapter.TYPE_ROUTINE
    }
}