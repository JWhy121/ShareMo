package com.cookandroid.sharemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class WriteTempActivity : AppCompatActivity() {

    lateinit var rv_post : RecyclerView
    var postDataList : ArrayList<PostData> = ArrayList<PostData>()

    private lateinit var mDatabaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_temp)

        rv_post = findViewById(R.id.rv_Post)

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

        mDatabaseRef.child("PostData").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("태그", "${snapshot.value}")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        rv_post.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var postAdapter : PostDataAdapter = PostDataAdapter(postDataList)
        rv_post.setHasFixedSize(true)

        rv_post.adapter = postAdapter

    }

}