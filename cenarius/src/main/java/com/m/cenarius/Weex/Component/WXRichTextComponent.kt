package com.m.cenarius.Weex.Component

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.widget.TextView

import com.taobao.weex.WXSDKInstance
import com.taobao.weex.dom.WXDomObject
import com.taobao.weex.ui.component.WXComponent
import com.taobao.weex.ui.component.WXComponentProp
import com.taobao.weex.ui.component.WXVContainer

/**
 * Created by m on 2017/6/28.
 */

class WXRichTextComponent(instance: WXSDKInstance, dom: WXDomObject, parent: WXVContainer<*>) : WXComponent<TextView>(instance, dom, parent) {

    override fun initComponentHostView(context: Context): TextView {
        val view = TextView(context)
        view.movementMethod = LinkMovementMethod.getInstance()
        return view
    }

    @WXComponentProp(name = "tel")
    fun setTelLink(tel: String) {
        val spannable = SpannableString(tel)
        spannable.setSpan(URLSpan("tel:" + tel), 0, tel.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        (hostView as TextView).text = spannable
    }
}
