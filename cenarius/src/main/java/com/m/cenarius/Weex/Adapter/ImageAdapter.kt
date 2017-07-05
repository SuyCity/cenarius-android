package com.m.cenarius.Weex.Adapter

import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.taobao.weex.WXEnvironment
import com.taobao.weex.WXSDKManager
import com.taobao.weex.adapter.IWXImgLoaderAdapter
import com.taobao.weex.common.WXImageStrategy
import com.taobao.weex.dom.WXImageQuality

/**
 * Created by m on 2017/4/26.
 */

class ImageAdapter : IWXImgLoaderAdapter {

    override fun setImage(url: String, view: ImageView?, quality: WXImageQuality, strategy: WXImageStrategy) {
        WXSDKManager.getInstance().postOnUiThread(Runnable {
            if (view == null || view.layoutParams == null) {
                return@Runnable
            }
            if (url.isEmpty()) {
                view.setImageBitmap(null)
                return@Runnable
            }
            var temp = url
            if (url.startsWith("//")) {
                temp = "https:" + url
            }
            if (view.layoutParams.width <= 0 || view.layoutParams.height <= 0) {
                return@Runnable
            }
            if (!strategy.placeHolder.isEmpty()) {
                //                    Glide.with(WXEnvironment.getApplication()).load(temp).placeholder(0).into(view);
                //                    Picasso.Builder builder=new Picasso.Builder(WXEnvironment.getApplication());
                //                    Picasso picasso=builder.build();
                //                    picasso.load(Uri.parse(strategy.placeHolder)).into(view);

                //                    view.setTag(strategy.placeHolder.hashCode(),picasso);
            }
            Glide.with(WXEnvironment.getApplication())
                    .load(temp)
                    .listener(object : RequestListener<String, GlideDrawable> {
                        override fun onException(e: Exception, model: String, target: Target<GlideDrawable>, isFirstResource: Boolean): Boolean {
                            strategy.imageListener?.onImageFinish(url, view, false, null)
                            return false
                        }

                        override fun onResourceReady(resource: GlideDrawable, model: String, target: Target<GlideDrawable>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                            strategy.imageListener?.onImageFinish(url, view, true, null)
                            return false
                        }
                    })
                    .into(view)


            //                Picasso.with(WXEnvironment.getApplication())
            //                        .load(temp)
            //                        .into(view, new Callback() {
            //                            @Override
            //                            public void onSuccess() {
            //                                if(strategy.getImageListener()!=null){
            //                                    strategy.getImageListener().onImageFinish(url,view,true,null);
            //                                }
            //
            //                                if(!TextUtils.isEmpty(strategy.placeHolder)){
            //                                    ((Picasso) view.getTag(strategy.placeHolder.hashCode())).cancelRequest(view);
            //                                }
            //                            }
            //                            @Override
            //                            public void onError() {
            //                                if(strategy.getImageListener()!=null){
            //                                    strategy.getImageListener().onImageFinish(url,view,false,null);
            //                                }
            //                            }
            //                        });
        }, 0)
    }
}
