package com.m.cenarius.resourceproxy.cache;

import android.content.Context;

import com.m.cenarius.Constants;
import com.m.cenarius.route.Route;
import com.m.cenarius.utils.AppContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 缓存资源文件
 * <p>
 * 存储位置默认在/data/data/www下
 */

public class InternalCache {

    public static final String TAG = "InternalCache";

    private static InternalCache sInstance;
    private File fileDir;

    public static InternalCache getInstance() {
        if (null == sInstance) {
            synchronized (InternalCache.class) {
                if (null == sInstance) {
                    sInstance = new InternalCache();
                }
            }
        }
        return sInstance;
    }

//    @Override
//    public CacheEntry findCache(Route route) {
//        if (route == null) {
//            return null;
//        }
//        File file = file(route);
//        if (file.exists() && file.canRead()) {
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                byte[] bytes = IOUtils.toByteArray(fileInputStream);
//                CacheEntry cacheEntry = new CacheEntry(file.length(), new ByteArrayInputStream(bytes));
//                fileInputStream.close();
//                LogUtils.i(TAG, "hit");
//                return cacheEntry;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    /**
     * 删除单个资源缓存
     *
     * @param route 资源地址
     */
    public boolean removeCache(Route route) {
        File file = file(route);
        return file.exists() && file.delete();
    }

    /**
     * 保存文件缓存
     *
     * @param route route
     * @param bytes 数据
     */
    public boolean saveCache(Route route, byte[] bytes) {
        if (null == bytes) {
            return false;
        }
        // 如果存在，则先删掉之前的缓存
        removeCache(route);
        File saveFile = null;
        try {
            saveFile = file(route);
            File fileDir = saveFile.getParentFile();
            if (!fileDir.exists()) {
                if (!fileDir.mkdirs()) {
                    return false;
                }
            }
            OutputStream outputStream = new FileOutputStream(saveFile);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建文件夹
     */
    public void createWWW() {
        File htmlDir = fileDir();
        if (!htmlDir.exists()) {
            htmlDir.mkdirs();
        }
    }

    /**
     * 清除缓存
     *
     * @return whether clear cache successfully
     */
    public boolean clearWWW() {
        File htmlDir = fileDir();
        if (!htmlDir.exists()) {
            return true;
        }
        File[] htmlFiles = htmlDir.listFiles();
        if (null == htmlFiles) {
            return true;
        }
        boolean processed = true;
        for (File file : htmlFiles) {
            if (!file.delete()) {
                processed = false;
            }
        }
        return processed;
    }

    /**
     * www存储目录
     *
     * @return 存储目录
     */
    public File fileDir() {
        if (fileDir == null) {
//            fileDir = new File(AppContext.getInstance().getDir(Constants.CACHE_HOME_DIR, Context.MODE_PRIVATE), Constants.DEFAULT_DISK_INTERNAL_FILE_PATH);
            fileDir = new File(wwwCachePath());
        }
        return fileDir;
    }

    /**
     * 单个存储文件路径
     *
     * @param route route
     * @return html对应的存储文件
     */
    public File file(Route route) {
        return new File(fileDir(), route.file);
    }

    /**
     * 获取缓存目录
     */
    public String cachePath() {
        return AppContext.getInstance().getDir(Constants.CACHE_HOME_DIR,
                Context.MODE_PRIVATE).getPath() + "/";
    }

    /**
     * 获取www目录
     */
    public String wwwCachePath() {
        String cachePath = cachePath() + Constants.DEFAULT_ASSET_FILE_PATH;
        return cachePath;
    }

}
