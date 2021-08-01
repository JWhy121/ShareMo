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



class ChatUserAdapter(private val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<ChatUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.nickname.text = user.user_nickname

        holder.layoutUser.setOnClickListener {
            val intent = Intent(context,ChatRoomActivity::class.java)
            intent.putExtra("user_uid",user.user_uid)
            intent.putExtra("user_nickname",user.user_nickname)
            context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nickname = view.findViewById<TextView>(R.id.tv_ChatListUser)
        val layoutUser = view.findViewById<LinearLayout>(R.id.LayoutUser)
    }
}