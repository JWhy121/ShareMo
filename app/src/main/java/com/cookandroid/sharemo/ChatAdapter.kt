package com.cookandroid.sharemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/*채팅 창 화면 리싸이클러뷰에 연결하는 adapter*/
class ChatAdapter(private val context: Context, private val chatList: ArrayList<ChatData>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null

    //MESSAGE_TYPE을 구분해서 나일 경우 item_right를, 상대방일 경우 item_left를 뷰에 연결
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == MESSAGE_TYPE_RIGHT){
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right,parent, false)
            return ViewHolder(view)
        }else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }
    }

    //chatList 사이즈 반환
    override fun getItemCount(): Int {
        return chatList.size
    }

    //텍스트뷰에 메시지 출력
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtUserName.text = chat.message
    }

    //위젯 id 연결
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName = view.findViewById<TextView>(R.id.tvMessage)
    }

    //나와 상대방의 uid를 구분해서 MESSAGE_TYPE을 0 혹은 1로 설정
    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(chatList[position].senderUid == firebaseUser!!.uid){
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }
}
