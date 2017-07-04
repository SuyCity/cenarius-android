package com.m.cenarius.Update

import android.content.Context
import android.text.TextUtils

import com.alibaba.fastjson.JSON
import com.litesuits.common.io.FileUtils
import com.litesuits.common.io.IOUtils
import com.litesuits.go.OverloadPolicy
import com.litesuits.go.SchedulePolicy
import com.litesuits.go.SmartExecutor
import com.m.cenarius.Native.Cenarius
import com.m.cenarius.Network.Network
import com.m.cenarius.Utils.VersionUtil
import com.orhanobut.logger.Logger

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.zip.ZipEntry

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by m on 2017/4/24.
 * `UpdateManager` 提供更新能力。
 */

class UpdateManager private constructor() {

    enum class State {
        UNZIP_WWW, //解压www
        UNZIP_WWW_ERROR, //解压www出错
        DOWNLOAD_CONFIG_FILE, //下载配置文件
        DOWNLOAD_CONFIG_FILE_ERROR, //下载配置文件出错
        DOWNLOAD_FILES_FILE, //下载路由表
        DOWNLOAD_FILES_FILE_ERROR, //下载路由表出错
        DOWNLOAD_FILES, //下载文件
        DOWNLOAD_FILES_ERROR, //下载文件出错
        UPDATE_SUCCESS
        //更新文件成功
    }

    /*更新*/
    interface UpdateCallback {
        fun completion(state: State, progress: Int)
    }

    private var developMode: Boolean? = false
    private var updateCallback: UpdateCallback? = null
    private var progress = 0
    private var isDownloadFileError: Boolean? = false
    private var downloadFilesCount = 0
    private var unzipFilesCount = 0

    private val mainRealm = Realm.getInstance(RealmConfiguration.Builder().name(dbName).build())

    private var resourceConfig: Config? = null
    private var resourceFiles: List<com.m.cenarius.Update.File>? = null
    private var cacheConfig: Config? = null
    private var cacheFiles: RealmResults<FileRealm>? = null
    private var serverConfig: Config? = null
    private var serverConfigData: ByteArray? = null
    private var serverFiles: List<com.m.cenarius.Update.File>? = null
    private var downloadFiles: List<com.m.cenarius.Update.File>? = null

    init {
        EventBus.getDefault().register(this)
    }

    private fun updateAction(callback: UpdateCallback) {
        updateCallback = callback
        // 开发模式，直接成功
        if (developMode!!) {
            complete(State.UPDATE_SUCCESS)
            return
        }

        // 重置变量
        progress = 0

        loadLocalConfig()
        loadLocalFiles()
        downloadConfig()
    }

    /*加载本地的config*/
    private fun loadLocalConfig() {
        try {
            cacheConfig = JSON.parseObject(FileUtils.readFileToString(cacheConfigUrl, "UTF-8"), Config::class.java)
        } catch (e: IOException) {
            cacheConfig = null
        }

        try {
            val inputStream = Cenarius.context.assets.open(resourceConfigUrl)
            resourceConfig = JSON.parseObject(IOUtils.toString(inputStream, "UTF-8"), Config::class.java)
        } catch (e: IOException) {
            Logger.e(e, "You must put $configName file in www folder")
        }

    }

    /*加载本地的路由表*/
    private fun loadLocalFiles() {
        cacheFiles = mainRealm.where(FileRealm::class.java).findAll()
        try {
            val inputStream = Cenarius.context.assets.open(resourceFilesUrl)
            resourceFiles = JSON.parseArray(IOUtils.toString(inputStream, "UTF-8"), com.m.cenarius.Update.File::class.java)
        } catch (e: IOException) {
            Logger.e(e, "You must put $filesName file in www folder")
        }

    }

