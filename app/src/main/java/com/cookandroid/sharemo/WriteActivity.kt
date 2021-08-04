package com.cookandroid.sharemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


/*글쓰기 화면*/
class WriteActivity : AppCompatActivity() {

    lateinit var btn_close: Button
    lateinit var btn_upload: Button
    lateinit var btn_imgUpload: Button

    lateinit var iv_contentImage: ImageView

    var imgUrl : String = ""

    lateinit var edt_content: EditText
    lateinit var edt_price: EditText
    lateinit var edt_website: EditText

    private var select_spinner: Spinner? = null
    private var arrayAdapter: ArrayAdapter<String>? = null

    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var GALLEY_CODE : Int = 10

    var timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)


        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")
        fbStorage = FirebaseStorage.getInstance()

        btn_close = findViewById(R.id.btn_Close)
        btn_upload = findViewById(R.id.btn_Upload)
        btn_imgUpload = findViewById(R.id.btn_ImgUpload)

        iv_contentImage = findViewById(R.id.iv_ContentImage)

        edt_content = findViewById(R.id.edt_Content)
        edt_price = findViewById(R.id.edt_Price)
        edt_website = findViewById(R.id.edt_Website)

        select_spinner = findViewById(R.id.spn_Select)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.selectItem) as Array<String>)
        arrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        select_spinner!!.adapter = arrayAdapter

        select_spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        btn_imgUpload.setOnClickListener {
            //앨범 열기
            var intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)

            startActivityForResult(intent, GALLEY_CODE)
        }

        lateinit var nickname: String
        lateinit var dong: String

        btn_close.setOnClickListener {
            onBackPressed()
        }

        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth!!.currentUser!!.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //파이어베이스의 데이터를 가져옴
                        var user: User? = snapshot.getValue(User::class.java)
                        Log.d("택", "${user!!.user_email.toString()}")

                        nickname = user!!.user_nickname.toString()
                        dong = user!!.user_dong.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Tag", "Failed")
                    }
                })



        btn_upload.setOnClickListener {
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

                        val hashMap : HashMap<String, String> = HashMap()

                        var str_content: String = edt_content.text.toString()
                        var str_price = edt_price.text.toString()
                        var str_website: String = edt_website.text.toString()
                        var selectedItem: String = select_spinner!!.getSelectedItem().toString()

                        hashMap.put("imgUrl", downloadUrl.toString())
                        hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
                        hashMap.put("content", str_content)
                        hashMap.put("nickname", nickname)
                        hashMap.put("price", str_price)
                        hashMap.put("dong", dong)
                        hashMap.put("website", str_website)
                        hashMap.put("item", selectedItem)
                        hashMap.put("timstamp", timestamp)

                        mDatabaseRef.ref.child("PostData").child("${selectedItem}").push().setValue(hashMap)
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        Toast.makeText(this, "업로드", Toast.LENGTH_SHORT).show()
                                    }
                                }

                        Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                        var intent = Intent(this, ShareListActivity::class.java)
                        intent.putExtra("SELECTED_ITEM", selectedItem)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {

                    val hashMap : HashMap<String, String> = HashMap()

                    var str_content: String = edt_content.text.toString()
                    var str_price = edt_price.text.toString()
                    var str_website: String = edt_website.text.toString()
                    var selectedItem: String = select_spinner!!.getSelectedItem().toString()

                    hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
                    hashMap.put("content", str_content)
                    hashMap.put("nickname", nickname)
                    hashMap.put("price", str_price)
                    hashMap.put("dong", dong)
                    hashMap.put("website", str_website)
                    hashMap.put("item", selectedItem)
                    hashMap.put("timstamp", timestamp)

                    mDatabaseRef.ref.child("PostData").child("${selectedItem}").push().setValue(hashMap)

                    Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, ShareListActivity::class.java)
                    intent.putExtra("SELECTED_ITEM", selectedItem)
                    startActivity(intent)
                    finish()

                }
            }catch (e : NullPointerException){
                Toast.makeText(this, "이미지 선택 안함", Toast.LENGTH_SHORT).show();
            }
        }









        /*btn_upload.setOnClickListener {
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
                        val hashMap : HashMap<String, String> = HashMap()

                        var str_content: String = edt_content.text.toString()
                        var str_price = edt_price.text.toString()
                        var str_website: String = edt_website.text.toString()
                        var selectedItem: String = select_spinner!!.getSelectedItem().toString()

                        hashMap.put("imgUrl", downloadUrl.toString())
                        hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
                        hashMap.put("content", str_content)
                        hashMap.put("nickname", nickname)
                        hashMap.put("price", str_price)
                        hashMap.put("dong", dong)
                        hashMap.put("website", str_website)
                        hashMap.put("item", selectedItem)
                        hashMap.put("timstamp", timestamp)

                        mDatabaseRef.ref.child("PostData").child("${selectedItem}").push().setValue(hashMap)
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        Toast.makeText(this, "업로드 완료", Toast.LENGTH_SHORT).show()
                                    }
                                }

                        Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                        var intent = Intent(this, ShareListActivity::class.java)
                        intent.putExtra("SELECTED_ITEM", selectedItem)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {

                }
            }catch (e : NullPointerException){
                Toast.makeText(this, "이미지 선택 X", Toast.LENGTH_SHORT).show();
            }
        }*/
    }


    //이미지 받아와서 화면에 출력
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == GALLEY_CODE) {
            imgUrl = getRealPathFromUri(data!!.data)
            var cropOptions : RequestOptions = RequestOptions()
            Glide.with(applicationContext)
                    .load(imgUrl)
                    .into(iv_contentImage)

            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    //절대경로 받아오기
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