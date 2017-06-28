package com.m.cenarius.Network;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.Body;
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

    public enum HTTPMethod {
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

        @POST
        Call<ResponseBody> json(@Url String url, @Body RequestBody body, @HeaderMap Map<String, String> headers);

    }

    public static Call<ResponseBody> call(String url) {
        return call(url, null, null, null);
    }

    public static void requset(String url, Callback<ResponseBody> callback) {
        request(url, null, null, null, callback);
    }

    public static Call<ResponseBody> call(String url, HTTPMethod method) {
        return call(url, method, null, null);
    }

    public static void requset(String url, HTTPMethod method, Callback<ResponseBody> callback) {
        request(url, method, null, null, callback);
    }

    public static Call<ResponseBody> call(String url, HTTPMethod method, Map<String, String> parameters) {
        return call(url, method, parameters, null);
    }

    public static void requset(String url, HTTPMethod method, Map<String, String> parameters, Callback<ResponseBody> callback) {
        request(url, method, parameters, null, callback);
    }

    public static Call<ResponseBody> call(String url, HTTPMethod method, Map<String, String> parameters, Map<String, String> headers) {
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

        String u = OpenApi.sign(url, parameters, headers);

        if (method == HTTPMethod.POST) {
            return service.post(u, p, h);
        }
        else {
            return service.get(u, p, h);
        }
    }

    public static void request(String url, HTTPMethod method, Map<String, String> parameters, Map<String, String> headers, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = call(url, method, parameters, headers);
        call.enqueue(callback);
    }
}
