package io.jun.healthit.model.data

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import io.jun.healthit.adapter.ExpListAdapter

data class ParentRoutine(var title: String) : AbstractExpandableItem<Routine>(), MultiItemEntity {

    override fun getItemType(): Int {
        return ExpListAdapter.TYPE_PARENT
    }

    override fun getLevel(): Int {
        return 0
    }
}