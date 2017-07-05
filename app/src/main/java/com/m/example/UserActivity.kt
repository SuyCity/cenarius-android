package com.m.example

import android.app.Activity
import android.os.Bundle

import com.m.cenarius.Extension.*
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val params = getParamsJsonObject()
        if (params != null) {
            val id = params.getString("id")
            val name = params.getString("name")
            idTextView.text = id
            nameTextView.text = name
        }
    }
}
