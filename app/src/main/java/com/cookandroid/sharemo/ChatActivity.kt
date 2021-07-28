package com.cookandroid.sharemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //데이버베이스에 메시지 작성하기
        lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("message")

        mDatabaseRef.setValue("하하하하하하")

    }
}