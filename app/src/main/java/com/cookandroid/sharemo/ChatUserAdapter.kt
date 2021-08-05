package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*채팅 목록 화면 리싸이클러뷰에 연결하는 adapter*/
class ChatUserAdapter() :
    RecyclerView.Adapter<ChatUserAdapter.ViewHolder>() {

    private lateinit var chatUserList : ArrayList<ChatData>
    private lateinit var context: Context
    private lateinit var list : ArrayList<String>

    //생성자
    constructor(chatUserList: ArrayList<ChatData>, context: Context, list: ArrayList<String>) : this(){
        this.chatUserList = chatUserList
        this.context = context
        this.list = list
        for(a in chatUserList){
            list.add(a.senderNickname!!)
        }
        list.toSet().toList()
    }

    /*채팅 목록에서 아이템 뷰를 클릭하면 해당 채팅방으로 연결
    chatUserList를 불러와서 senderNickname이 같으면 하나로 묶어 중복을 제거
    이후 intent로 내 uid, 상대방 uid, 상대방 닉네임을 전달(ChatRoomActivity에서 값을 읽어서 채팅 방 화면 띄워줌*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos : Int = adapterPosition

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

    //중복을 제거한 리스트 사이즈 반환
    override fun getItemCount(): Int {
        if(list != null){
            Log.d("태그","${list.toSet().toList()}")
            return list.toSet().toList().size
        }else{
            return 0
        }

    }

    //닉네임 출력
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nickname.text = list.toSet().toList().get(position)
    }

    //위젯 id 연결
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nickname = view.findViewById<TextView>(R.id.tv_ChatListUser)

    }
}