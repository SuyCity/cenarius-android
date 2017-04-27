package com.m.cenarius.Update;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by m on 2017/4/26.
 */

public class FileRealm extends RealmObject {

    @PrimaryKey
    private String path;
    private String md5;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
