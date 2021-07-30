package com.cookandroid.sharemo

import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.CursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class WriteTempActivity : AppCompatActivity() {

    lateinit var rv_post : RecyclerView
    lateinit var adapter : RecyclerView.Adapter<PostDataAdapter.CustomViewHolder>
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var arrayList: ArrayList<PostData>

    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_temp)

        rv_post = findViewById(R.id.rv_Post) //아이디 연결
        rv_post.setHasFixedSize(true) //리사이클러뷰 성능 강화
        layoutManager = LinearLayoutManager(this)
        rv_post.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        arrayList = ArrayList<PostData>() //PostData 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

        mDatabaseRef.child("PostData").orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //파이어베이스의 데이터를 가져옴
                arrayList.clear()

                for (data : DataSnapshot in snapshot.getChildren()) {
                    var postData : PostData? = data.getValue(PostData::class.java)

                    arrayList.add(postData!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                    Log.d("태그", "$arrayList")
                }
                adapter.notifyDataSetChanged() //리스트 저장 및 새로고침

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        adapter = PostDataAdapter(arrayList, this)
        rv_post.setAdapter(adapter)


    }


}