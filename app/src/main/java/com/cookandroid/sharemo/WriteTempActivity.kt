package com.cookandroid.sharemo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Adapter
import android.widget.CursorAdapter
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_write_temp.*
import kotlinx.android.synthetic.main.list_item.view.*


class WriteTempActivity : AppCompatActivity() {

    lateinit var mSearchText: EditText//시원
    lateinit var rv_post : RecyclerView//mRecyclerView
    lateinit var adapter : RecyclerView.Adapter<PostDataAdapter.CustomViewHolder>
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var arrayList: ArrayList<PostData>

    lateinit var FirebaseRecyclerAdapter : FirebaseRecyclerAdapter<PostData , PostViewHolder>//시원 추가


    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_temp)

        mSearchText = findViewById(R.id.searchText)//시원
        rv_post = findViewById(R.id.rv_Post) //아이디 연결
        rv_post.setHasFixedSize(true) //리사이클러뷰 성능 강화
        layoutManager = LinearLayoutManager(this)
        rv_post.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        arrayList = ArrayList<PostData>() //PostData 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo")



        mDatabaseRef.child("PostData").orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
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

        //검색창에 입력 후 이벤트 처리
        mSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            //입력하는 중간에 처리
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = mSearchText.getText().toString().trim()
                loadFirebaseData(searchText)
            }

            //입력이 끝났을 때 처리
            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    //검색한 데이터 띄우기
    private fun loadFirebaseData(searchText : String) {
        if(searchText.isEmpty()){
            FirebaseRecyclerAdapter.cleanup()
            rv_post.adapter = FirebaseRecyclerAdapter

        }else {
            val firebaseSearchQuery = mDatabaseRef.orderByChild("PostData").startAt(searchText).endAt(searchText + "\uf8ff")

            val FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<PostData, PostViewHolder>(
                PostData::class.java,
                R.layout.list_item,
                PostViewHolder::class.java,
                firebaseSearchQuery
            ){
                override fun populateViewHolder(viewHolder: PostViewHolder, model: PostData?, position: Int) {
                    viewHolder.mview.tv_Dong.setText(model?.dong)
                    viewHolder.mview.tv_Content.setText(model?.content)
                    viewHolder.mview.tv_Nickname.setText(model?.nickname)
                    viewHolder.mview.tv_Price.setText(model?.price)

                    //Picasso.with(applicationContext).load(model?.image).into(viewHolder.mview.UserImageView)//이미지 할 때 사용
                }
            }
            rv_post.adapter = FirebaseRecyclerAdapter
        }

    }

    class PostViewHolder(var mview: View): RecyclerView.ViewHolder(mview){

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

