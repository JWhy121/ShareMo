package com.cookandroid.sharemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스

    lateinit var edt_email : EditText
    lateinit var edt_pwd : EditText
    lateinit var edt_name : EditText
    lateinit var edt_phone : EditText
    lateinit var edt_nickname : EditText

    lateinit var rg_sex : RadioGroup
    lateinit var rb_male : RadioButton
    lateinit var rb_female : RadioButton

    lateinit var btn_register : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()

        edt_email = findViewById(R.id.edt_email)
        edt_pwd = findViewById(R.id.edt_pwd)
        edt_name = findViewById(R.id.edt_name)
        btn_register = findViewById(R.id.btn_register)

        btn_register.setOnClickListener {
            //회원가입 처리 시작
            var str_email : String = edt_email.text.toString()
            var str_pwd : String = edt_pwd.text.toString()
            var str_name : String = edt_name.text.toString()

            //firebaseauth 진행
            mFirebaseAuth!!.createUserWithEmailAndPassword(str_email, str_pwd)?.addOnCompleteListener(this){
                if(it.isSuccessful){
                    val user : FirebaseUser? = mFirebaseAuth?.currentUser
                    Toast.makeText(this, "$str_name 님, 가입을 축하합니다", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this, "생성 실패", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
}