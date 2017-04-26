package com.m.cenarius.Update;

import android.app.Application;
import android.content.Context;

import com.m.cenarius.Native.Cenarius;

import java.io.File;

/**
 * Created by m on 2017/4/24.
 * `UpdateManager` 提供更新能力。
 */

public final class UpdateManager {

    public enum State {
        UNZIP_WWW, //解压www
        UNZIP_WWW_ERROR, //解压www出错
        DOWNLOAD_CONFIG_FILE, //下载配置文件
        DOWNLOAD_CONFIG_FILE_ERROR, //下载配置文件出错
        DOWNLOAD_FILES_FILE, //下载路由表
        DOWNLOAD_FILES_FILE_ERROR, //下载路由表出错
        DOWNLOAD_FILES, //下载文件
        DOWNLOAD_FILES_ERROR, //下载文件出错
        UPDATE_SUCCESS, //更新文件成功
    }

    /*设置远程资源地址*/
    public static void setServerUrl(String url) {
        UpdateManager.serverUrl = url;
    }

    public static void setDevelopMode(Boolean mode) {
        sharedInstance.developMode = mode;
    }

    public static String getCacheUrl() {
        return UpdateManager.cacheUrl;
    }

    /*更新*/
    public interface UpdateCallback {
        void completion(State state, int progress);
    }

    private static UpdateManager sharedInstance = new UpdateManager();

    private static String wwwName = "www";
    private static String zipName = "www.zip";
    private static String filesName = "cenarius-files.json";
    private static String configName = "cenarius-config.json";
    private static String dbName = "cenarius-files.realm";
    private static int retry = 5;
    private static int maxConcurrentOperationCount = 2;

    private static String resourceUrl = wwwName;
    private static String resourceConfigUrl = resourceUrl + "/" + configName;
    private static String resourceFilesUrl = resourceUrl + "/" + filesName;
    private static String resourceZipUrl = resourceUrl + "/" + zipName;
    private static String cacheUrl = Cenarius.application.getDir(wwwName, Context.MODE_PRIVATE).getPath();
    private static String cacheConfigUrl = cacheUrl + "/" + configName;
    private static String serverUrl;
    private static String serverConfigUrl = serverUrl + "/" + configName;
    private static String serverFilesUrl = serverUrl + "/" + filesName;

    private Boolean developMode = false;
    private UpdateCallback updateCallback;
    private int progress = 0;
    private Boolean isDownloadFileError = false;
    private int downloadFilesCount = 0;

}
