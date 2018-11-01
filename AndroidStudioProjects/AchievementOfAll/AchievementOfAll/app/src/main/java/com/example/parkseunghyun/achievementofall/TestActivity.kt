package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import org.jetbrains.anko.startActivity

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        button.setOnClickListener{
            startActivity<LoginActivity>()
        }
    }
}
