package com.example.imagedownloader.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.imagedownloader.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = MainFragment()
        val presenter = MainPresenter(fragment)

        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.container, fragment)
        ft.commit()
        fragment.setPresenter(presenter)

    }
}
