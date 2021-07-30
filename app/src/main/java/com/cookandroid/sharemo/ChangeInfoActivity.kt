package com.cookandroid.sharemo

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cookandroid.sharemo.HomeFragment.Companion.TAG

class ChangeInfoActivity : AppCompatActivity() {

    //private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        var btn_back : Button = findViewById(R.id.btn_Back)

        btn_back.setOnClickListener {
            var intent = Intent(this, BottomNavActivity::class.java)
            //profileFragment = ProfileFragment.newInstance()
            //supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()
            startActivity(intent)
        }

    }



}