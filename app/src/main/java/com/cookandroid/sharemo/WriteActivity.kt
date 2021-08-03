package com.cookandroid.sharemo

import android.Manifest
import android.app.Activity
import android.app.MediaRouteActionProvider
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.MessageFormat.format
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cookandroid.sharemo.databinding.ActivityBottomNavBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.String.format
import java.text.DateFormat
import java.text.MessageFormat.format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlinx.android.synthetic.main.activity_write.*
import kotlin.math.log


/*글쓰기 화면*/
class WriteActivity : AppCompatActivity() {

    lateinit var btn_close: Button
    lateinit var btn_upload: Button
    lateinit var btn_imgUpload: Button

    lateinit var iv_contentImage: ImageView
    var pickImageFromAlbum = 0

    var uriPhoto: Uri? = null

    lateinit var edt_content: EditText
    lateinit var edt_price: EditText
    lateinit var edt_website: EditText

    private var select_spinner: Spinner? = null
    private var arrayAdapter: ArrayAdapter<String>? = null

    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage
    private lateinit var storageRef: StorageReference

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
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            var path: String = Environment.getExternalStorageDirectory().absolutePath
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
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
            var str_content: String = edt_content.text.toString()
            var str_price = edt_price.text.toString()
            var str_website: String = edt_website.text.toString()
            var selectedItem: String = select_spinner!!.getSelectedItem().toString()

            val hashMap : HashMap<String, String> = HashMap()

            //권한이 부여되었는지 확인
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                if(uriPhoto != null){
                    var storageRef = fbStorage?.reference?.child("image/" + UUID.randomUUID().toString())

                    storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {

                        Toast.makeText(this,"Image Uploaded", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, "Failed" + it.message, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
            }

            hashMap.put("imgUrl", uriPhoto.toString())
            hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
            hashMap.put("content", str_content)
            hashMap.put("nickname", nickname)
            hashMap.put("price", str_price)
            hashMap.put("dong", dong)
            hashMap.put("website", str_website)
            hashMap.put("item", selectedItem)
            hashMap.put("timstamp", timestamp)



            /*var Post = PostData(mFirebaseAuth!!.currentUser!!.uid, str_content, nickname, str_price,
                    dong, str_website, selectedItem, timestamp, str_imgUrl)*/

            mDatabaseRef.ref.child("PostData").child("${selectedItem.toString()}").push().setValue(hashMap)
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                //선택된 이미지 경로
                uriPhoto = data?.data

                iv_contentImage.setImageURI(uriPhoto)
                iv_contentImage.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }
    }

    private fun ImageUpload() {




/*        if (uriPhoto != null) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")
            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(uriPhoto!!)
                    .addOnSuccessListener {
                        hashMap.put("imgUrl", uriPhoto.toString())
                        Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Failed" + it.message, Toast.LENGTH_SHORT)
                                .show()
                    }
        }*/

    }



}