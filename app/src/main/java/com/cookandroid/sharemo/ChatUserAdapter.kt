package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference


class ChatUserAdapter() :
    RecyclerView.Adapter<ChatUserAdapter.ViewHolder>() {

    private lateinit var chatUserList : ArrayList<ChatData>
    private lateinit var context: Context

    constructor(chatUserList : ArrayList<ChatData>, context: Context) : this(){
        this.chatUserList = chatUserList
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(chatUserList != null){
            return chatUserList.size
        }else{
            return 0
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nickname.text = chatUserList.get(position).receiverNickname
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nickname = view.findViewById<TextView>(R.id.tv_ChatListUser)
    }
}