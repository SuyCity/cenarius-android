package com.m.example

import android.app.Activity
import android.os.Bundle

import com.m.cenarius.Network.OpenApi
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_open_api.*
import java.util.TreeMap

class OpenApiActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_api)

        OpenApi.set("APPKEY", "APPSECRET")
        signButton.setOnClickListener { sign() }
    }

    fun sign() {
        val url = urlEditText.text.toString()
        Logger.d("url: ", url)

        val headers = TreeMap<String, String>()
        headers.put("X-Requested-With", "OpenAPIRequest")
        if (jsonCheckBox.isChecked) {
            headers.put("Content-Type", "application/json")
        }

        val parameters = TreeMap<String, String>()
        parameters.put("pa", "A&A")
        parameters.put("c", "0")

        val urlSign = OpenApi.sign(url, parameters, headers)
        Logger.d("urlSign: ", urlSign)
        signTextView.text = urlSign
    }
}
