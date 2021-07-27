package com.cookandroid.sharemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WriteActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스

    lateinit var btn_close : Button
    lateinit var btn_writeEnd : Button
    lateinit var edt_content : EditText
    lateinit var edt_price : EditText
    lateinit var edt_website : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()

        btn_close = findViewById(R.id.btn_close)
        btn_writeEnd = findViewById(R.id.btn_writeEnd)
        edt_content = findViewById(R.id.edt_content)
        edt_price = findViewById(R.id.edt_price)
        edt_website = findViewById(R.id.edt_website)

        btn_close.setOnClickListener {
            onBackPressed()
        }

        btn_writeEnd.setOnClickListener {
            upload()
        }

    }

    private fun upload() {

        var str_uid : String = mFirebaseAuth!!.currentUser!!.uid
        var str_content : String = edt_content.text.toString()
        var str_price : String = edt_price.text.toString()
        var str_website : String = edt_website.text.toString()


        val mFirebaseUser : FirebaseUser? = mFirebaseAuth?.currentUser

        val Post = PostData(str_uid, str_content, str_price, str_website)

        //mDatabaseRef.child("Posts").setValue(Post)

        mDatabaseRef.ref.child("Posts").push().setValue(Post);

        Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
        finish()

    }
}