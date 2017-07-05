package com.m.cenarius.Network

import com.m.cenarius.Extension.*
import okhttp3.MediaType
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

class Network {

    enum class HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    private interface Service {

        @GET
        fun get(@Url url: String, @QueryMap parameters: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @FormUrlEncoded
        @POST
        fun post(@Url url: String, @FieldMap parameters: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

        @POST
        fun json(@Url url: String, @Body body: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    }

    companion object {

        val client: OkHttpClient = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
        val mediaTypeJSON: MediaType = MediaType.parse(OpenApi.contentTypeValue)

        fun call(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Map<String, String> = TreeMap(), headers: Map<String, String> = TreeMap()): Call<ResponseBody> {
            val retrofit = Retrofit.Builder().baseUrl(url + File.separator).client(client).build()
            val service = retrofit.create(Service::class.java)

            val urlString = OpenApi.sign(url, parameters, headers)

            if (method == HTTPMethod.POST) {
                val value = headers[OpenApi.contentTypeKey]
                if (value != null && value.contains(OpenApi.contentTypeValue)) {
                    val body = RequestBody.create(mediaTypeJSON, parameters.toJSONString())
                    return service.json(urlString, body, headers)
                } else {
                    return service.post(urlString, parameters, headers)
                }
            } else {
                return service.get(urlString, parameters, headers)
            }
        }

        fun request(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Map<String, String> = TreeMap(), headers: Map<String, String> = TreeMap(), callback: Callback<ResponseBody>) {
            val call = call(url, method, parameters, headers)
            call.enqueue(callback)
        }
    }



//    fun requset(url: String, callback: Callback<ResponseBody>) {
//        request(url, null, null, null, callback)
//    }
//
//    fun requset(url: String, method: HTTPMethod, callback: Callback<ResponseBody>) {
//        request(url, method, null, null, callback)
//    }
//
//    fun requset(url: String, method: HTTPMethod, parameters: Map<String, String>, callback: Callback<ResponseBody>) {
//        request(url, method, parameters, null, callback)
//    }


}