    private fun downloadConfig() {
        complete(State.DOWNLOAD_CONFIG_FILE)
        Network.requset(serverConfigUrl, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        serverConfigData = response.body().bytes()
                        serverConfig = JSON.parseObject<Config>(serverConfigData, Config::class.java)
                        if (isWwwFolderNeedsToBeInstalled) {
                            unzipWww()
                        } else if (shouldDownloadWww()) {
                            downloadFilesFile()
                        } else {
                            complete(State.UPDATE_SUCCESS)
                        }
                    } catch (e: IOException) {
                        Logger.e(e, "downloadConfig fail")
                    }

                } else {
                    complete(State.DOWNLOAD_CONFIG_FILE_ERROR)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Logger.e(t, "downloadConfig fail")
                complete(State.DOWNLOAD_CONFIG_FILE_ERROR)
            }
        })
    }

    private fun downloadFilesFile() {
        complete(State.DOWNLOAD_FILES_FILE)
        loadLocalConfig()
        loadLocalFiles()
        Network.requset(serverFilesUrl, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        serverFiles = JSON.parseArray(response.body().string(), com.m.cenarius.Update.File::class.java)
                        downloadFiles = getDownloadFiles(serverFiles)
                        if (downloadFiles!!.size > 0) {
                            downloadFiles(downloadFiles)
                        } else {
                            saveConfig()
                            complete(State.UPDATE_SUCCESS)
                        }
                    } catch (e: IOException) {
                        Logger.e(e, "downloadFilesFile fail")
                    }

                } else {
                    complete(State.DOWNLOAD_FILES_FILE_ERROR)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Logger.e(t, "downloadFilesFile fail")
                complete(State.DOWNLOAD_FILES_FILE_ERROR)
            }
        })
    }

    private fun downloadFiles(files: List<com.m.cenarius.Update.File>) {
        val smallExecutor = SmartExecutor()
        smallExecutor.setCoreSize(maxConcurrentOperationCount)
        smallExecutor.setQueueSize(files.size)
        smallExecutor.setSchedulePolicy(SchedulePolicy.FirstInFistRun)
        smallExecutor.setOverloadPolicy(OverloadPolicy.ThrowExecption)
        downloadFilesCount = 0
        isDownloadFileError = false
        for (file in files) {
            smallExecutor.execute(Runnable {
                if (!downloadFile(file, retryConut)) {
                    smallExecutor.cancelWaitingTask(null)
                }
            })
        }
    }

    private fun downloadFile(file: com.m.cenarius.Update.File, retryConut: Int): Boolean {
        val call = Network.call(this.serverUrl + File.separator + file.path)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                FileUtils.writeByteArrayToFile(File(cacheUrl, file.path), response.body().bytes())
                downloadFileSuccess(file)
                return true
            }
        } catch (e: IOException) {
            Logger.e(e, null)
        }

        return downloadFileRetry(file, retryConut)
    }

    private fun downloadFileRetry(file: com.m.cenarius.Update.File, retryConut: Int): Boolean {
        if (retryConut > 0) {
            return downloadFile(file, retryConut - 1)
        } else {
            downloadFileError()
            return false
        }
    }

    private fun downloadFileSuccess(file: com.m.cenarius.Update.File) {
        EventBus.getDefault().post(DownloadFileSuccessEvent(file))
    }

    private fun downloadFileError() {
        EventBus.getDefault().post(DownloadFileErrorEvent())
    }

    private fun saveConfig() {
        try {
            FileUtils.writeByteArrayToFile(cacheConfigUrl, serverConfigData)
        } catch (e: IOException) {
            Logger.e(e, null)
        }

    }

    private fun saveFiles(files: List<com.m.cenarius.Update.File>) {
        mainRealm.executeTransaction { realm ->
            realm.deleteAll()
            for (file in files) {
                realm.insert(file.toRealm())
            }
        }
    }

    private fun getDownloadFiles(serverFiles: List<com.m.cenarius.Update.File>): List<com.m.cenarius.Update.File> {
        val downloadFiles = ArrayList<com.m.cenarius.Update.File>()
        for (file in serverFiles) {
            if (shouldDownload(file)) {
                downloadFiles.add(file)
            }
        }
        return downloadFiles
    }

    private fun shouldDownload(serverFile: com.m.cenarius.Update.File): Boolean {
        for (cacheFile in cacheFiles!!) {
            if (TextUtils.equals(cacheFile.path, serverFile.path) && TextUtils.equals(cacheFile.md5, serverFile.md5)) {
                return false
            }
        }
        return true
    }

    private //没有缓存或者缓存比预置低
    val isWwwFolderNeedsToBeInstalled: Boolean
        get() {
            if (cacheConfig == null || VersionUtil.compareVersion(cacheConfig!!.release, resourceConfig!!.release) < 0) {
                return true
            }
            return false
        }

    private fun unzipWww() {
        complete(State.UNZIP_WWW)
        mainRealm.executeTransaction { realm -> realm.deleteAll() }
        Thread(Runnable {
            try {
                FileUtils.deleteDirectory(cacheUrl)
                val inputStream = Cenarius.context.assets.open(resourceZipUrl)
                ZipUtil.iterate(inputStream) { `in`, zipEntry ->
                    val name = zipEntry.name
                    if (name != null) {
                        val file = File(cacheUrl, name)
                        if (zipEntry.isDirectory) {
                            org.zeroturnaround.zip.commons.FileUtils.forceMkdir(file)
                        } else {
                            org.zeroturnaround.zip.commons.FileUtils.forceMkdir(file.parentFile)
                            org.zeroturnaround.zip.commons.FileUtils.copy(`in`, file)
                            unzipFileSuccess()
                        }
                    }
                }
                unzipSuccess()
            } catch (e: IOException) {
                Logger.e(e, null)
                complete(State.UNZIP_WWW_ERROR)
            }
        }).start()
    }

    private fun unzipFileSuccess() {
        unzipFilesCount++
        var progress = unzipFilesCount * 100 / resourceFiles!!.size
        if (shouldDownloadWww()) {
            progress /= 2
        }
        if (this.progress != progress) {
            this.progress = progress
            complete(State.UNZIP_WWW)
        }
    }

    private fun shouldDownloadWww(): Boolean {
        if (hasMinVersion(serverConfig)) {
            // 满足最小版本要求
            if (isWwwFolderNeedsToBeInstalled) {
                return VersionUtil.compareVersion(serverConfig!!.release, resourceConfig!!.release) > 0
            } else {
                return VersionUtil.compareVersion(serverConfig!!.release, cacheConfig!!.release) > 0
            }
        }
        return false
    }

    private fun hasMinVersion(serverConfig: Config): Boolean {
        val versionName = VersionUtil.appVersionName
        if (versionName != null && serverConfig.android_min_version != null && VersionUtil.compareVersion(versionName, serverConfig.android_min_version) >= 0) {
            return true
        }
        return false
    }

    private fun unzipSuccess() {
        EventBus.getDefault().post(UnzipSuccessEvent())
    }

    private fun complete(state: State) {
        EventBus.getDefault().post(CompleteEvent(state))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected fun onCompleteEvent(event: CompleteEvent) {
        updateCallback!!.completion(event.state, progress)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected fun onUnzipSuccessEvent(event: UnzipSuccessEvent) {
        try {
            val inputStream = Cenarius.context.assets.open(resourceConfigUrl)
            FileUtils.copyInputStreamToFile(inputStream, cacheConfigUrl)
            saveFiles(resourceFiles)
            if (shouldDownloadWww()) {
                downloadFilesFile()
            } else {
                complete(State.UPDATE_SUCCESS)
            }
        } catch (e: IOException) {
            Logger.e(e, null)
        }

    }


    private class CompleteEvent private constructor(var state: State)

    private class UnzipSuccessEvent

    private class DownloadFileSuccessEvent private constructor(var file: com.m.cenarius.Update.File)

    private class DownloadFileErrorEvent

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected fun onDownloadFileErrorEvent(event: DownloadFileErrorEvent) {
        if ((!isDownloadFileError)!!) {
            isDownloadFileError = true
            complete(State.DOWNLOAD_FILES_ERROR)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected fun onDownloadFileSuccessEvent(event: DownloadFileSuccessEvent) {
        if (isDownloadFileError!!) {
            return
        }
        mainRealm.executeTransaction { realm -> realm.insertOrUpdate(event.file.toRealm()) }
        downloadFilesCount += 1
        val unzipProgress = progress
        val downloadProgress = downloadFilesCount * (100 - unzipProgress) / downloadFiles!!.size
        val progress = unzipProgress + downloadProgress
        if (this.progress != progress) {
            this.progress = progress
            complete(State.DOWNLOAD_FILES)
        }
        if (downloadFilesCount == downloadFiles!!.size) {
            saveConfig()
            saveFiles(serverFiles)
            complete(State.UPDATE_SUCCESS)
        }
    }

    companion object {

        fun setDevelopMode(mode: Boolean?) {
            sharedInstance.developMode = mode
        }

        fun getCacheUrl(file: String): File {
            return File(cacheUrl, file)
        }

        fun update(callback: UpdateCallback) {
            sharedInstance.updateAction(callback)
        }

        private val wwwName = "www"
        private val zipName = "www.zip"
        private val filesName = "cenarius-files.json"
        private val configName = "cenarius-config.json"
        private val dbName = "cenarius-files.realm"
        private val retryConut = 5
        private val maxConcurrentOperationCount = 2

        private val resourceUrl = wwwName
        private val resourceConfigUrl = resourceUrl + File.separator + configName
        private val resourceFilesUrl = resourceUrl + File.separator + filesName
        private val resourceZipUrl = resourceUrl + File.separator + zipName
        val cacheUrl = Cenarius.context.getDir(wwwName, Context.MODE_PRIVATE)
        private val cacheConfigUrl = File(cacheUrl, configName)
        /*设置远程资源地址*/
        var serverUrl: String? = null
            set(url) {
                field = url
                serverConfigUrl = this.serverUrl + File.separator + configName
                serverFilesUrl = this.serverUrl + File.separator + filesName
            }
        private var serverConfigUrl: String? = null
        private var serverFilesUrl: String? = null

        private val sharedInstance = UpdateManager()
    }


}
