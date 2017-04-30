package com.m.cenarius.Network;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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

    private static Retrofit retrofit = new Retrofit.Builder().client(new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()).build();

    private enum HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    private interface Service {

        @GET
        Call get(@Url String url, @QueryMap Map<String, String> parameters, @HeaderMap Map<String, String> headers);

        @FormUrlEncoded
        @POST
        Call post(@Url String url, @FieldMap Map<String, String> parameters, @HeaderMap Map<String, String> headers);



    }

    public Call requset(String url) {
        return requset(url, null, null, null);
    }

    public Call requset(String url, HTTPMethod method) {
        return requset(url, method, null, null);
    }

    public Call requset(String url, HTTPMethod method, Map<String, String> parameters) {
        return requset(url, method, parameters, null);
    }

    public Call requset(String url, HTTPMethod method, Map<String, String> parameters, Map<String, String> headers) {
        Service service = retrofit.create(Service.class);
        if (method == HTTPMethod.POST) {
            return service.post(url, parameters, headers);
        }
        else {
            return service.get(url, parameters, headers);
        }
    }
}
