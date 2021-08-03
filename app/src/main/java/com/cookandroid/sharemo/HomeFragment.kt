package com.cookandroid.sharemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment


/*홈 화면*/
class HomeFragment : Fragment() {


    companion object {
        const val TAG : String = "로그"

        fun newInstance() : HomeFragment {
            return HomeFragment()
        }

    }


    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "HomeFragment - onCreate() called")

    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "HomeFragment - onAttach() called")
    }

    // 뷰가 생성되었을 때
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        Log.d(TAG, "HomeFragment - onCreateView() called")

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var imgBtn_glocery : ImageView = view.findViewById(R.id.imgBtn_Glocery)
        var imgBtn_talent : ImageView = view.findViewById(R.id.imgBtn_Talent)
        var imgBtn_car : ImageView = view.findViewById(R.id.imgBtn_Car)
        var imgBtn_etc : ImageView = view.findViewById(R.id.imgBtn_Etc)

        imgBtn_glocery.setOnClickListener {

            val intent = Intent(getActivity(), ShareListActivity::class.java)
            intent.putExtra("SELECTED_ITEM","생필품/음식")
            startActivity(intent)
        }

        imgBtn_talent.setOnClickListener {

            val intent = Intent(getActivity(), ShareListActivity::class.java)
            intent.putExtra("SELECTED_ITEM","재능")
            startActivity(intent)
        }

        imgBtn_car.setOnClickListener {

            val intent = Intent(getActivity(), ShareListActivity::class.java)
            intent.putExtra("SELECTED_ITEM","이동수단")
            startActivity(intent)
        }

        imgBtn_etc.setOnClickListener {

            val intent = Intent(getActivity(), ShareListActivity::class.java)
            intent.putExtra("SELECTED_ITEM","기타")
           startActivity(intent)
        }

        return view

    }

}