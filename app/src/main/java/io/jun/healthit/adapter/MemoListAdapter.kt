package io.jun.healthit.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.jun.healthit.R
import io.jun.healthit.model.data.Memo
import io.jun.healthit.view.MemoDetailActivity
import io.jun.healthit.view.fragment.MemoFragment
import io.jun.healthit.viewmodel.MemoViewModel
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import io.jun.healthit.util.calculateSetAndVolume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoListAdapter internal constructor(
    private val fragment: MemoFragment
) : RecyclerView.Adapter<MemoListAdapter.MemoViewHolder>(), RecyclerViewFastScroller.OnPopupTextUpdate {

    private val inflater: LayoutInflater = LayoutInflater.from(fragment.context)
    private val memoViewModel = ViewModelProvider(fragment).get(MemoViewModel::class.java)
    private var memos = emptyList<Memo>()

    private var originSortMemos = emptyList<Memo>()
    //태그별 해당 메모만 모아놓은 메모들
    private var tag0Memos = ArrayList<Memo>()
    private var tag1Memos = ArrayList<Memo>()
    private var tag2Memos = ArrayList<Memo>()
    private var tag3Memos = ArrayList<Memo>()
    private var tag4Memos = ArrayList<Memo>()
    private var tag5Memos = ArrayList<Memo>()
    private var tag6Memos = ArrayList<Memo>()

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoTitle: TextView = itemView.findViewById(R.id.textView_title)
        val memoContent: TextView = itemView.findViewById(R.id.textView_content)
        val memoRecord: TextView = itemView.findViewById(R.id.textView_record)
        val memoDate: TextView = itemView.findViewById(R.id.textView_date)
        val memoImage: ImageView = itemView.findViewById(R.id.imageView)
        val memoTag: ImageView = itemView.findViewById(R.id.imageView_tag)
        val memoPin: ImageView = itemView.findViewById(R.id.imageView_pin)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val itemView = inflater.inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val current = memos[position]
        
            //제목과 내용이 공백일 시 뷰 없앰
            holder.memoTitle.apply {
                if (current.title == "") visibility = View.GONE
                else {
                    visibility = View.VISIBLE
                    text = current.title
                }
            }

            holder.memoContent.apply {
                if (current.content == "") visibility = View.GONE
                else {
                    visibility = View.VISIBLE
                    text = current.content
                }
            }

        val setAndVolume = current.record?.let { calculateSetAndVolume(it) }
        holder.memoRecord.text = String.format(fragment.getString(R.string.memo_record), setAndVolume?.first, setAndVolume?.second)

            holder.memoDate.text = current.date

            holder.memoTag.setImageResource(
                when (current.tag) {
                    1 -> R.drawable.ic_circle_red
                    2 -> R.drawable.ic_circle_orange
                    3 -> R.drawable.ic_circle_yellow
                    4 -> R.drawable.ic_circle_green
                    5 -> R.drawable.ic_circle_blue
                    6 -> R.drawable.ic_circle_purple
                    else -> R.drawable.transparent
                }
            )

            if (current.photo!!.isNotEmpty()) {
                CoroutineScope(Dispatchers.Default).launch {

                    Glide.with(fragment)
                        .asBitmap()
                        .load(current.photo[0])
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                holder.memoImage.visibility = View.VISIBLE
                                holder.memoImage.setImageBitmap(resource)
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })

                }
            } else
                holder.memoImage.visibility = View.GONE

        //핀 on / off에 따라 pin이미지 visibility
        holder.memoPin.visibility = if(current.pin!!) View.VISIBLE
                                    else View.GONE

        //TODO viewholder에 observe하면 memory lick을 만날 수 있다?
            //MainActivity 툴바의 편집 버튼(switch) 상태를 Livedata로 관찰
            memoViewModel.isEditMode().observe(fragment, Observer { editOn ->
                if (editOn)
                    holder.deleteBtn.visibility = View.VISIBLE
                else
                    holder.deleteBtn.visibility = View.GONE
            })

            holder.deleteBtn.setOnClickListener {
                memoViewModel.delete(current)
            }

            //메모 클릭시 해당 메모 상세보기로 넘어가기
            holder.itemView.setOnClickListener {
                val pinStatus = current.pin //val 값으로 셋팅안해주면 원래 형태가 var이라 intent에 넣지 못함
                fragment.startActivity(Intent(fragment.context, MemoDetailActivity::class.java).apply {
                    putExtra("id", current.id)
                    putExtra("pin", pinStatus)    //pin 상태도 같이 넘겨야 다음 액티비티에서 초기화 null 에러가 안남
                })
            }
        
    }

    internal fun setMemos(memos: List<Memo>) {
        this.memos = memos
        notifyDataSetChanged()

        originSortMemos = memos

        //핀메모로 설정할 때 이 function이 한 번 더 실행되기 때문에 메모리스트들 초기화
        clearTagMemos()

        //메모들의 태그별로 따로 리스트 만들기
        for (j in memos.indices) {
            when(memos[j].tag) {
                0 -> tag0Memos.add(memos[j])
                1 -> tag1Memos.add(memos[j])
                2 -> tag2Memos.add(memos[j])
                3 -> tag3Memos.add(memos[j])
                4 -> tag4Memos.add(memos[j])
                5 -> tag5Memos.add(memos[j])
                6 -> tag6Memos.add(memos[j])
            }
        }
    }

    private fun clearTagMemos() {
        tag0Memos.clear()
        tag1Memos.clear()
        tag2Memos.clear()
        tag3Memos.clear()
        tag4Memos.clear()
        tag5Memos.clear()
        tag6Memos.clear()
    }

    override fun getItemCount() = memos.size

    //fast Scroll 라이브러리 : 스크롤바 터치시 보여줄 아이템 정보
    override fun onChange(position: Int): String {

        return memos[position].date!!
    }

    //메모 태그에 따라 정렬
    fun changeMemoByTag(i:Int) {
        when(i) {
            0 -> this.memos = originSortMemos
            1 -> this.memos = tag0Memos
            2 -> this.memos = tag1Memos
            3 -> this.memos = tag2Memos
            4 -> this.memos = tag3Memos
            5 -> this.memos = tag4Memos
            6 -> this.memos = tag5Memos
            7 -> this.memos = tag6Memos
        }
        notifyDataSetChanged()
    }

}