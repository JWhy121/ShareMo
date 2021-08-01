package com.cookandroid.sharemo

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap


/*채팅방 화면 리사이클러뷰로 출력*/
class ChatRoomActivity : AppCompatActivity() {

    var mFirebaseUser : FirebaseUser? = null
    var mDatabaseRef : DatabaseReference? = null //실시간 데이터베이스
    var chatList = ArrayList<ChatData>()
    var topic = ""

    lateinit var chatRecyclerView : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var img_back : ImageView

    lateinit var btn_sendMessage : ImageButton
    lateinit var edt_message : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        img_back = findViewById(R.id.imgBack)

        lateinit var tv_nickname : TextView

        tv_nickname = findViewById(R.id.tv_Nickname)
        btn_sendMessage = findViewById(R.id.btnSendMessage)
        edt_message = findViewById(R.id.etMessage)

        chatRecyclerView.setHasFixedSize(true) //리사이클러뷰 성능 강화
        layoutManager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        //intent로 데이터 받아오기
        var intent = getIntent()
        var post_uid : String? = intent.getStringExtra("post_uid")
        var post_nickname = intent.getStringExtra("post_nickname")


        //뒤로가기 버튼을 눌렀을 때
        img_back.setOnClickListener {
            onBackPressed()
        }


        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")


        //채팅창 위에 상대방의 닉네임 출력
        mDatabaseRef!!.child("UserAccount").child("$post_uid!!").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                tv_nickname.text = post_nickname
            }
        })


        //전송 버튼을 눌렀을 때 edt창이 비어있으면 메시지를 입력하라고 Toast, 텍스트가 있으면 sendMessage함수로 메시지 전송
        btn_sendMessage.setOnClickListener {
            var message : String = edt_message.text.toString()

            if(message.isEmpty()){
                Toast.makeText(applicationContext, "메지시를 입력하세요", Toast.LENGTH_SHORT).show()
                edt_message.setText("")
            }else{
                sendMessage(mFirebaseUser!!.uid, post_uid!!, message)
                edt_message.setText("") //다시 edit 창 초기화
                /*topic = "/topics/$post_uid"
                PushNotification(NotificationData(post_nickname!!, message)
                )*/
            }
        }

        readMessage(mFirebaseUser!!.uid, post_uid!!)  //메시지를 읽어오는 함수
    }

    //메시지 보내기
    private fun sendMessage(senderUid : String, receiverUid : String, message : String){
        var reference : DatabaseReference? = FirebaseDatabase.getInstance().getReference("ShareMo")

        var hashMap : HashMap<String, String> = HashMap()
        hashMap.put("senderUid", senderUid)
        hashMap.put("receiverUid", receiverUid)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)
    }


    //기존 메시지 있으면 읽어오기
    fun readMessage(senderUid: String, receiverUid: String){
        val databaseReference : DatabaseReference =
            FirebaseDatabase.getInstance().getReference("ShareMo").child("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            //chatList clear 후 sender와 receiver uid가 같은 곳에 chat 데이터 출력
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for(dataSnapShot : DataSnapshot in  snapshot.children){
                    val chat = dataSnapShot.getValue(ChatData::class.java)

                    if(chat!!.senderUid.equals(senderUid) && chat!!.receiverUid.equals(receiverUid) ||
                            chat!!.senderUid.equals(receiverUid) && chat!!.receiverUid.equals(senderUid)
                    ){
                        chatList.add(chat)
                    }
                }

                val chapAdapter = ChatAdapter(this@ChatRoomActivity, chatList)

                chatRecyclerView.adapter = chapAdapter
            }
        })
    }

/*    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }*/
}