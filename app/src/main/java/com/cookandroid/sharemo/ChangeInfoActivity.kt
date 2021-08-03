package com.cookandroid.sharemo

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.cookandroid.sharemo.HomeFragment.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.view.MenuItem
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


/*사용자 정보 수정 화면*/
class ChangeInfoActivity : AppCompatActivity() {

    var pickImageFromAlbum = 0
    var fbStorage : FirebaseStorage? = null
    var uriPhoto : Uri? = null

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스

    lateinit var edt_infoName : EditText
    lateinit var edt_InfoNickname : EditText
    lateinit var edt_infoPwd : EditText
    lateinit var edt_infoPhone : EditText
    lateinit var btn_changeInfo : Button
    lateinit var iv_infoPhoto : ImageView
    lateinit var tv_addPhoto : TextView

    lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)


        //툴바 사용
        setSupportActionBar(findViewById(R.id.toolbar))
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)



        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")
        fbStorage = FirebaseStorage.getInstance()

        edt_infoName = findViewById(R.id.edt_InfoName)
        edt_InfoNickname = findViewById(R.id.edt_InfoNickname)
        edt_infoPwd = findViewById(R.id.edt_InfoPwd)
        edt_infoPhone = findViewById(R.id.edt_InfoPhone)
        iv_infoPhoto = findViewById(R.id.iv_InfoPhoto)
        tv_addPhoto = findViewById(R.id.tv_AddPhoto)

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

        tv_addPhoto.setOnClickListener {
            //앨범 열기
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
        }





        btn_changeInfo.setOnClickListener{
            //이름 받아오기
           // var str_name : String = edt_infoName.text.toString()
            //닉네임 받아오기
            //닉네임 수정 시 string으로 받아서 저장할 수 있도록
            //전화번호 받아오기
            //전화번호 수정 시 string으로 받아서 저장할 수 있도록
            //비밀 번호 입력 시 그대로 수정 저장

            //회원정보 수정 시작

            //권한이 부여되었는지 확인
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                ImageUpload()
            }

            changePassword()

            finish()
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == pickImageFromAlbum) {
            if(resultCode == Activity.RESULT_OK){
                //선택된 이미지 경로
                uriPhoto = data?.data

                iv_infoPhoto.setImageURI(uriPhoto)
                iv_infoPhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    private fun changePassword() {
        if (edt_infoPwd.text.isNotEmpty()) {
            val user: FirebaseUser? = mFirebaseAuth!!.currentUser
            val credential = EmailAuthProvider
                    .getCredential(user!!.email!!, "yeonji")
// Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "비밀번호가 변경되었습니다. ", Toast.LENGTH_SHORT).show()
                            user?.updatePassword(edt_infoPwd.text.toString())
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "비밀번호 수정 완료", Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                    }
                        } else {
                            Toast.makeText(this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
        }else {
            Toast.makeText(this, "비밀번호가 입력되지 않았습니다. ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ImageUpload() {
        val mFirebaseUser : String = mFirebaseAuth?.currentUser!!.uid!!.toString()
        var imgFileName = "USER_IMAGE" + mFirebaseUser +  "_.png"
        var storageRef = fbStorage?.reference?.child("images/userImages")?.child(imgFileName)

        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            Toast.makeText(this,"Image Uploaded", Toast.LENGTH_SHORT).show()
        }
    }

}