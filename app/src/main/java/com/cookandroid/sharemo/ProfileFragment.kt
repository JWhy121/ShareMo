package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth


/*프로필 화면*/
class ProfileFragment : Fragment(){

    companion object {
        const val TAG : String = "로그"

        fun newInstance() : ProfileFragment {
            return ProfileFragment()
        }

    }

    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ProfileFragment - onCreate() called")

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

        var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증

        mFirebaseAuth = FirebaseAuth.getInstance()

        //정보 수정 액티비티로 넘어가는 버튼
        btn_changeInfo.setOnClickListener {

            val intent = Intent(getActivity(), ChangeInfoActivity::class.java)
            startActivity(intent)
            //다른 액티비티에서 전환할 때
            //activity?.finish()
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