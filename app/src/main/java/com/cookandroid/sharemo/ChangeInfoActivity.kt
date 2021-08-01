package com.cookandroid.sharemo

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.cookandroid.sharemo.HomeFragment.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ChangeInfoActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스
    //private lateinit var database : FirebaseDatabase


    //private lateinit var profileFragment: ProfileFragment
    lateinit var edt_infoName : EditText
    lateinit var edt_InfoNickname : EditText
    lateinit var edt_InfoPwd : EditText
    lateinit var edt_InfoPhone : EditText
    lateinit var btn_changeInfo : Button
    lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        //var user = Firebase.auth.currentUser
       // if(user != null){

      //  }else {

      //  }

        //val user = Firebase.auth.currentUser


        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("sharMo")

        edt_infoName = findViewById(R.id.edt_InfoName)
        edt_InfoNickname = findViewById(R.id.edt_InfoNickname)
        edt_InfoPwd = findViewById(R.id.edt_InfoPwd)
        edt_InfoPhone = findViewById(R.id.edt_InfoPhone)

        btn_changeInfo = findViewById(R.id.btn_changeInfo)



        btn_changeInfo.setOnClickListener{
            //이름 받아오기
           // var str_name : String = edt_infoName.text.toString()
            //닉네임 받아오기
            //닉네임 수정 시 string으로 받아서 저장할 수 있도록
            //전화번호 받아오기
            //전화번호 수정 시 string으로 받아서 저장할 수 있도록
            //비밀 번호 입력 시 그대로 수정 저장

            //회원정보 수정 시작


        }


    }



}