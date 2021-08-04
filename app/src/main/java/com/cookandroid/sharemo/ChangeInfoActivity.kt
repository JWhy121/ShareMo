package com.cookandroid.sharemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.view.MenuItem
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/*사용자 정보 수정 화면*/
class ChangeInfoActivity : AppCompatActivity() {

    var pickImageFromAlbum = 0
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

    var imgUrl : String = ""
    private lateinit var fbStorage: FirebaseStorage
    private var GALLEY_CODE : Int = 10

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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")
        fbStorage = FirebaseStorage.getInstance()

        edt_infoName = findViewById(R.id.edt_InfoName)
        edt_InfoNickname = findViewById(R.id.edt_InfoNickname)
        edt_infoPwd = findViewById(R.id.edt_InfoPwd)
        edt_infoPhone = findViewById(R.id.edt_InfoPhone)
        iv_infoPhoto = findViewById(R.id.iv_InfoPhoto)
        tv_addPhoto = findViewById(R.id.tv_AddPhoto)

        btn_changeInfo = findViewById(R.id.btn_changeInfo)

        var str_infoName : String


        //사용자의 이름 출력(변경 X)
        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: User? = snapshot.getValue(User::class.java)
                edt_infoName.setText("${user!!.user_name.toString()}")
                //firebase에서 user 닉네임 받아오기
                edt_InfoNickname.setText("${user!!.user_nickname.toString()}")
                //firebase에서 user 전화번호 받아오기
                edt_infoPhone.setText("${user!!.user_phone.toString()}")

            }
        })

        tv_addPhoto.setOnClickListener {
            //앨범 열기
            var intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)

            startActivityForResult(intent, GALLEY_CODE)
        }





        btn_changeInfo.setOnClickListener{

            try {
                var storageReference : StorageReference = fbStorage.getReference()

                var file : Uri = Uri.fromFile(File(imgUrl))
                var riversRef : StorageReference = storageReference.child("images/"+file.lastPathSegment)
                var uploadTask : UploadTask = riversRef.putFile(file)

                var urlTask : Task<Uri> = uploadTask.continueWithTask(Continuation {
                    if(!it.isSuccessful){
                        it.exception
                    }
                    riversRef.downloadUrl
                }).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        var downloadUrl : Uri? = it.result

                        var str_nickname : String = edt_InfoNickname.text.toString()
                        var str_phone : String = edt_infoPhone.text.toString()

                        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").addValueEventListener(object : ValueEventListener {

                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                var user: User? = snapshot.getValue(User::class.java)

                                str_infoName = user!!.user_name.toString()

                                val hashMap : HashMap<String, String> = HashMap()

                                hashMap.put("user_name",str_infoName)
                                hashMap.put("user_profileImage", downloadUrl.toString())
                                hashMap.put("user_nickname", str_nickname)
                                hashMap.put("user_phone", str_phone)
                                hashMap.put("user_uid",user!!.user_uid.toString())
                                hashMap.put("user_email",user!!.user_email.toString())
                                hashMap.put("user_dong",user!!.user_dong.toString())

                                mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").setValue(hashMap)

                            }
                        })

                        Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e : NullPointerException){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == GALLEY_CODE) {
            imgUrl = getRealPathFromUri(data!!.data)
            var cropOptions : RequestOptions = RequestOptions()
            Glide.with(applicationContext)
                    .load(imgUrl)
                    .apply(cropOptions.optionalCircleCrop())
                    .into(iv_infoPhoto)

            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getRealPathFromUri(uri: Uri?) : String{
        var proj : Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var cursorLoader : CursorLoader = CursorLoader(this,uri!!,proj,null,null,null)
        var cursor : Cursor? = cursorLoader.loadInBackground()

        var columIndex : Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        var url : String = cursor.getString(columIndex)
        cursor.close()
        return url
    }



}