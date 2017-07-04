package com.m.cenarius.Network

import java.io.File
import java.util.TreeMap
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

/**
 * Created by m on 2017/4/30.
 */

object Network {

    //    private static Retrofit retrofit = new Retrofit.Builder().client(new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()).build();

    enum class HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    private interface Service {

        @GET
        operator fun get(@Url url: String, @QueryMap parameters: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @FormUrlEncoded
        @POST
        fun post(@Url url: String, @FieldMap parameters: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @POST
        fun json(@Url url: String, @Body body: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    }

    fun requset(url: String, callback: Callback<ResponseBody>) {
        request(url, null, null, null, callback)
    }

    fun requset(url: String, method: HTTPMethod, callback: Callback<ResponseBody>) {
        request(url, method, null, null, callback)
    }

    fun requset(url: String, method: HTTPMethod, parameters: Map<String, String>, callback: Callback<ResponseBody>) {
        request(url, method, parameters, null, callback)
    }

    @JvmOverloads fun call(url: String, method: HTTPMethod? = null, parameters: Map<String, String>? = null, headers: Map<String, String>? = null): Call<ResponseBody> {
        val client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
        val retrofit = Retrofit.Builder().baseUrl(url + File.separator).client(client).build()
        val service = retrofit.create(Service::class.java)

        var p = parameters
        if (p == null) {
            p = TreeMap<String, String>()
        }
        var h = headers
        if (h == null) {
            h = TreeMap<String, String>()
        }

        val u = OpenApi.sign(url, parameters, headers)

        if (method == HTTPMethod.POST) {
            return service.post(u, p, h)
        } else {
            return service[u, p, h]
        }
    }

    fun request(url: String, method: HTTPMethod?, parameters: Map<String, String>?, headers: Map<String, String>?, callback: Callback<ResponseBody>) {
        val call = call(url, method, parameters, headers)
        call.enqueue(callback)
    }
}
