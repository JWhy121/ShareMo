package com.cookandroid.sharemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentHome: FragmentHome
    private lateinit var fragmentChat: FragmentChat
    private lateinit var fragmentMypage: FragmentMypage

    lateinit var bottom_Nav : com.google.android.material.bottomnavigation.BottomNavigationView



    companion object{
        const val TAG: String = "로그"
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //레이아웃과 연결
        setContentView(R.layout.activity_navigation)

        Log.d(TAG, "NavigationActivity - onCreate() called")

        bottom_Nav.findViewById<BottomNavigationView>(R.id.bottom_Nav)
        bottom_Nav.setOnNavigationItemSelectedListener (this)

        fragmentHome = FragmentHome.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, fragmentHome).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "NavigationActivity - onNavigationItemSelected() called")

        when(item.itemId){
            R.id.menu_Chatting -> {
                Log.d(TAG, "NavigationActivity - 채팅 클릭!")
                fragmentChat = FragmentChat.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, fragmentChat).commit()
            }
            R.id.menu_Home -> {
                Log.d(TAG, "NavigationActivity - 홈 클릭!")
                fragmentHome = FragmentHome.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, fragmentHome).commit()
            }
            R.id.menu_Mypage -> {
                Log.d(TAG, "NavigationActivity - 내정보 클릭!")
                fragmentMypage = FragmentMypage.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, fragmentMypage).commit()
            }
        }

        return true
    }
}