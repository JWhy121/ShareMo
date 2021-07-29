package com.cookandroid.sharemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        var intent = Intent(this, MainActivity::class.java)
        //2초 뒤 MainActivity로 가기
        Handler().postDelayed({ startActivity(intent) }, 2000L)

    }
}