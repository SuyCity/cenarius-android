package com.m.cenarius.Update;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.litesuits.go.OverloadPolicy;
import com.litesuits.go.SchedulePolicy;
import com.litesuits.go.SmartExecutor;
import com.m.cenarius.Native.Cenarius;
import com.m.cenarius.Network.Network;
import com.m.cenarius.Utils.Utils;
import com.orhanobut.logger.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        serverUrl = url;
        serverConfigUrl = serverUrl + File.separator + configName;
        serverFilesUrl = serverUrl + File.separator + filesName;
    }

    public static void setDevelopMode(Boolean mode) {
        sharedInstance.developMode = mode;
    }

    public static File getCacheUrl() {
        return cacheUrl;
    }

    public static void update(UpdateCallback callback) {
        sharedInstance.updateAction(callback);
    }

    /*更新*/
    public interface UpdateCallback {
        void completion(State state, int progress);
    }

    private static String wwwName = "www";
    private static String zipName = "www.zip";
    private static String filesName = "cenarius-files.json";
    private static String configName = "cenarius-config.json";
    private static String dbName = "cenarius-files.realm";
    private static int retryConut = 5;
    private static int maxConcurrentOperationCount = 2;

    private static String resourceUrl = wwwName;
    private static String resourceConfigUrl = resourceUrl + File.separator + configName;
    private static String resourceFilesUrl = resourceUrl + File.separator + filesName;
    private static String resourceZipUrl = resourceUrl + File.separator + zipName;
    private static File cacheUrl = Cenarius.application.getDir(wwwName, Context.MODE_PRIVATE);
    private static File cacheConfigUrl = new File(cacheUrl, configName);
    private static String serverUrl;
    private static String serverConfigUrl;
    private static String serverFilesUrl;

    private Boolean developMode = false;
    private UpdateCallback updateCallback;
    private int progress = 0;
    private Boolean isDownloadFileError = false;
    private int downloadFilesCount = 0;
    private int unzipFilesCount = 0;

    private Realm mainRealm = Realm.getInstance(new RealmConfiguration.Builder().name(dbName).build());

    private Config resourceConfig;
    private List<com.m.cenarius.Update.File> resourceFiles;
    private Config cacheConfig;
    private RealmResults<FileRealm> cacheFiles;
    private Config serverConfig;
    private byte[] serverConfigData;
    private List<FileRealm> serverFiles;
    private List<FileRealm> downloadFiles;

    private static UpdateManager sharedInstance = new UpdateManager();
    private UpdateManager() {
        EventBus.getDefault().register(this);
    }

    private void updateAction(UpdateCallback callback) {
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
        try {
            cacheConfig = JSON.parseObject(FileUtils.readFileToString(cacheConfigUrl, "UTF-8"), Config.class);
        } catch (IOException e) {
            cacheConfig = null;
        }

        try {
            InputStream inputStream = Cenarius.application.getAssets().open(resourceConfigUrl);
            resourceConfig = JSON.parseObject(IOUtils.toString(inputStream, "UTF-8"), Config.class);
        } catch (IOException e) {
            Logger.e(e, "You must put " + configName + " file in www folder");
        }
    }

    /*加载本地的路由表*/
    private void loadLocalFiles() {
        cacheFiles = mainRealm.where(FileRealm.class).findAll();
        try {
            InputStream inputStream = Cenarius.application.getAssets().open(resourceFilesUrl);
            resourceFiles = JSON.parseArray(IOUtils.toString(inputStream, "UTF-8"), com.m.cenarius.Update.File.class);
        } catch (IOException e) {
            Logger.e(e, "You must put " + filesName + " file in www folder");
        }
    }

    private void downloadConfig() {
        complete(State.DOWNLOAD_CONFIG_FILE);
        Call<ResponseBody> call = Network.requset(serverConfigUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        serverConfigData = response.body().bytes();
                        serverConfig = JSON.parseObject(serverConfigData, Config.class);
                        if (isWwwFolderNeedsToBeInstalled()) {
                            unzipWww();
                        } else if (shouldDownloadWww()) {
                            downloadFilesFile();
                        } else {
                            complete(State.UPDATE_SUCCESS);
                        }
                    } catch (IOException e) {
                        Logger.e(e, "downloadConfig fail");
                    }
                } else {
                    complete(State.DOWNLOAD_CONFIG_FILE_ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e(t, "downloadConfig fail");
                complete(State.DOWNLOAD_CONFIG_FILE_ERROR);
            }
        });
    }

    private void downloadFilesFile() {
        complete(State.DOWNLOAD_FILES_FILE);
        loadLocalConfig();
        loadLocalFiles();
        Call<ResponseBody> call = Network.requset(serverFilesUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        serverFiles = JSON.parseObject(response.body().bytes(), FileRealm[].class);
                        downloadFiles = getDownloadFiles(serverFiles);
                        if (downloadFiles.size() > 0) {
                            downloadFiles(downloadFiles);
                        } else {
                            saveConfig();
                            complete(State.UPDATE_SUCCESS);
                        }
                    } catch (IOException e) {
                        Logger.e(e, "downloadFilesFile fail");
                    }
                } else {
                    complete(State.DOWNLOAD_FILES_FILE_ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e(t, "downloadFilesFile fail");
                complete(State.DOWNLOAD_FILES_FILE_ERROR);
            }
        });
    }

    private void downloadFiles(List<FileRealm> files) {
        final SmartExecutor smallExecutor = new SmartExecutor();
        smallExecutor.setCoreSize(maxConcurrentOperationCount);
        smallExecutor.setQueueSize(files.size());
        smallExecutor.setSchedulePolicy(SchedulePolicy.FirstInFistRun);
        smallExecutor.setOverloadPolicy(OverloadPolicy.ThrowExecption);
        downloadFilesCount = 0;
        isDownloadFileError = false;
        for (final FileRealm file : files) {
            smallExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!downloadFile(file, retryConut)) {
                        smallExecutor.cancelWaitingTask(null);
                    }
                }
            });
        }
    }

    private boolean downloadFile(FileRealm file, int retryConut) {
        Call<ResponseBody> call = Network.requset(serverUrl + File.separator + file.getPath());
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                FileUtils.writeByteArrayToFile(new File(cacheUrl, file.getPath()), response.body().bytes());
                downloadFileSuccess(file);
                return true;
            }
        } catch (IOException e) {
            Logger.e(e, null);
        }
        return downloadFileRetry(file, retryConut);
    }

    private boolean downloadFileRetry(FileRealm file, int retryConut) {
        if (retryConut > 0) {
            return downloadFile(file, retryConut - 1);
        } else {
            downloadFileError();
            return false;
        }
    }

    private void downloadFileSuccess(FileRealm file) {
        EventBus.getDefault().post(new DownloadFileSuccessEvent(file));
    }

    private void downloadFileError() {
        EventBus.getDefault().post(new DownloadFileErrorEvent());
    }

    private void saveConfig() {
        try {
            FileUtils.writeByteArrayToFile(cacheConfigUrl, serverConfigData);
        } catch (IOException e) {
            Logger.e(e, null);
        }
    }

    private void saveFiles(final List<FileRealm> files) {
        mainRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
                realm.insert(files);
            }
        });
    }

    private List<FileRealm> getDownloadFiles(List<FileRealm> serverFiles) {
        List<FileRealm> downloadFiles = new ArrayList<>();
        for (FileRealm file: serverFiles) {
            if (shouldDownload(file)) {
                downloadFiles.add(file);
            }
        }
        return downloadFiles;
    }

    private boolean shouldDownload(FileRealm serverFile) {
        for (FileRealm cacheFile: cacheFiles) {
            if (cacheFile.getPath().equals(serverFile.getPath()) && cacheFile.getMd5().equals(serverFile.getMd5())) {
                return false;
            }
        }
        return true;
    }

    private boolean isWwwFolderNeedsToBeInstalled() {
        if (cacheConfig == null || Utils.compareVersion(cacheConfig.release, resourceConfig.release) < 0) {
            //没有缓存或者缓存比预置低
            return true;
        }
        return false;
    }

    private void unzipWww() {
        complete(State.UNZIP_WWW);
        mainRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(cacheUrl);
                    InputStream inputStream = Cenarius.application.getAssets().open(resourceZipUrl);
                    ZipUtil.iterate(inputStream, new ZipEntryCallback() {
                        @Override
                        public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                            String name = zipEntry.getName();
                            if (name != null) {
                                File file = new File(cacheUrl, name);
                                if (zipEntry.isDirectory()) {
                                    org.zeroturnaround.zip.commons.FileUtils.forceMkdir(file);
                                } else {
                                    org.zeroturnaround.zip.commons.FileUtils.forceMkdir(file.getParentFile());
                                    org.zeroturnaround.zip.commons.FileUtils.copy(in, file);
                                    unzipFileSuccess();
                                }
                            }
                        }
                    });
                    unzipSuccess();
                } catch (IOException e) {
                    Logger.e(e, null);
                    complete(State.UNZIP_WWW_ERROR);
                }
            }
        }).start();
    }

    private void unzipFileSuccess() {
        unzipFilesCount ++;
        int progress = unzipFilesCount * 100 / resourceFiles.size();
        if (shouldDownloadWww()) {
            progress /= 2;
        }
        if (this.progress != progress) {
            this.progress = progress;
            complete(State.UNZIP_WWW);
        }
    }

    private boolean shouldDownloadWww() {
        if (hasMinVersion(serverConfig)) {
            // 满足最小版本要求
            if (isWwwFolderNeedsToBeInstalled()) {
                return Utils.compareVersion(serverConfig.release, resourceConfig.release) > 0;
            } else {
                return Utils.compareVersion(serverConfig.release, cacheConfig.release) > 0;
            }
        }
        return false;
    }

    private boolean hasMinVersion(Config serverConfig) {
        String versionName = Utils.getAppVersionName();
        if (versionName != null && serverConfig.android_min_version != null && Utils.compareVersion(versionName, serverConfig.android_min_version) >= 0) {
            // 满足最小版本要求
            return true;
        }
        return false;
    }

    private void unzipSuccess() {

    }

    private void complete(State state) {
        EventBus.getDefault().post(new CompleteEvent(state));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onCompleteEvent(CompleteEvent event) {
        updateCallback.completion(event.state, progress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onUnzipSuccessEvent(UnzipSuccessEvent event) {
        try {
            InputStream inputStream = Cenarius.application.getAssets().open(resourceConfigUrl);
            FileUtils.copyInputStreamToFile(inputStream, cacheConfigUrl);
            saveFiles(resourceFiles);
            if (shouldDownloadWww()) {
                downloadFilesFile();
            } else {
                complete(State.UPDATE_SUCCESS);
            }
        } catch (IOException e) {
            Logger.e(e, null);
        }
    }


    private static class CompleteEvent {

        public State state;

        private CompleteEvent(State state) {
            this.state = state;
        }
    }

    private static class UnzipSuccessEvent {

    }

    private static class DownloadFileSuccessEvent {

        public FileRealm file;

        private DownloadFileSuccessEvent(FileRealm file) {
            this.file = file;
        }
    }

    private static class DownloadFileErrorEvent {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onDownloadFileErrorEvent(DownloadFileErrorEvent event) {
        if (!isDownloadFileError) {
            isDownloadFileError = true;
            complete(State.DOWNLOAD_FILES_ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onDownloadFileSuccessEvent(final DownloadFileSuccessEvent event) {
        if (isDownloadFileError) {
            return;
        }
        mainRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(event.file);
            }
        });
        downloadFilesCount += 1;
        int unzipProgress = progress;
        int downloadProgress = downloadFilesCount * (100 - unzipProgress) / downloadFiles.size();
        int progress = unzipProgress + downloadProgress;
        if (this.progress != progress) {
            this.progress = progress;
            complete(State.DOWNLOAD_FILES);
        }
        if (downloadFilesCount == downloadFiles.size()) {
            saveConfig();
            saveFiles(serverFiles);
            complete(State.UPDATE_SUCCESS);
        }
    }


}
