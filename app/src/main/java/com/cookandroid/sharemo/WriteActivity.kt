package com.cookandroid.sharemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.log

class WriteActivity : AppCompatActivity() {

    lateinit var btn_close : Button
    lateinit var btn_upload : Button

    lateinit var iv_contentImage : ImageView

    lateinit var edt_content : EditText
    lateinit var edt_price : EditText
    lateinit var edt_website : EditText

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

        btn_close = findViewById(R.id.btn_Close)
        btn_upload = findViewById(R.id.btn_Upload)

        iv_contentImage = findViewById(R.id.iv_ContentImage)

        edt_content = findViewById(R.id.edt_Content)
        edt_price = findViewById(R.id.edt_Price)
        edt_website = findViewById(R.id.edt_Website)

        lateinit var nickname : String

        btn_close.setOnClickListener {
            onBackPressed()
        }

        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth!!.currentUser!!.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //파이어베이스의 데이터를 가져옴
                var user : User? = snapshot.getValue(User::class.java)
                Log.d("택","${user!!.user_mail.toString()}")

                nickname = user!!.user_nickname.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Tag","안됨")
            }
        })


        btn_upload.setOnClickListener {
            var str_content : String = edt_content.text.toString()
            var str_price = edt_price.text.toString()
            var str_website : String = edt_website.text.toString()

            val Post = PostData(mFirebaseAuth!!.currentUser!!.uid,str_content, nickname, str_price, "공릉동",str_website)

            //mDatabaseRef.child("Posts").setValue(Post)

            mDatabaseRef.ref.child("PostData").push().setValue(Post);

            Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, WriteTempActivity::class.java)
            startActivity(intent)
            finish()
        }




    }
}