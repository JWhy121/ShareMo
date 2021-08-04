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

class ChatFragment : Fragment() {

    lateinit var mDatabaseRef : DatabaseReference
    lateinit var mDatabase : FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var chatUserAdapter : RecyclerView.Adapter<ChatUserAdapter.ViewHolder>
    lateinit var chatLayoutManager : RecyclerView.LayoutManager

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
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {

        Log.d(TAG, "ChatFragment - onCreateView() called")

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        var chatRecyclerView : RecyclerView = view.findViewById(R.id.rv_Post)



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

        chatUserAdapter = ChatUserAdapter(chatUserList, context, list)
        chatRecyclerView.setAdapter(chatUserAdapter)


 /*       if(view is RecyclerView){
            var context : Context = view.context
            chatRecyclerView = view
            chatRecyclerView.setHasFixedSize(true)


            Log.d("태그", "1")
            chatLayoutManager = LinearLayoutManager(context)
            Log.d("태그", "2")
            chatRecyclerView.setLayoutManager(chatLayoutManager)
            Log.d("태그", "3")

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")
            Log.d("태그", "${mDatabaseRef.child("ChatRooms").child("users").key.toString()}")

            //val firebaseSearchQuery = mDatabaseRef.child("PostData").child("$selectedItem").orderByChild("content").startAt(searchText).endAt(searchText + "\uf8ff")
            mDatabaseRef.child("ChatRooms").child("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //파이어베이스의 데이터를 가져옴
                    chatUserList.clear()

                    for (data : DataSnapshot in snapshot.children) {
                        var user : ChatData? = data.getValue(ChatData::class.java)

                        chatUserList.add(user!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                        Log.d("태그", "$chatUserList")
                    }
                    chatUserAdapter.notifyDataSetChanged() //리스트 저장 및 새로고침

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            chatUserAdapter = ChatUserAdapter(chatUserList, context)
            chatRecyclerView.setAdapter(chatUserAdapter)
        }*/

        return view
    }

}