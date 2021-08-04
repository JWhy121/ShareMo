package com.cookandroid.sharemo

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/*게시글 상세 화면*/
class PostActivity : AppCompatActivity() {

    lateinit var post_nickname : TextView
    lateinit var post_dong : TextView
    lateinit var post_content : TextView
    lateinit var post_price : TextView
    lateinit var post_website : TextView
    lateinit var btn_startChat : Button
    lateinit var btn_delete : Button
    lateinit var btn_website : Button
    lateinit var img_postImg : ImageView
    lateinit var img_userImg : ImageView

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스
    var uriPhoto: Uri? = null

    var chatRoomUid : String? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //툴바 사용
        setSupportActionBar(findViewById(R.id.toolbar))
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        post_nickname = findViewById(R.id.post_Nickname)
        post_content = findViewById(R.id.post_Content)
        post_dong = findViewById(R.id.post_Dong)
        post_price = findViewById(R.id.post_Price)
        post_website = findViewById(R.id.post_Website)
        btn_startChat = findViewById(R.id.btn_StartChat)
        btn_delete = findViewById(R.id.btn_Delete)
        btn_website = findViewById(R.id.btn_Website)
        img_postImg = findViewById(R.id.img_PostImg)
        img_userImg = findViewById(R.id.img_UserImg)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")


        var user_nickname : String = ""


        var intent : Intent = getIntent()

        var uid : String? = intent.getStringExtra("UID")
        var nickname : String? = intent.getStringExtra("NICKNAME")
        var img_url : String? = intent.getStringExtra("IMGURL")

        Log.d("꾸깅", "$img_url")

        post_nickname.setText(nickname.toString())
        post_content.setText(intent.getStringExtra("CONTENT"))
        post_dong.setText(intent.getStringExtra("DONG"))
        post_price.setText(intent.getStringExtra("PRICE"))
        post_website.setText(intent.getStringExtra("WEBSITE"))

        if(img_url == null){
            img_postImg.setImageResource(R.drawable.user)
        }else{
            Glide.with(applicationContext).load(img_url).into(img_postImg)
        }



        if(mFirebaseAuth!!.currentUser!!.uid == uid){
            btn_startChat.visibility = View.GONE
        }else{
            btn_delete.visibility = View.GONE
        }

        if(post_website.text.equals(""))
            btn_website.visibility = View.INVISIBLE

        btn_website.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("${post_website.text.toString()}"))
            startActivity(intent)
        }

        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth!!.currentUser!!.uid!!}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //파이어베이스의 데이터를 가져옴
                        var user: User? = snapshot.getValue(User::class.java)

                        user_nickname = user!!.user_nickname.toString()
                        Log.d("tatata", "$user_nickname")

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Tag", "안됨")
                    }
                })




        //채팅하기 버튼 클릭
        btn_startChat.setOnClickListener {

            //////////////////////////////////////////////////


            var intent = Intent(this, ChatRoomActivity::class.java)

/*            var fragment = Fragment()

            lateinit var bundle : Bundle
            bundle.putString("post_uid", uid)
            bundle.putString("post_nickname", nickname)

            fragment.arguments = bundle*/

            intent.putExtra("post_uid", uid)
            intent.putExtra("post_nickname", nickname)
            intent.putExtra("sender_nickname", user_nickname)

            startActivity(intent)
        }

        btn_delete.setOnClickListener {

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