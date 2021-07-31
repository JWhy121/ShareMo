package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class PostDataAdapter() : RecyclerView.Adapter<PostDataAdapter.CustomViewHolder>(){

    private lateinit var postDataList : ArrayList<PostData>
    private lateinit var context : Context

    constructor(postDataList: ArrayList<PostData>, context: Context) : this() {
        this.postDataList = postDataList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDataAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos : Int = adapterPosition
                val postData : PostData = postDataList.get(curPos)
                if(curPos != RecyclerView.NO_POSITION){
                    var intent = Intent(context, PostGloceryActivity::class.java).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("UID", postData.uid)
                    intent.putExtra("CONTENT", postData.content)
                    intent.putExtra("DONG", postData.dong)
                    intent.putExtra("NICKNAME", postData.nickname)
                    intent.putExtra("PRICE", postData.price)
                    intent.putExtra("WEBSITE", postData.website)
                    context.startActivity(intent)
                }
            }
        }
    }
    override fun onBindViewHolder(holder: PostDataAdapter.CustomViewHolder, position: Int) {

/*        Glide.with(holder.itemView)
                .load(postDataList.get(position).image)
                .into(holder.image)*/
        holder.content.text = postDataList.get(position).content
        holder.dong.text = postDataList.get(position).dong
        holder.price.text = postDataList.get(position).price
        holder.nickname.text = postDataList.get(position).nickname
    }

    override fun getItemCount(): Int {
        if(postDataList != null){
            return postDataList.size
        }else
        {
            return 0
        }
    }

    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        //val image = itemView.findViewById<ImageView>(R.id.iv_Image)
        val content = itemView.findViewById<TextView>(R.id.tv_Content)
        val dong = itemView.findViewById<TextView>(R.id.tv_Dong)
        val nickname = itemView.findViewById<TextView>(R.id.tv_Nickname)
        val price = itemView.findViewById<TextView>(R.id.tv_Price)
    }





}