package com.m.cenarius.Update

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by m on 2017/4/26.
 */

open class FileRealm : RealmObject() {

    @PrimaryKey
    open var path: String? = null
    open var md5: String? = null
}
