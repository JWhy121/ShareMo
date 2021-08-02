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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import android.Manifest
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.activity_change_info.*


/*사용자 정보 수정 화면*/
class ChangeInfoActivity : AppCompatActivity() {

    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스

    //private lateinit var database : FirebaseDatabase
    private lateinit var auth: FirebaseAuth


    //private lateinit var profileFragment: ProfileFragment
    lateinit var edt_infoName: EditText
    lateinit var edt_InfoNickname: EditText
    lateinit var edt_infoPwd: EditText
    lateinit var edt_InfoPhone: EditText
    lateinit var btn_changeInfo: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        //툴바 사용
        setSupportActionBar(findViewById(R.id.toolbar))
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)


        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")

        lateinit var edt_infoName: EditText


        edt_infoName = findViewById(R.id.edt_InfoName)
        edt_InfoNickname = findViewById(R.id.edt_InfoNickname)
        edt_infoPwd = findViewById(R.id.edt_InfoPwd)
        edt_InfoPhone = findViewById(R.id.edt_InfoPhone)

        btn_changeInfo = findViewById(R.id.btn_changeInfo)


        //사용자의 이름 출력(변경 X)
        mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: User? = snapshot.getValue(User::class.java)
                edt_infoName.setText("${user!!.user_name.toString()}")
            }
        })

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)


        auth = FirebaseAuth.getInstance()
        btn_changeInfo.setOnClickListener {

            //닉네임 받아오기
            //닉네임 수정 시 string으로 받아서 저장할 수 있도록
            //전화번호 받아오기
            //전화번호 수정 시 string으로 받아서 저장할 수 있도록
            //비밀 번호 입력 시 그대로 수정 저장
            //회원정보 수정 시작

            changePassword()

        }


    }

    private fun changePassword() {
        if (edt_infoPwd.text.isNotEmpty() && edt_ChangePwd.text.isNotEmpty()) {

            val user: FirebaseUser? = auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider
                        .getCredential(user!!.email!!, edt_infoPwd.text.toString())
// Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "비밀번호가 변경되었습니다. ", Toast.LENGTH_SHORT).show()
                                user?.updatePassword(edt_ChangePwd.text.toString())
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this, "비밀번호 수정 완료", Toast.LENGTH_SHORT).show()
                                                /////////
                                                finish()
                                                ///////
                                            }
                                        }
                            } else {
                                Toast.makeText(this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                            }
                        }


            } else {
                var intent = Intent(this, ProfileFragment::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(this, "비밀번호가 입력되지 않았습니다. ", Toast.LENGTH_SHORT).show()

        }


    }
   //툴바 뒤로가기
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
       }
        return super.onOptionsItemSelected(item)
    }
}