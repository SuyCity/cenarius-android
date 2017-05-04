package com.m.cenarius.Network;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by m on 2017/4/30.
 */

public class Network {

//    private static Retrofit retrofit = new Retrofit.Builder().client(new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()).build();

    private enum HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    private interface Service {

        @GET
        Call<ResponseBody> get(@Url String url, @QueryMap Map<String, String> parameters, @HeaderMap Map<String, String> headers);

        @FormUrlEncoded
        @POST
        Call<ResponseBody> post(@Url String url, @FieldMap Map<String, String> parameters, @HeaderMap Map<String, String> headers);



    }

    public static Call<ResponseBody> requset(String url) {
        return requset(url, null, null, null);
    }

    public static Call<ResponseBody> requset(String url, HTTPMethod method) {
        return requset(url, method, null, null);
    }

    public static Call<ResponseBody> requset(String url, HTTPMethod method, Map<String, String> parameters) {
        return requset(url, method, parameters, null);
    }

    public static Call<ResponseBody> requset(String url, HTTPMethod method, Map<String, String> parameters, Map<String, String> headers) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url + File.separator).client(client).build();
        Service service = retrofit.create(Service.class);

        Map<String, String> p = parameters;
        if (p == null) {
            p = new TreeMap<>();
        }
        Map<String, String> h = headers;
        if (h == null) {
            h = new TreeMap<>();
        }
        if (method == HTTPMethod.POST) {
            return service.post(url, p, h);
        }
        else {
            return service.get(url, p, h);
        }
    }
}
