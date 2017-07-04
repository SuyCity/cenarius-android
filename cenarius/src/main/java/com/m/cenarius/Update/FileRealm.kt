package com.m.cenarius.Update

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by m on 2017/4/26.
 */

class FileRealm : RealmObject() {

    @PrimaryKey
    var path: String? = null
    var md5: String? = null
}
