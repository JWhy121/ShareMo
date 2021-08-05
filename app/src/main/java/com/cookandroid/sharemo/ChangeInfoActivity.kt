package com.cookandroid.sharemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.Manifest
import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.view.MenuItem
import android.widget.*
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
import java.util.*


/*사용자 정보 수정 화면 액티비티*/
class ChangeInfoActivity : AppCompatActivity() {

    //파이어베이스
    private var mFirebaseAuth : FirebaseAuth? = null
    private lateinit var mDatabaseRef : DatabaseReference

    //위젯 연결 변수 선언
    lateinit var edt_infoName : EditText
    lateinit var edt_InfoNickname : EditText
    lateinit var edt_infoPwd : EditText
    lateinit var edt_infoPhone : EditText
    lateinit var edt_changePwd : EditText
    lateinit var btn_changeInfo : Button
    lateinit var iv_infoPhoto : ImageView
    lateinit var tv_addPhoto : TextView


    lateinit var user: User

    //이미지 업로드 시 필요한 변수 선언
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

        //파이어베이스에서 인스턴스 가져오기
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")
        fbStorage = FirebaseStorage.getInstance()

        //위젯 연결
        edt_infoName = findViewById(R.id.edt_InfoName)
        edt_InfoNickname = findViewById(R.id.edt_InfoNickname)
        edt_infoPwd = findViewById(R.id.edt_InfoPwd)
        edt_infoPhone = findViewById(R.id.edt_InfoPhone)
        edt_changePwd = findViewById(R.id.edt_ChangePwd)

        iv_infoPhoto = findViewById(R.id.iv_InfoPhoto)
        tv_addPhoto = findViewById(R.id.tv_AddPhoto)

        btn_changeInfo = findViewById(R.id.btn_changeInfo)

        var str_infoName : String
        var str_infoNickname : String


        //사용자의 이름, 닉네임, 전화번호 기본 출력 (이름, 닉네임은 수정 불가능)
        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: User? = snapshot.getValue(User::class.java)
                edt_infoName.setText("${user!!.user_name.toString()}")

                edt_InfoNickname.setText("${user!!.user_nickname.toString()}")

                edt_infoPhone.setText("${user!!.user_phone.toString()}")
                if(user!!.user_profileImage.equals("")){
                    iv_infoPhoto.setImageResource(R.drawable.user)
                }else{
                    var cropOptions : RequestOptions = RequestOptions()
                    Glide.with(applicationContext)
                            .load(user!!.user_profileImage)
                            .apply(cropOptions.optionalCircleCrop())
                            .into(iv_infoPhoto)
                }
            }
        })

        //사진 업로드 버튼
        tv_addPhoto.setOnClickListener {

            //앨범 열기
            var intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)

            startActivityForResult(intent, GALLEY_CODE)
        }

        //정보 수정 버튼
        btn_changeInfo.setOnClickListener{

            //사진 업로드 시 스토리지에 저장하고 내 정보 화면에 보여줌, 비밀번호 변경 시 비밀번호 변경 완료
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
                    //이미지 업로드 성공 시
                    if(it.isSuccessful)
                    {
                        var downloadUrl : Uri? = it.result

                        var str_nickname : String = edt_InfoNickname.text.toString()
                        var str_phone : String = edt_infoPhone.text.toString()

                        //파이어베이스에 정보 변경 내용 업데이트
                        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")
                                .addValueEventListener(object : ValueEventListener {

                                    override fun onCancelled(error: DatabaseError) {

                                    }
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var user: User? = snapshot.getValue(User::class.java)

                                        var str_nickname : String = edt_InfoNickname.text.toString()
                                        var str_phone : String = edt_infoPhone.text.toString()

                                        str_infoName = user!!.user_name.toString()

                                        val hashMap : HashMap<String, String> = HashMap()

                                        hashMap.put("user_profileImage", downloadUrl.toString())
                                        hashMap.put("user_name",str_infoName)
                                        hashMap.put("user_nickname", str_nickname)
                                        hashMap.put("user_phone", str_phone)
                                        hashMap.put("user_uid",user!!.user_uid.toString())
                                        hashMap.put("user_email",user!!.user_email.toString())
                                        hashMap.put("user_dong",user!!.user_dong.toString())

                                        mDatabaseRef.child("UserAccount")
                                                .child("${mFirebaseAuth?.currentUser!!.uid}").setValue(hashMap)

                                    }
                                })
                        Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                        //비밀번호 변경
                        if(edt_infoPwd.text.isNotEmpty() && edt_changePwd.text.isNotEmpty()) {
                            changePassword()
                        }
                        finish()
                    }
                }.addOnFailureListener {
                    //이미지 업로드 하지 않고 다른 정보만 수정하거나 아무것도 수정하지 않았을 경우
                    var str_nickname : String = edt_InfoNickname.text.toString()
                    var str_phone : String = edt_infoPhone.text.toString()

                    //파이어베이스에 정보 변경 내용 업데이트
                    mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")
                            .addValueEventListener(object : ValueEventListener {

                                override fun onCancelled(error: DatabaseError) {

                                }
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var user: User? = snapshot.getValue(User::class.java)

                                    str_infoName = user!!.user_name.toString()

                                    val hashMap : HashMap<String, String> = HashMap()

                                    hashMap.put("user_name",str_infoName)
                                    hashMap.put("user_nickname", str_nickname)
                                    hashMap.put("user_phone", str_phone)
                                    hashMap.put("user_profileImage", user!!.user_profileImage.toString())
                                    hashMap.put("user_uid",user!!.user_uid.toString())
                                    hashMap.put("user_email",user!!.user_email.toString())
                                    hashMap.put("user_dong",user!!.user_dong.toString())

                                    mDatabaseRef.child("UserAccount")
                                            .child("${mFirebaseAuth?.currentUser!!.uid}").setValue(hashMap)
                                }
                            })
                    //비밀번호 변경
                    if(edt_infoPwd.text.isNotEmpty() && edt_changePwd.text.isNotEmpty()) {
                        changePassword()
                    }
                    finish()
                }
            }catch (e : NullPointerException){
            }
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

    //비밀번호 변경 메소드
    private fun changePassword() {

        val user: FirebaseUser? = mFirebaseAuth!!.currentUser
        val credential = EmailAuthProvider
                .getCredential(user!!.email!!, edt_infoPwd.text.toString())

        user.reauthenticate(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "비밀번호가 변경되었습니다. ", Toast.LENGTH_SHORT).show()
                        user?.updatePassword(edt_changePwd.text.toString())
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "비밀번호 수정 완료", Toast.LENGTH_SHORT).show()
                                    }
                                }
                    } else {
                        Toast.makeText(this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    //갤러리 코드 확인해서 열고 사진 가져와서 이미지뷰에 출력
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == GALLEY_CODE) {
            if(resultCode == Activity.RESULT_OK){
                imgUrl = getRealPathFromUri(data!!.data)
                var cropOptions : RequestOptions = RequestOptions()
                Glide.with(applicationContext)
                        .load(imgUrl)
                        .apply(cropOptions.optionalCircleCrop())
                        .into(iv_infoPhoto)
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //절대 경로 구하기
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