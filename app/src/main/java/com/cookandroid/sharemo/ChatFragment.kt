package com.cookandroid.sharemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

/*채팅 목록 화면 프래그먼트*/
class ChatFragment : Fragment() {

    //파이어베이스
    lateinit var mDatabaseRef : DatabaseReference
    lateinit var mDatabase : FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth

    //위젯 연결 변수 선언
    lateinit var chatUserAdapter : RecyclerView.Adapter<ChatUserAdapter.ViewHolder>
    lateinit var chatLayoutManager : RecyclerView.LayoutManager

    //채팅 목록 리스트와 구분해서 보여줄 리스트 선언
    lateinit var chatUserList : ArrayList<ChatData>
    lateinit var list : ArrayList<String>



    companion object {
        const val TAG : String = "로그"

        fun newInstance() : ChatFragment {
            return ChatFragment()
        }

    }

    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChatFragment - onCreate() called")
    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ChatFragment - onAttach() called")
    }

    // 뷰가 생성되었을 때
    // 프레그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {

        Log.d(TAG, "ChatFragment - onCreateView() called")

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        var chatRecyclerView : RecyclerView = view.findViewById(R.id.rv_Post)

        //리싸이클러뷰에 어댑터 연결
        var context : Context = view.context
        chatRecyclerView = view as RecyclerView
        chatRecyclerView.setHasFixedSize(true)
        chatLayoutManager = LinearLayoutManager(context)
        chatRecyclerView.setLayoutManager(chatLayoutManager)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabase.getReference("ShareMo")
        firebaseAuth = FirebaseAuth.getInstance()
        val mFirebaseUser : FirebaseUser? = firebaseAuth?.currentUser
        var user : String = mFirebaseUser!!.uid

        chatUserList = ArrayList<ChatData>()
        list = ArrayList<String>()

        /*파이어베이스 ChatRooms에서 유저 receiverUid가 현재 사용자인 부분을 가져와서 senderUid를 중복 제거해서
        출력되도록 함 (저장되어 있는 데이터에서 receiverUid가 현재 사용자여야 내 채팅 화면에 상대방 uid(senderUid)가 보이게 됨)*/
        mDatabaseRef.child("ChatRooms").child("users").orderByChild("receiverUid").equalTo("${mFirebaseUser.uid}")
                .addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(datasnapshot : DataSnapshot in snapshot.getChildren()){
                    var userList : ChatData? = datasnapshot.getValue(ChatData::class.java)
                    chatUserList.add(userList!!)
                    list.add(userList!!.senderNickname!!)
                    list.toSet().toList()
                }
                chatUserAdapter.notifyDataSetChanged()
            }
        })

        //리싸이클러뷰에 어댑터 연결
        chatUserAdapter = ChatUserAdapter(chatUserList, context, list)
        chatRecyclerView.setAdapter(chatUserAdapter)

        return view
    }

}