package com.m.example

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.image.imagemodule.ImagePickerControllerModule
import com.image.imagemodule.OnChoosePhothsListener
import com.image.imagemodule.OnTakePhotosListener
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        take_phtot.setOnClickListener{takePhoto()}
        choose_image.setOnClickListener { chooseImage() }
    }

    private fun chooseImage() {
        ImagePickerControllerModule.chooseLocalPhotos(this)
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        ImagePickerControllerModule.takePhotos(this)
    }

    /**
     * 显示图片
     */
    private fun showImage(path: String?) {
        val bitmap = ImagePickerControllerModule.getimage(path);
        if (bitmap != null) show_image.setImageBitmap(bitmap)
    }

    /**
     * 拍照、选图、回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //拍照图片回调
        ImagePickerControllerModule.takePhtotoForActivityResult(requestCode, resultCode, object : OnTakePhotosListener {
            override fun onshow(url: String?) {
                showImage(url)
            }
        })

        //选择系统图片回调
        ImagePickerControllerModule.chooseLocalPhotosForActivityResult(requestCode,resultCode,data,this, object : OnChoosePhothsListener {
            override fun onshow(url: String?) {
                showImage(url)
            }
        })


    }

}
