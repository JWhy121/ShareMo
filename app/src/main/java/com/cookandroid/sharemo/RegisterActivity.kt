package com.cookandroid.sharemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스

    lateinit var edt_email : EditText
    lateinit var edt_pwd : EditText
    lateinit var edt_name : EditText
    lateinit var edt_phone : EditText
    lateinit var edt_nickname : EditText

    lateinit var btn_register : Button
    lateinit var btn_back : Button
    lateinit var btn_confirm : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

        edt_email = findViewById(R.id.edt_Email)
        edt_pwd = findViewById(R.id.edt_Pwd)
        edt_name = findViewById(R.id.edt_Name)
        edt_phone = findViewById(R.id.edt_Phone)
        edt_nickname = findViewById(R.id.edt_Nickname)

        btn_register = findViewById(R.id.btn_Register)
        btn_back = findViewById(R.id.btn_Back)
        btn_confirm = findViewById(R.id.btn_Confirm)


        btn_back.setOnClickListener {
            onBackPressed()
        }



        btn_register.setOnClickListener {
            //회원가입 처리 시작
            var str_email : String = edt_email.text.toString()
            var str_pwd : String = edt_pwd.text.toString()
            var str_name : String = edt_name.text.toString()
            var str_phoneNum : String = edt_phone.text.toString()
            var str_nickname : String = edt_nickname.text.toString()


            if(str_email.equals("") || str_pwd.equals("") || str_name.equals("") || str_phoneNum.equals("") || str_nickname.equals("")){
                Toast.makeText(this, "가입 정보를 모두 입력하세요", Toast.LENGTH_SHORT).show()
            }else{

                //firebaseauth 진행
                mFirebaseAuth!!.createUserWithEmailAndPassword(str_email, str_pwd)?.addOnCompleteListener(this){
                    if(it.isSuccessful){
                        val mFirebaseUser : FirebaseUser? = mFirebaseAuth?.currentUser

                        val User = User(mFirebaseUser!!.uid, str_email, str_pwd, str_name, str_phoneNum, str_nickname)

                        mDatabaseRef.child("UserAccount").child(mFirebaseUser!!.uid).setValue(User)

                        Toast.makeText(this, "$str_name 님, 가입을 축하합니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this, "이미 등록된 아이디이거나\n 비밀번호가 6자 이상이 아닙니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}