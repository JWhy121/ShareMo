package com.cookandroid.sharemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap


/*채팅방 화면 액비티비*/
class ChatRoomActivity : AppCompatActivity() {

    //파이어베이스
    var mFirebaseUser : FirebaseUser? = null
    private lateinit var mDatabaseRef : DatabaseReference

    var chatList = ArrayList<ChatData>()

    //위젯 연결할 변수 선언
    lateinit var chatRecyclerView : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var img_back : ImageView
    lateinit var imgBtn_phonecall : ImageView
    lateinit var imgBtn_sendMsg : ImageView
    lateinit var btn_sendMessage : ImageButton
    lateinit var edt_message : EditText
    lateinit var tv_nickname : TextView

    var chatRoomUid : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        img_back = findViewById(R.id.imgBack)
        imgBtn_phonecall = findViewById(R.id.imgBtn_PhoneCall)
        imgBtn_sendMsg = findViewById(R.id.imgBtn_sendMsg)

        tv_nickname = findViewById(R.id.tv_Nickname)
        btn_sendMessage = findViewById(R.id.btnSendMessage)
        edt_message = findViewById(R.id.etMessage)

        chatRecyclerView.setHasFixedSize(true) //리사이클러뷰 성능 강화
        layoutManager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

        //intent로 데이터 받아오기
        var intent = getIntent()
        var post_uid : String? = intent.getStringExtra("post_uid")
        var post_nickname = intent.getStringExtra("post_nickname")
        var sender_nickname = intent.getStringExtra("sender_nickname")

        lateinit var postUser_phone : String

        //파이어베이스에서 데이터를 가져와서 상대방 전화번호 저장
        mDatabaseRef.child("UserAccount").child("${post_uid!!}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user: User? = snapshot.getValue(User::class.java)

                        postUser_phone = user!!.user_phone.toString()

                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })



        //뒤로가기 버튼을 눌렀을 때
        img_back.setOnClickListener {
            onBackPressed()
        }

        //전화하기 버튼을 눌렀을 때 상대방에게 전화 거는 화면으로 연결
        imgBtn_phonecall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$postUser_phone"))
            startActivity(intent)
        }
        //문자하기 버튼을 눌렀을 때 문자 전송 화면으로 연결
        imgBtn_sendMsg.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "$postUser_phone"))
                startActivity(intent)
        }


        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")


        //intent로 받아온 닉네임을 채팅창 위에 출력
        mDatabaseRef!!.child("UserAccount").child("$post_uid!!").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    tv_nickname.text = post_nickname
                }
            })


        //전송 버튼을 눌렀을 때 edt창이 비어있으면 메시지를 입력하라고 Toast, 텍스트가 있으면 sendMessage함수로 메시지 전송
        btn_sendMessage.setOnClickListener {

            var message: String = edt_message.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "메지시를 입력하세요", Toast.LENGTH_SHORT)
                    .show()
                edt_message.setText("")
            } else {
                sendMessage(mFirebaseUser!!.uid, post_uid!!, message, sender_nickname!!,post_nickname!!)
                edt_message.setText("") //다시 edit 창 초기화

            }
        }
        //메시지를 읽어오는 메소드
        readMessage(mFirebaseUser!!.uid, post_uid!!)
    }

    //메시지 보내기
    private fun sendMessage(
        senderUid: String,
        receiverUid: String,
        message: String,
        sender_nickname: String,
        receiver_nickname: String
    ){
        var reference : DatabaseReference? = FirebaseDatabase.getInstance().getReference("ShareMo")

        //받아온 값들을 hashMap을 통해 ChatRoom/users에 저장
        var hashMap : HashMap<String, String> = HashMap()
        hashMap.put("senderUid", senderUid)
        hashMap.put("receiverUid", receiverUid)
        hashMap.put("message", message)
        hashMap.put("senderNickname", sender_nickname)
        hashMap.put("receiverNickname", receiver_nickname)

        reference!!.child("ChatRooms").child("users").push().setValue(hashMap)
    }
    //기존 메시지 있으면 읽어오기
    fun readMessage(senderUid: String, receiverUid: String){
        val databaseReference : DatabaseReference =
            FirebaseDatabase.getInstance().getReference("ShareMo").child("ChatRooms")
                .child("users")

        databaseReference.child("$chatRoomUid").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            //chatList clear 후 sender와 receiver uid가 같은 곳에 chat 데이터 출력(리싸이클러뷰)
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(ChatData::class.java)

                    if (chat!!.senderUid.equals(senderUid) && chat!!.receiverUid.equals(receiverUid) ||
                        chat!!.senderUid.equals(receiverUid) && chat!!.receiverUid.equals(
                            senderUid
                        )
                    ) {
                        chatList.add(chat)
                    }
                }

                val chapAdapter = ChatAdapter(this@ChatRoomActivity, chatList)

                chatRecyclerView.adapter = chapAdapter
            }
        })
    }
}