package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*


/*게시글 리스트 화면*/
class ShareListActivity : AppCompatActivity() {

    lateinit var mSearchText: EditText
    lateinit var rv_post : RecyclerView
    lateinit var adapter : RecyclerView.Adapter<PostDataAdapter.CustomViewHolder>
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var arrayList: ArrayList<PostData>

    lateinit var FirebaseRecyclerAdapter : FirebaseRecyclerAdapter<PostData , UsersViewHolder>


    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference

    lateinit var imgBtn_eidt : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_list)

        //툴바 사용
        setSupportActionBar(findViewById(R.id.toolbar))
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        rv_post = findViewById(R.id.rv_Post) //아이디 연결
        imgBtn_eidt = findViewById(R.id.imgBtn_Edit)
        mSearchText = findViewById(R.id.edt_SearchText)

        rv_post.setHasFixedSize(true) //리사이클러뷰 성능 강화
        layoutManager = LinearLayoutManager(this)
        rv_post.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        arrayList = ArrayList<PostData>() //PostData 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

        var intent : Intent = getIntent()

        var selectedItem : String? = intent.getStringExtra("SELECTED_ITEM")


        mDatabaseRef.child("PostData").child("$selectedItem")
            .orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //파이어베이스의 데이터를 가져옴
                arrayList.clear()

                for (data : DataSnapshot in snapshot.getChildren()) {
                    var postData : PostData? = data.getValue(PostData::class.java)

                    arrayList.add(postData!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                    Log.d("태그", "$arrayList")
                }
                adapter.notifyDataSetChanged() //리스트 저장 및 새로고침

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        adapter = PostDataAdapter(arrayList, this)
        rv_post.setAdapter(adapter)

        //글쓰기 버튼에 클릭 리스너 연결
        imgBtn_eidt.setOnClickListener {
            var intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }

        mSearchText.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val searchText = mSearchText.getText().toString().trim()

                loadFirebaseData(searchText)
            }
        } )

    }


    //검색한 데이터 띄우기
    private fun loadFirebaseData(searchText : String) {

        if(searchText.isEmpty()){

            FirebaseRecyclerAdapter.cleanup()

            rv_post.setAdapter(adapter)

        }else {

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")

            var intent : Intent = getIntent()

            var selectedItem : String? = intent.getStringExtra("SELECTED_ITEM")

            val firebaseSearchQuery = mDatabaseRef.child("PostData").child("$selectedItem").orderByChild("content").startAt(searchText).endAt(searchText + "\uf8ff")

            FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<PostData, UsersViewHolder>(
                    PostData::class.java,
                    R.layout.list_item,
                    UsersViewHolder::class.java,
                    firebaseSearchQuery
            ) {
                override fun populateViewHolder(viewHolder: UsersViewHolder?, model: PostData?, position: Int) {

                    viewHolder!!.content.text = model?.content
                    viewHolder!!.dong.text = model?.dong
                    viewHolder!!.price.text = model?.price
                    viewHolder!!.nickname.text = model?.nickname
                    Glide.with(viewHolder!!.itemView)
                        .load(model?.imgUrl)
                        .into(viewHolder!!.iv_img)
                }
            }
            rv_post.setAdapter(FirebaseRecyclerAdapter)
        }
    }

    class UsersViewHolder(var mview : View) : RecyclerView.ViewHolder(mview) {
        val content = mview.findViewById<TextView>(R.id.tv_Content)
        val dong = mview.findViewById<TextView>(R.id.tv_Dong)
        val nickname = mview.findViewById<TextView>(R.id.tv_Nickname)
        val price = mview.findViewById<TextView>(R.id.tv_Price)
        val iv_img = mview.findViewById<ImageView>(R.id.iv_Image)
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

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu_search, menu)

        val search = menu?.findItem(R.id.menu_action_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        //searchView?.setOnQueryTextListener(this)


        return true
    }


     */

}