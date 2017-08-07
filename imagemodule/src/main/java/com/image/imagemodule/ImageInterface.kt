package com.image.imagemodule

/**
 *********************************************
 * @Description 图片选择或拍照的接口
 * @author:YuSy
 * @E-mail:you551@163.com
 * @qq:447234062
 * @date 2017/7/31 15:28
 **********************************************
 */

/**
 * 选择图片返回接口
 */
interface OnChoosePhothsListener{

    fun onshow(url: String?)
}

/**
 * 拍照图片返回接口
 */
interface OnTakePhotosListener{
    /**
     * 先临时存好图片地址，最后在Activity中的onActivityResult中根据requestCode判断接收
     * requestCode为ImagePickerControllerModule.TAKE_PHOTO_REQUESTCODE
     */
    fun onshow(url: String?)
}