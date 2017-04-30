package com.m.cenarius.Update;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.m.cenarius.Native.Cenarius;
import com.orhanobut.logger.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

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
    private UpdateManager() {
        EventBus.getDefault().register(this);
    }

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

    private Realm mainRealm = Realm.getInstance(new RealmConfiguration.Builder().name(dbName).build());

    private Config resourceConfig;
    private String resourceFiles;
    private Config cacheConfig;
    private RealmResults<FileRealm> cacheFiles;
    private Config serverConfig;
    private String serverConfigData;
    private String serverFiles;
    private String downloadFiles;

    private void update(UpdateCallback callback) {
        updateCallback = callback;
        // 开发模式，直接成功
        if (developMode) {
            complete(State.UPDATE_SUCCESS);
            return;
        }

        // 重置变量
        progress = 0;

        loadLocalConfig();
        loadLocalFiles();
        downloadConfig();
    }

    /*加载本地的config*/
    private void loadLocalConfig() {
        File cacheConfigFile = new File(cacheConfigUrl);
        try {
            cacheConfig = JSON.parseObject(FileUtils.readFileToByteArray(cacheConfigFile), Config.class);
        } catch (IOException e) {
            cacheConfig = null;
        }

        try {
            InputStream inputStream = Cenarius.application.getAssets().open(resourceConfigUrl);
            resourceConfig = JSON.parseObject(IOUtils.toByteArray(inputStream), Config.class);
        } catch (IOException e) {
            Logger.e(e, "You must put " + configName + " file in www folder");
        }
    }

    /*加载本地的路由表*/
    private void loadLocalFiles() {
        cacheFiles = mainRealm.where(FileRealm.class).findAll();
        try {
            InputStream inputStream = Cenarius.application.getAssets().open(resourceConfigUrl);
            JSON.parseObject(IOUtils.toByteArray(inputStream), Config.class);
        } catch (IOException e) {
            Logger.e(e, "You must put " + filesName + " file in www folder");
        }
    }

    private void downloadConfig() {
        complete(State.DOWNLOAD_CONFIG_FILE);

    }

    private void complete(State state) {
        EventBus.getDefault().post(new MessageEvent(state));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private void onMessageEvent(MessageEvent event) {
        updateCallback.completion(event.state, progress);
    }


    private static class MessageEvent {

        public State state;

        private MessageEvent(State state) {
            this.state = state;
        }
    }

}
