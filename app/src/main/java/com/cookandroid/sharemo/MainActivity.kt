package com.cookandroid.sharemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스
    lateinit var edtEmail : EditText
    lateinit var edtPwd : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()

        edtEmail = findViewById(R.id.edt_Email)
        edtPwd = findViewById(R.id.edt_Pwd)

        var btnLogin : Button = findViewById(R.id.btn_Login)

        btnLogin.setOnClickListener {

            if(edtEmail.text.toString().equals("") || edtPwd.text.toString().equals("")) {
                Toast.makeText(this, "로그인 정보를 모두 입력하세요", Toast.LENGTH_SHORT).show()
            }else{
                mFirebaseAuth!!.signInWithEmailAndPassword(edtEmail.text.toString(), edtPwd.text.toString())!!.addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        var intent = Intent(this, BottomNavActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "아이디 혹은 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        var btnRegister : Button = findViewById(R.id.btn_Register)
        btnRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}