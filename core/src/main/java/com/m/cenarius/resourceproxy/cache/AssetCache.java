package com.m.cenarius.resourceproxy.cache;

import com.m.cenarius.Constants;
import com.m.cenarius.route.Route;

/**
 * 预置到asset中的只读cache
 */
public class AssetCache {

    public static final String TAG = "AssetCache";

    private static AssetCache sInstance;

    public static AssetCache getInstance() {
        if (null == sInstance) {
            synchronized (AssetCache.class) {
                if (null == sInstance) {
                    sInstance = new AssetCache();
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
//        //读取资源文件夹routes
//        try {
//            if (RouteManager.getInstance().resourceRoutes != null) {
//                for (Route presetRoute : RouteManager.getInstance().resourceRoutes) {
//                    if (presetRoute.equals(route)) {
//                        //资源文件路径
//                        String pathString = filePath(presetRoute.file);
//                        AssetManager assetManager = AppContext.getInstance().getResources().getAssets();
//                        try {
//                            InputStream inputStream = assetManager.open(pathString);
//                            CacheEntry cacheEntry = new CacheEntry(0, inputStream);
//                            LogUtils.i(TAG, "hit");
//                            return cacheEntry;
//                        } catch (IOException e) {
//
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LogUtils.i(TAG, e.getMessage());
//        }
//        return null;
//    }

//    public CacheEntry findWhiteListCache(String uri){
//        //资源文件路径
//        String pathString = filePath(uri);
//        AssetManager assetManager = AppContext.getInstance().getResources().getAssets();
//        try {
//            InputStream inputStream = assetManager.open(pathString);
//            CacheEntry cacheEntry = new CacheEntry(0, inputStream);
//            return cacheEntry;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    private String filePath(String uri) {
        return Constants.DEFAULT_ASSET_FILE_PATH + "/" + uri;
    }

    /**
     * 单个存储文件路径
     */
    public String fileUrl(Route route) {
        return assetsPath() + filePath(route.file);
    }

    public String fileUrl(String uri) {
        return assetsPath() + filePath(uri);
    }

    /**
     * 获取assets目录
     */
    public String assetsPath() {
        return "file:///android_asset/";
    }

    /**
     * 获取www目录
     */
    public String wwwAssetsPath(){
        String assetsPath = assetsPath() + Constants.DEFAULT_ASSET_FILE_PATH;
        return assetsPath;
    }

//    @Override
//    public boolean removeCache(Route route) {
//        // do nothing
//        return true;
//    }

}
