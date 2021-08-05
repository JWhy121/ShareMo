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


/*로그인 화면 액티비티*/
class LoginActivity : AppCompatActivity() {

    //파이어베이스
    private var mFirebaseAuth : FirebaseAuth? = null
    private lateinit var mDatabaseRef : DatabaseReference

    //위젯 연결할 변수 선언
    lateinit var edtEmail : EditText
    lateinit var edtPwd : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //파이어베이스에서 인스턴스 가져오기
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference()

        edtEmail = findViewById(R.id.edt_Email)
        edtPwd = findViewById(R.id.edt_Pwd)

        var btnLogin : Button = findViewById(R.id.btn_Login)
        var btnRegister : Button = findViewById(R.id.btn_Register)

        //로그인 버튼 : 두 정보 모두 입력하지 않으면 모두 입력하라는 Toast 메시지, 회원정보가 일치하면 로그인 성공
        btnLogin.setOnClickListener {

            if(edtEmail.text.toString().equals("") || edtPwd.text.toString().equals("")) {
                Toast.makeText(this, "로그인 정보를 모두 입력하세요", Toast.LENGTH_SHORT).show()
            }else{
                mFirebaseAuth!!.signInWithEmailAndPassword(edtEmail.text.toString(), edtPwd.text.toString())!!.addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        edtEmail.setText("")
                        edtPwd.setText("")
                        var intent = Intent(this, BottomNavActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "아이디 혹은 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //회원가입 페이지로 연결하는 버튼
        btnRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}