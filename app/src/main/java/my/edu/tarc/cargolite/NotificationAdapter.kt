package my.edu.tarc.cargolite

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_layout.view.*

class NotificationAdapter(private val notificationList: List<NotificationItem>): RecyclerView.Adapter<NotificationAdapter.notificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notificationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_layout,parent,false)
        return notificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: notificationViewHolder, position: Int) {
        val currentItem = notificationList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView1.text = currentItem.text1
        holder.textView2.text = currentItem.text2

//        if(position==0){
//            holder.textView1.setBackgroundColor(Color.YELLOW)
//        }
    }

    override fun getItemCount() = notificationList.size


    inner class notificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.text_view1
        val textView2: TextView = itemView.text_view2

//        init{
//            itemView.setOnClickListener(this)
//        }
//
//        override fun onClick(v: View?){
//            val position = adapterPosition
//            if(position != RecyclerView.NO_POSITION){
//                listener.onItemClick(position)
//            }
//        }
//    }
//
//    interface OnItemClickListener{
//        fun onItemClick(position: Int)
    }
}