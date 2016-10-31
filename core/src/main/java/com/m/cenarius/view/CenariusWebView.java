package com.m.cenarius.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.m.cenarius.Constants;
import com.m.cenarius.R;
import com.m.cenarius.utils.BusProvider;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * pull-to-refresh
 * error view
 *
 */
public class CenariusWebView extends FrameLayout implements CenariusWebViewCore.UriLoadCallback{

    public static final String TAG = "CenariusWebView";

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CenariusWebViewCore mCore;
    private CenariusErrorView mErrorView;
    private ProgressBar mProgressBar;

    private String mUri;
    private boolean mUsePage;
    private WeakReference<CenariusWebViewCore.UriLoadCallback> mUriLoadCallback = new WeakReference<CenariusWebViewCore.UriLoadCallback>(null);

    public CenariusWebView(Context context) {
        super(context);
        init();
    }

    public CenariusWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenariusWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_cenarius_webview, this, true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mCore = (CenariusWebViewCore) findViewById(R.id.webview);
        mErrorView = (CenariusErrorView) findViewById(R.id.cenarius_error_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        BusProvider.getInstance().register(this);
    }

    /**
     * 设置下拉刷新监听
     * @param listener
     */
    public void setOnRefreshListener(final OnRefreshListener listener) {
        if (null != listener) {
            mSwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    listener.onRefresh();
                }
            });
        }
    }

    /**
     * 下拉刷新颜色
     *
     * @param color
     */
    public void setRefreshMainColor(int color) {
        if (color > 0) {
            mSwipeRefreshLayout.setMainColor(color);
        }
    }

    /**
     * 启用/禁用 下拉刷新手势
     *
     * @param enable
     */
    public void enableRefresh(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    /**
     * 设置刷新
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    public WebView getWebView() {
        return mCore;
    }

    /***************************设置CenariusWebViewCore的一些方法代理****************************/

    public void setWebViewClient(CenariusWebViewClient client) {
        mCore.setWebViewClient(client);
    }

    public void setWebChromeClient(CenariusWebChromeClient client) {
        mCore.setWebChromeClient(client);
    }

    public void loadUri(String uri) {
        mCore.loadUri(uri);
        this.mUri = uri;
        this.mUsePage = true;
    }

    public void loadUri(String uri, final CenariusWebViewCore.UriLoadCallback callback) {
        this.mUri = uri;
        this.mUsePage = true;
        if (null != callback) {
            this.mUriLoadCallback = new WeakReference<CenariusWebViewCore.UriLoadCallback>(callback);
        }

        mCore.loadUri(uri, this);
    }

    public void loadPartialUri(String uri) {
        mCore.loadPartialUri(uri);
        this.mUri = uri;
        this.mUsePage = false;
    }

    public void loadPartialUri(String uri, final CenariusWebViewCore.UriLoadCallback callback) {
        this.mUri = uri;
        this.mUsePage = false;
        if (null != callback) {
            this.mUriLoadCallback = new WeakReference<CenariusWebViewCore.UriLoadCallback>(callback);
        }

        mCore.loadPartialUri(uri, this);
    }

    @Override
    public boolean onStartLoad() {
        post(new Runnable() {
            @Override
            public void run() {
                if (null == mUriLoadCallback.get() || !mUriLoadCallback.get().onStartLoad()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onStartDownloadHtml() {
        post(new Runnable() {
            @Override
            public void run() {
                if (null == mUriLoadCallback.get() || !mUriLoadCallback.get().onStartDownloadHtml()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onSuccess() {
        post(new Runnable() {
            @Override
            public void run() {
                if (null == mUriLoadCallback.get() || !mUriLoadCallback.get().onSuccess()) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onFail(final CenariusWebViewCore.RxLoadError error) {
        post(new Runnable() {
            @Override
            public void run() {
                if (null == mUriLoadCallback.get() || !mUriLoadCallback.get().onFail(error)) {
                    mProgressBar.setVisibility(View.GONE);
                    mErrorView.show(error.messsage);
                }
            }
        });
        return true;
    }

    public void destroy() {
        mSwipeRefreshLayout.removeView(mCore);
        mCore.destroy();
        mCore = null;
    }

    public void loadUrl(String url) {
        mCore.loadUrl(url);
    }

    public void loadData(String data, String mimeType, String encoding) {
        mCore.loadData(data, mimeType, encoding);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        mCore.loadUrl(url, additionalHttpHeaders);
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding,
                                    String historyUrl) {
        mCore.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void onPause() {
        mCore.onPause();
    }

    public void onResume() {
        mCore.onResume();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            onPageVisible();
        } else {
            onPageInvisible();
        }
    }

    /**
     * 自定义url拦截处理
     *
     * @param widget
     */
    public void addCenariusWidget(CenariusWidget widget) {
        if (null == widget) {
            return;
        }
        mCore.addCenariusWidget(widget);
    }

    public void onPageVisible() {
        mCore.loadUrl("javascript:window.Cenarius.Lifecycle.onPageVisible()");
    }

    public void onPageInvisible() {
        mCore.loadUrl("javascript:window.Cenarius.Lifecycle.onPageInvisible()");
    }

    @Override
    protected void onDetachedFromWindow() {
        BusProvider.getInstance().unregister(this);
        super.onDetachedFromWindow();
    }

    public void onEventMainThread(BusProvider.BusEvent event) {
        if (event.eventId == Constants.EVENT_CNRS_RETRY) {
            mErrorView.setVisibility(View.GONE);
            reload();
        } else if (event.eventId == Constants.EVENT_CNRS_NETWORK_ERROR) {
            boolean handled = false;
            CenariusWebViewCore.RxLoadError error = CenariusWebViewCore.RxLoadError.UNKNOWN;
            if (null != event.data) {
                int errorType = event.data.getInt(Constants.KEY_ERROR_TYPE);
                error = CenariusWebViewCore.RxLoadError.parse(errorType);
            }
            if (null != mUriLoadCallback && null != mUriLoadCallback.get()) {
                handled = mUriLoadCallback.get().onFail(error);
            }
            if (!handled) {
                mProgressBar.setVisibility(View.GONE);
                mErrorView.show(error.messsage);
            }
        }
    }

    /**
     * 重新加载页面
     */
    public void reload() {
        if (mUsePage) {
            mCore.loadUri(mUri, this);
        } else {
            mCore.loadPartialUri(mUri, this);
        }
    }
}
