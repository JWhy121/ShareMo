package com.cookandroid.sharemo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.config.GservicesValue.value
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

        btn_close = findViewById(R.id.btn_close)
        btn_upload = findViewById(R.id.btn_upload)

        iv_contentImage = findViewById(R.id.iv_contentImage)

        edt_content = findViewById(R.id.edt_content)
        edt_price = findViewById(R.id.edt_price)
        edt_website = findViewById(R.id.edt_website)

        btn_close.setOnClickListener {
            onBackPressed()
        }

        btn_upload.setOnClickListener {
            var str_uid : String = mFirebaseAuth!!.currentUser!!.uid
            var str_content : String = edt_content.text.toString()
            var str_price : Int = edt_price.text.toString().toInt()
            var str_website : String = edt_website.text.toString()

            val Post : PostData = PostData(R.drawable.user, str_uid, "닉네임", str_content, str_price, "공릉동", str_website)

            //mDatabaseRef.child("Posts").setValue(Post)

            mDatabaseRef.ref.child("PostData").push().setValue(Post);

            Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
            finish()
        }




    }
}