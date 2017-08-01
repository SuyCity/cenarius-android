package com.image.imagemodule

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import com.anthonycr.grant.PermissionsManager
import com.anthonycr.grant.PermissionsResultAction
import java.io.File
import java.lang.Exception
import android.graphics.BitmapFactory
import android.graphics.Bitmap




/**
 *********************************************
 * @Description 图片选择
 * @author:YuSy
 * @E-mail:you551@163.com
 * @qq:447234062
 * @date 2017/7/31 15:25
 **********************************************
 */

class ImagePickerControllerModule {

    companion object {
        val TAKE_PHOTO_REQUESTCODE = 0x112;
        val CHECK_PHOTO_REQUESTCODE = 0x113;

        var file: File ?= null;

        /**
         * 图片拍照接口
         */
        fun takePhotos(mActivity: Activity) {
            val permissionArray = arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, permissionArray, object : PermissionsResultAction() {
                override fun onGranted() {
                    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        return
                    }
                    //图片的路劲
                    val filePaht = Environment.getExternalStorageDirectory().getPath() + File.separator + "temp"+File.separator
                    val fileParent = File(filePaht)
                    if (!fileParent.exists()) {
                        fileParent.mkdirs()
                    }

                    file = File(fileParent,"${System.currentTimeMillis()}.jpg")
                    if (!file?.exists()!!){
                        file?.createNewFile()
                    }

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    if (android.os.Build.VERSION.SDK_INT < 24) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    } else {
                        //7.0以上（包括 7.0）系统，拍照处理
                        val contentValues = ContentValues(1)
                        contentValues.put(MediaStore.Images.Media.DATA, file?.getAbsolutePath());
                        val uri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    }
                    mActivity.startActivityForResult(intent, TAKE_PHOTO_REQUESTCODE)

                }

                override fun onDenied(permission: String?) {
                    Toast.makeText(mActivity, "$permission 权限被禁止，请到手机设置→权限管理→选择允许调用$permission 权限", Toast.LENGTH_LONG).show();
                }
            })
        }


        /**
         * 拍照图片后的回调
         */
        fun takePhtotoForActivityResult(requestCode: Int, resultCode: Int, listener: OnTakePhotosListener){
            if (requestCode == TAKE_PHOTO_REQUESTCODE && resultCode == Activity.RESULT_OK){
                listener.onshow(file.toString())
                file = null
            }
        }

        /**
         * 图片选择接口
         */
        fun chooseLocalPhotos(mActivity: Activity) {
            val permissionArray = arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, permissionArray, object : PermissionsResultAction() {
                override fun onGranted() {
                    val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    mActivity.startActivityForResult(intent, CHECK_PHOTO_REQUESTCODE)
                }

                override fun onDenied(permission: String?) {
                    Toast.makeText(mActivity, "$permission 权限被禁止，请到手机设置→权限管理→选择允许调用$permission 权限", Toast.LENGTH_LONG).show();
                }
            })
        }

        /**
         * 图片选择后的回调
         */
        fun chooseLocalPhotosForActivityResult(requestCode: Int, resultCode: Int, data:Intent?, mActivity: Activity, listener: OnChoosePhothsListener){
            if (requestCode == CHECK_PHOTO_REQUESTCODE && resultCode == Activity.RESULT_OK){
                try {
                    val absolutePath = getAbsolutePath(mActivity, data?.data)
                    listener.onshow(absolutePath)
                } catch(e: Exception) {
                    listener.onshow(null)
                }
            }

        }

        /**
         * 取出图片的路径
         */
        private fun getAbsolutePath(context: Context,uri: Uri?): String? {
            if (null == uri) return null
            val scheme = uri.scheme
            var data: String? = null
            if (scheme == null){
                data = uri.path
            } else if (TextUtils.equals(ContentResolver.SCHEME_FILE, scheme)) {
                data = uri.path
            } else if (TextUtils.equals(ContentResolver.SCHEME_CONTENT, scheme)) {
                val cursor = context.getContentResolver().query(uri,
                        arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (null != cursor) {
                    if (cursor!!.moveToFirst()) {
                        val index = cursor!!.getColumnIndex(
                                MediaStore.Images.ImageColumns.DATA)
                        if (index > -1) {
                            data = cursor!!.getString(index)
                        }
                    }
                    cursor!!.close()
                }
            }
            return data
        }


        /**
         * 得到Bitmap,压缩后的
         * @param srcPath 文件路径
         * *
         * @return
         */
        fun getimage(srcPath: String?): Bitmap? {

            if (TextUtils.isEmpty(srcPath)) return null
            val newOpts = BitmapFactory.Options()
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)//此时返回bm为空

            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            val hh = 800f//这里设置高度为800f
            val ww = 480f//这里设置宽度为480f
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 1//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0)
                be = 1
            newOpts.inSampleSize = be//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
            return bitmap
        }
    }

}