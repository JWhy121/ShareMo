package com.cookandroid.sharemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/*게시글 상세 화면 액티비티*/
class PostActivity : AppCompatActivity() {

    //파이어베이스
    private var mFirebaseAuth : FirebaseAuth? = null
    private lateinit var mDatabaseRef : DatabaseReference

    //위젯 연결할 변수 선언
    lateinit var post_nickname : TextView
    lateinit var post_dong : TextView
    lateinit var post_content : TextView
    lateinit var post_price : TextView
    lateinit var post_website : TextView
    lateinit var btn_startChat : Button
    lateinit var btn_website : Button
    lateinit var img_postImg : ImageView
    lateinit var img_postProfileImg : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //툴바 사용
        setSupportActionBar(findViewById(R.id.toolbar))
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        //변수와 위젯 id 연결
        post_nickname = findViewById(R.id.post_Nickname)
        post_content = findViewById(R.id.post_Content)
        post_dong = findViewById(R.id.post_Dong)
        post_price = findViewById(R.id.post_Price)
        post_website = findViewById(R.id.post_Website)
        btn_startChat = findViewById(R.id.btn_StartChat)
        btn_website = findViewById(R.id.btn_Website)
        img_postImg = findViewById(R.id.img_PostImg)
        img_postProfileImg = findViewById(R.id.img_postProfileImg)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")


        var user_nickname : String = ""
        var post_profileImg : String = ""

        //intent로 값 받아옴
        var intent : Intent = getIntent()
        var uid : String? = intent.getStringExtra("UID")
        var nickname : String? = intent.getStringExtra("NICKNAME")
        var img_url : String? = intent.getStringExtra("IMGURL")

        //화면에 받아온 값 출력
        post_nickname.setText(nickname.toString())
        post_content.setText(intent.getStringExtra("CONTENT"))
        post_dong.setText(intent.getStringExtra("DONG"))
        post_price.setText(intent.getStringExtra("PRICE"))
        post_website.setText(intent.getStringExtra("WEBSITE"))

        if(img_url == null){
            img_postImg.setImageResource(R.drawable.null_image)
        }else{
            var cropOptions : RequestOptions = RequestOptions()
            Glide.with(applicationContext)
                    .load(img_url)
                    .apply(cropOptions.optionalCircleCrop())
                    .into(img_postImg)
        }

        //해당 글이 자신의 글일 경우 채팅하기 버튼 보이지 않게 함
        if(mFirebaseAuth!!.currentUser!!.uid == uid){
            btn_startChat.visibility = View.GONE
        }

        //웹사이트를 등록하지 않았다면 웹사이트 바로가기 버튼 보이지 않게 함
        if(post_website.text.equals(""))
            btn_website.visibility = View.INVISIBLE

        //웹사이트 바로가기 버튼 클릭 시 해당 사이트로 외부 연결
        btn_website.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("${post_website.text.toString()}"))
            startActivity(intent)
        }

        //화면에 게시자의 프로필 화면 출력
        mDatabaseRef.child("UserAccount").child("$uid")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //파이어베이스의 데이터를 가져옴
                        var user: User? = snapshot.getValue(User::class.java)
                        post_profileImg = user!!.user_profileImage.toString()
                        var cropOptions : RequestOptions = RequestOptions()
                        if(user!!.user_profileImage.equals("")){
                            img_postProfileImg.setImageResource(R.drawable.user)
                        }else{
                            Glide.with(applicationContext)
                                    .load(post_profileImg)
                                    .apply(cropOptions.optionalCircleCrop())
                                    .into(img_postProfileImg)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Tag", "Failed")
                    }
                })

        //현재 사용자의 닉네임을 받아옴
        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth!!.currentUser!!.uid!!}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user: User? = snapshot.getValue(User::class.java)

                        user_nickname = user!!.user_nickname.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })




        //채팅하기 버튼 클릭하면 ChatRoomActivity로 이동하고 글 작성자의 uid, nickname과 현재 사용자의 닉네임을 intent로 전달
        btn_startChat.setOnClickListener {

            var intent = Intent(this, ChatRoomActivity::class.java)

            intent.putExtra("post_uid", uid)
            intent.putExtra("post_nickname", nickname)
            intent.putExtra("sender_nickname", user_nickname)

            startActivity(intent)
        }
    }

    //툴바 : 뒤로가기 버튼
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