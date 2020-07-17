package io.jun.healthit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.jun.healthit.R
import io.jun.healthit.model.Record
import io.jun.healthit.util.DialogUtil
import io.jun.healthit.util.Setting

class RecordListAdapter(val context: Context, private val canBeEdited: Boolean, onlyForAddNew: Boolean)
    : RecyclerView.Adapter<RecordListAdapter.RecordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var records: ArrayList<Record> = arrayListOf()
    private val nameGuide = context.resources.getString(R.string.name_guide)
    private val payloadDelete = "PAYLOAD_DELETE"

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
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = inflater.inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(itemView)
    }

    // Update ALL VIEW holder
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val current = records[position]

        //memoDetailActivity에서 현재 운동기록이름이 직전과 같다면 공백으로 표시
        if(!canBeEdited && position > 0 && current.name == records[position-1].name)
            holder.recordName.text = ""
        else
            holder.recordName.text = current.name

        //같은 이름의 운동이면 경계선 없애기
        if(position<itemCount-1 && current.name == records[position+1].name)
            holder.recordLayout.background = context.getDrawable(R.drawable.border_side_record)
        else
            holder.recordLayout.background = context.getDrawable(R.drawable.border_bottom_record)

        holder.recordWeight.text = current.weight.toString()
        holder.recordSet.text = current.set.toString()
        holder.recordReps.text = current.reps.toString()

        //편집 가능한 모드면 삭제 버튼 셋팅
        if(canBeEdited && position > 0) {
            holder.deleteBtn.visibility = View.VISIBLE
            holder.deleteBtn.setOnClickListener {
                records.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            }
        } else {
            holder.deleteBtn.visibility = View.GONE
        }

        if(canBeEdited) {
            //운동 기록 클릭해서 수정하기
            holder.itemView.setOnClickListener {
                DialogUtil.editRecordDialog(this, context, inflater, position, current)
            }
        }
    }

    override fun getItemCount() = records.size

    fun addRecordDefault() {
        if(itemCount < Setting.MAX_RECORD && itemCount == 0) {
            this.records.add(Record(nameGuide, 40, 5, 10))
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
}