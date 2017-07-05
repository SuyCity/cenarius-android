package com.m.cenarius.Update

/**
 * Created by m on 2017/5/4.
 */

open class File {

    open var path: String? = null
    open var md5: String? = null

    open fun toRealm(): FileRealm {
        val fileRealm = FileRealm()
        fileRealm.path = path
        fileRealm.md5 = md5
        return fileRealm
    }
}
