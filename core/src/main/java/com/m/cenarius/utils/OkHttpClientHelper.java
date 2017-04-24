package com.m.cenarius.utils;


import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class OkHttpClientHelper {

    private static OkHttpClient defaultClient;
    public static OkHttpClient getDefaultClient() {
        return getDefaultClient(false);
    }

    /**
     *
     * @param isIgnoreCard 是否忽略证书
     * @return
     */
    public static OkHttpClient getDefaultClient(boolean isIgnoreCard) {
        if (defaultClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true);

            if (isIgnoreCard){
                ignoreCard(builder);
            }

            defaultClient = builder.build();
        }

        return defaultClient;
    }

    /**
     * 忽略ssl证书
     * @param builder
     */
    private static void ignoreCard(OkHttpClient.Builder builder) {

        TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null,new TrustManager[]{tm}, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory());
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
