package io.jun.healthit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
            for (i in 0 until 3)
                addRecordDefault()
        }
    }

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        holder.recordName.text = current.name
        holder.recordWeight.text = current.weight.toString()
        holder.recordSet.text = current.set.toString()
        holder.recordReps.text = current.reps.toString()

        //편집 가능한 모드면 삭제 버튼 셋팅
        if(canBeEdited && position > 0) {
            holder.deleteBtn.visibility = View.VISIBLE
            holder.deleteBtn.setOnClickListener {
                records.remove(current)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            }
        } else
            holder.deleteBtn.visibility = View.GONE

        if(canBeEdited) {
            //운동 기록 클릭해서 수정하기
            holder.itemView.setOnClickListener {
                if(current.name != null && current.reps != null && current.set != null && current.weight != null && position != null)
                    DialogUtil.editRecordDialog(this, context, inflater, position, current)
            }
        }
    }

    override fun getItemCount() = records.size

    fun addRecordDefault() {
        if(itemCount < Setting.MAX_RECORD) {
            this.records.add(Record(nameGuide, 40, 5, 10))
            notifyItemInserted(itemCount)
        } else
            Toast.makeText(context, String.format(context.resources.getString(R.string.notice_max_record),
                                                    Setting.MAX_RECORD), Toast.LENGTH_SHORT).show()
    }

    fun addRecord(record: Record) {
        this.records.add(record)
        notifyDataSetChanged()
    }

    fun editRecord(index: Int, record: Record) {
        this.records[index] = record
        notifyItemChanged(index)
    }

    fun clearRecords() {
        this.records.clear()
        notifyDataSetChanged()
    }
}