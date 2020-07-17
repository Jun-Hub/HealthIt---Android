package io.jun.healthit.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleveroad.fanlayoutmanager.FanLayoutManager
import io.jun.healthit.R
import io.jun.healthit.model.Tip
import io.jun.healthit.view.RoutineActivity

class TipListAdapter(val context: Context, private val fanLayoutManager: FanLayoutManager):
    RecyclerView.Adapter<TipListAdapter.TipViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val tips: MutableList<Tip> = ArrayList()
    private lateinit var onItemClickListener: OnItemClickListener

    inner class TipViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tipName: TextView = itemView.findViewById(R.id.textView_name)
        val tipRecommend: TextView = itemView.findViewById(R.id.textView_recommend)
        var tipImage: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val itemView = inflater.inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val current = tips[position]

        holder.tipName.text = current.name
        holder.tipRecommend.text = current.recommend
        holder.tipImage.setImageResource(current.imageRes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.tipImage.transitionName = "shared$position"
        }

        holder.itemView.setOnClickListener {

            //이미 한 번 터치되서 카드가 올라왔을 때 또 터치하면 액티비티 전환
            if(fanLayoutManager.selectedItemPosition == position) {
                val intent = Intent(this.context, RoutineActivity::class.java)
                intent.putExtra("tipType", current.intentValue)
                context.startActivity(intent)
            } else {    //한 번만 터치하면 카드 올라오는 이펙트
                onItemClickListener.onItemClicked(holder.adapterPosition, holder.tipImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return tips.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(pos: Int, view: View?)
    }

    fun add(item: Tip) {
        this.tips.add(item)
        notifyDataSetChanged()
    }
}