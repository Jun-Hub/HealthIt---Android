package io.jun.healthit.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import io.jun.healthit.R
import io.jun.healthit.model.data.ParentRoutine
import io.jun.healthit.model.data.Routine

class ExpListAdapter (data: List<MultiItemEntity>) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data) {

    init {
        addItemType(TYPE_PARENT, R.layout.item_parent_routine)
        addItemType(TYPE_ROUTINE, R.layout.item_routine)
    }

    override fun convert(holder: BaseViewHolder, item: MultiItemEntity) {
        when (holder.itemViewType) {
            TYPE_PARENT -> {
                val parent = item as ParentRoutine
                holder.setText(R.id.textView_title, parent.title)
                    .setImageResource(R.id.imageView_arrow,
                                    if (parent.isExpanded) R.drawable.ic_arrow_drop_up_24dp
                                    else R.drawable.ic_arrow_drop_down_24dp)

                holder.itemView.setOnClickListener {
                    val pos = holder.adapterPosition
                    if (parent.isExpanded)
                        collapse(pos)
                    else
                        expand(pos)
                }
            }

            TYPE_ROUTINE -> {
                val routine = item as Routine
                holder.setText(R.id.textView_content, routine.content)
                    .setText(R.id.textView_notice, routine.notice)
                    .setText(R.id.textView_link, routine.link)
                    .getView<TextView>(R.id.textView_link).visibility = if(routine.link==null) View.GONE else View.VISIBLE
                holder.getView<TextView>(R.id.textView_it_link).visibility = if(routine.link==null) View.GONE else View.VISIBLE
                holder.getView<TextView>(R.id.textView_notice).visibility = if(routine.notice==null) View.GONE else View.VISIBLE
            }
        }
    }

    companion object {
        private val TAG = ExpListAdapter::class.java.simpleName

        const val TYPE_PARENT = 0
        const val TYPE_ROUTINE = 1
    }
}