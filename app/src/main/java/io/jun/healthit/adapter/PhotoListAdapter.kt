package io.jun.healthit.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.jun.healthit.R
import io.jun.healthit.util.ImageUtil
import io.jun.healthit.view.PhotoDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoListAdapter internal constructor(
    val context: Context, private val canBeEdited: Boolean
) : RecyclerView.Adapter<PhotoListAdapter.PhotoViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var bitmaps: ArrayList<Bitmap> = arrayListOf()
    private val payloadDelete = "PAYLOAD_DELETE"

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = inflater.inflate(R.layout.item_photo, parent, false)

        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        val current = bitmaps[position]
        holder.imageView.setImageBitmap(current)

        if(canBeEdited) {   //EditActivity에서 보여질 때(편집가능)
            holder.deleteBtn.setOnClickListener {
                bitmaps.remove(current)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount, payloadDelete)
            }
        } else {    //MemoDetailActivity에서 보여질 때 (편집 불가능)
            holder.deleteBtn.visibility = View.GONE //-> 삭제버튼 숨기기

            //이미지 터치시 사진 상세보기
            holder.imageView.setOnClickListener {
                CoroutineScope(Dispatchers.Default).launch {
                    //비트맵을 byteArray로 압축
                    val byteArray = ImageUtil.bitmapToByteArray(current, 100)
                    context.startActivity(Intent(context, PhotoDetailActivity::class.java).apply {
                        putExtra("byteArray", byteArray)
                    })
                }
            }
        }

    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int, payloads: MutableList<Any>) {
        val current = bitmaps[position]

        if(payloads.isNotEmpty()) {

            if(canBeEdited) {
                holder.deleteBtn.setOnClickListener {
                    bitmaps.remove(current)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount, payloadDelete)
                }
            }

        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    internal fun addPhoto(image: Bitmap) {
        this.bitmaps.add(image)
        notifyItemInserted(itemCount)
    }

    internal fun getBitmap(position: Int) = bitmaps[position]

    override fun getItemCount() = bitmaps.size
}