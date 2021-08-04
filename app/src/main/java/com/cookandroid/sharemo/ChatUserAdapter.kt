package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChatUserAdapter() :
    RecyclerView.Adapter<ChatUserAdapter.ViewHolder>() {

    private lateinit var chatUserList : ArrayList<ChatData>
    private lateinit var context: Context
    private lateinit var list : ArrayList<String>

    constructor(chatUserList: ArrayList<ChatData>, context: Context, list: ArrayList<String>) : this(){
        this.chatUserList = chatUserList
        this.context = context
        this.list = list
        for(a in chatUserList){
            list.add(a.senderNickname!!)
        }
        list.toSet().toList()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos : Int = adapterPosition
                //val userList : ChatData = chatUserList.get(curPos)

                var my_nickname : String = ""
                var my_uid : String = ""
                var your_nickname : String = ""

                for(a in chatUserList){
                    if(a.senderNickname == list.toSet().toList().get(curPos)){
                        my_nickname = a.senderNickname.toString()
                        my_uid = a.senderUid.toString()
                        your_nickname = a.receiverNickname.toString()

                        break
                    }
                }
                if(curPos != RecyclerView.NO_POSITION){
                    var intent = Intent(context, ChatRoomActivity::class.java).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("post_uid", my_uid)
                    intent.putExtra("post_nickname", my_nickname)
                    intent.putExtra("sender_nickname", your_nickname)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if(list != null){
            Log.d("태그","${list.toSet().toList()}")
            return list.toSet().toList().size
        }else{
            return 0
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nickname.text = list.toSet().toList().get(position)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nickname = view.findViewById<TextView>(R.id.tv_ChatListUser)

    }
}