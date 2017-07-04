package com.m.cenarius.Update

/**
 * Created by m on 2017/5/4.
 */

class File {

    var path: String? = null
    var md5: String? = null

    fun toRealm(): FileRealm {
        val fileRealm = FileRealm()
        fileRealm.path = path
        fileRealm.md5 = md5
        return fileRealm
    }
}
