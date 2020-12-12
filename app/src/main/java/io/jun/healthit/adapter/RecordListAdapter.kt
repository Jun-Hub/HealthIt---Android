package io.jun.healthit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.jun.healthit.R
import io.jun.healthit.model.data.Record
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.util.Setting
import java.util.*
import kotlin.collections.ArrayList

class RecordListAdapter(val context: Context, private val canBeEdited: Boolean, onlyForAddNew: Boolean)
    : RecyclerView.Adapter<RecordListAdapter.RecordViewHolder>(),
    ItemTouchHelperAdapter {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var records: ArrayList<Record> = arrayListOf()
    private val nameGuide = context.resources.getString(R.string.name_guide)

    lateinit var onEventListener: AdapterEventListener

    init {
        if(canBeEdited && onlyForAddNew) {
            addRecordDefault()
        }
    }

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recordLayout: RelativeLayout = itemView.findViewById(R.id.item_layout)

        val recordName: TextView = itemView.findViewById(R.id.textView_name)
        val recordWeight: TextView = itemView.findViewById(R.id.textView_weight)
        val recordSet: TextView = itemView.findViewById(R.id.textView_set)
        val recordReps: TextView = itemView.findViewById(R.id.textView_reps)
        val dragBtn: ImageButton = itemView.findViewById(R.id.drag_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = inflater.inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(itemView)
    }

    // Update ALL VIEW holder
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val current = records[position]

        //memoDetailActivity에서 현재 운동기록이름이 직전과 같다면 공백으로 표시
        if(!canBeEdited && position > 0 && current.name == records[position-1].name)
            holder.recordName.text = ""
        else
            holder.recordName.text = current.name

        //같은 이름의 운동이면 경계선 없애기
        if(position<itemCount-1 && current.name == records[position+1].name)
            holder.recordLayout.background = ContextCompat.getDrawable(context, R.drawable.border_side_record)
        else
            holder.recordLayout.background = ContextCompat.getDrawable(context, R.drawable.border_bottom_record)

        holder.recordWeight.text = getWeightString(current.weight.toString())
        holder.recordSet.text = current.set.toString()
        holder.recordReps.text = current.reps.toString()

        if(canBeEdited) {
            //운동 기록 클릭해서 수정하기
            holder.itemView.setOnClickListener {
                DialogUtil.editRecordDialog(this, context, inflater, position, current)
            }
        }

        if(canBeEdited) {
            holder.dragBtn.visibility = View.VISIBLE
            //드래그 버튼 터치하면 아이템 이동
            holder.dragBtn.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN)
                    onEventListener.onDragStarted(holder)
                false
            }
        } else {
            holder.dragBtn.visibility = View.GONE
        }
    }

    override fun getItemCount() = records.size

    //드래그할 때 아이템 이동
    override fun onItemMove(fromPosition: Int, targetPosition: Int) {
        Collections.swap(records, fromPosition, targetPosition)
        notifyItemMoved(fromPosition, targetPosition)

        //notifyItemChanged를 해줘야 수정된 records가 반영됨
        notifyItemChanged(fromPosition)
        notifyItemChanged(targetPosition)
    }

    override fun onItemRemove(position: Int) {
        records.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    //콜백으로 onEventListener를 초기화해줌
    fun setOnAdapterEventListener(adapterEventListener: AdapterEventListener) {
        onEventListener = adapterEventListener
    }

    fun addRecordDefault() {
        if(itemCount < Setting.MAX_RECORD && itemCount == 0) {
            this.records.add(Record(nameGuide, 40f, 5, 10))
            notifyItemInserted(itemCount)
        } else if(itemCount < Setting.MAX_RECORD) {
            this.records.add(records[itemCount-1])
            notifyItemRangeInserted(itemCount-1, itemCount)
        } else {
            Toast.makeText(context, String.format(context.resources.getString(R.string.notice_max_record), Setting.MAX_RECORD), Toast.LENGTH_SHORT).show()
        }
    }

    fun addRecord(record: Record) {
        this.records.add(record)
        notifyDataSetChanged()
    }

    fun editRecord(index: Int, record: Record) {
        this.records[index] = record
        if(index == 0)
            notifyItemRangeChanged(index, index+1)
        else
            notifyItemRangeChanged(index-1, index+1)
    }

    fun clearRecords() {
        this.records.clear()
        notifyDataSetChanged()
    }

    private fun getWeightString(weightStringRaw: String) =
        if(weightStringRaw.contains(".0"))
            weightStringRaw.substringBefore(".")
        else
            weightStringRaw
}