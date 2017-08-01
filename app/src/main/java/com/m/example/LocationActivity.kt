package com.m.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.location.locationmodule.LocationUtils
import com.location.locationmodule.OnLocationListener
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        button.setOnClickListener { LocationUtils.getLocation(this, object : OnLocationListener {
            override fun onGetLocation(map: Map<String, String>?) {
                textView.text = map?.toString()
            }
        }) }
    }
}
