package com.cookandroid.sharemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class PostDataAdapter(val postDataList : ArrayList<PostData>) : RecyclerView.Adapter<PostDataAdapter.CustomViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDataAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos : Int = adapterPosition
                val postData : PostData = postDataList.get(curPos)
                Toast.makeText(parent.context, "이름 : ${postData.content} 동네 : ${postData.dong}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onBindViewHolder(holder: PostDataAdapter.CustomViewHolder, position: Int) {
        holder.image.setImageResource(postDataList.get(position).image)
        holder.content.text = postDataList.get(position).content
        holder.dong.text = postDataList.get(position).dong
        holder.price.text = postDataList.get(position).price.toString()
        holder.nickname.text = postDataList.get(position).nickname
    }

    override fun getItemCount(): Int {
        return postDataList.size
    }

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val image = itemView.findViewById<ImageView>(R.id.iv_Image)
        val content = itemView.findViewById<TextView>(R.id.tv_Content)
        val dong = itemView.findViewById<TextView>(R.id.tv_Dong)
        val nickname = itemView.findViewById<TextView>(R.id.tv_Nickname)
        val price = itemView.findViewById<TextView>(R.id.tv_Price)
    }


}