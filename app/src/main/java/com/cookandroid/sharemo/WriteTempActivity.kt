package com.cookandroid.sharemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_write_glocery.*

class WriteTempActivity : AppCompatActivity() {

    lateinit var rv_post : RecyclerView
    lateinit var adapter : RecyclerView.Adapter<PostDataAdapter.CustomViewHolder>
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var arrayList: ArrayList<PostData>

    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference

    lateinit var imgBtn_eidt : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_glocery)

        rv_post = findViewById(R.id.rv_Post) //아이디 연결
        imgBtn_eidt = findViewById(R.id.imgBtn_Edit)


        rv_post.setHasFixedSize(true) //리사이클러뷰 성능 강화
        layoutManager = LinearLayoutManager(this)
        rv_post.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        arrayList = ArrayList<PostData>() //PostData 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

        mDatabaseRef.child("PostData").orderByKey().addValueEventListener(object : ValueEventListener {
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


        //글쓰기 버튼에 클릭 리스너 연결
        imgBtn_Edit.setOnClickListener {
            var intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }


    }


}