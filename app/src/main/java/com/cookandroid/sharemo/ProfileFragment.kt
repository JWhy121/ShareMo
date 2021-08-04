package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Text
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


/*프로필 화면*/
class ProfileFragment : Fragment(){

    lateinit var mDatabaseRef : DatabaseReference
    lateinit var mDatabase : FirebaseDatabase
    lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser

    companion object {
        const val TAG : String = "로그"

        fun newInstance() : ProfileFragment {
            return ProfileFragment()
        }


        //닉네임과 동네 프로필

        private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
        private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스


        //닉네임 받아오기
        lateinit var tv_nickname : TextView
        lateinit var tv_dong : TextView




    }

    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ProfileFragment - onCreate() called")

        //닉네임, 동네 받아오기
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")

        mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: User? = snapshot.getValue(User::class.java)
                //firebase에서 user 닉네임 받아오기
                tv_NickName.setText("${user!!.user_nickname.toString()}")
                //firebase에서 user 전화번호 받아오기
                tv_Dong.setText("${user!!.user_dong.toString()}")

            }
        })


    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ProfileFragment - onAttach() called")
    }

    // 뷰가 생성되었을 때
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        Log.d(TAG, "ProfileFragment - onCreateView() called")

        //fragment내 findViewById 사용
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        var btn_changeInfo : Button = view.findViewById(R.id.btn_ChangeInfo)
        var btn_logout : Button = view.findViewById(R.id.btn_Logout)
        var btn_drop : Button = view.findViewById(R.id.btn_Drop)
        var iv_infoImg : ImageView = view.findViewById(R.id.iv_Prof)

        mFirebaseAuth = FirebaseAuth.getInstance()
        val mFirebaseUser : FirebaseUser? = mFirebaseAuth?.currentUser

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount").child(mFirebaseUser!!.uid)

        mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: User? = snapshot.getValue(User::class.java)
                //firebase에서 user 이름 받아오기
                if(user!!.user_profileImage == null){
                    iv_infoImg.setImageResource(R.drawable.user)
                }else{
                    Glide.with(this@ProfileFragment).load(user!!.user_profileImage).into(iv_infoImg)
                }

            }
        })


        var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증

        mFirebaseAuth = FirebaseAuth.getInstance()

        //정보 수정 액티비티로 넘어가는 버튼
        btn_changeInfo.setOnClickListener {

            val intent = Intent(getActivity(), ChangeInfoActivity::class.java)
            startActivity(intent)
        }

        //로그아웃 버튼
        btn_logout.setOnClickListener {

            mFirebaseAuth!!.signOut()
            val intent = Intent(getActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        //탈퇴 버튼
        btn_drop.setOnClickListener {

            mFirebaseAuth!!.currentUser!!.delete()
            val intent = Intent(getActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }


}