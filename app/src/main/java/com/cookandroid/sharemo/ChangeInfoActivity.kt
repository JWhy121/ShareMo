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
import androidx.core.app.ActivityCompat
import com.cookandroid.sharemo.HomeFragment.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.Manifest
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


/*사용자 정보 수정 화면*/
class ChangeInfoActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스
    //private lateinit var database : FirebaseDatabase


    //private lateinit var profileFragment: ProfileFragment

    lateinit var edt_infoNickname : EditText
    lateinit var edt_infoPwd : EditText
    lateinit var edt_infoPhone : EditText
    lateinit var btn_changeInfo : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")

        lateinit var edt_infoName : EditText


        edt_infoName = findViewById(R.id.edt_InfoName)
        edt_infoNickname = findViewById(R.id.edt_InfoNickname)
        edt_infoPwd = findViewById(R.id.edt_InfoPwd)
        edt_infoPhone = findViewById(R.id.edt_InfoPhone)

        btn_changeInfo = findViewById(R.id.btn_changeInfo)


        //사용자의 이름 출력(변경 X)
        mDatabaseRef.addValueEventListener(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user : User? = snapshot.getValue(User::class.java)
                edt_infoName.setText("${user!!.user_name.toString()}")
            }
        })

        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)


        btn_changeInfo.setOnClickListener{
            //이름 받아오기

            //닉네임 받아오기
            //닉네임 수정 시 string으로 받아서 저장할 수 있도록
            //전화번호 받아오기
            //전화번호 수정 시 string으로 받아서 저장할 수 있도록
            //비밀 번호 입력 시 그대로 수정 저장

            //회원정보 수정 시작

            finish()
        }


    }



}