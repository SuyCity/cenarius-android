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

enum class HTTPMethod {
    GET,
    POST,
    PUT,
    DELETE
}

typealias Parameters = Map<String, Any>
typealias HTTPHeaders = Map<String, String>

open class Network {

    private interface Service {

        @GET
        fun methodGet(@Url url: String, @QueryMap parameters: Parameters, @HeaderMap headers: HTTPHeaders): Call<ResponseBody>

        @FormUrlEncoded
        @POST
        fun methodPost(@Url url: String, @FieldMap parameters: Parameters, @HeaderMap headers: HTTPHeaders): Call<ResponseBody>

        @POST
        fun methodJson(@Url url: String, @Body body: RequestBody, @HeaderMap headers: HTTPHeaders): Call<ResponseBody>

    }

    companion object {

        val client: OkHttpClient = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).retryOnConnectionFailure(true).build()
        val mediaTypeJSON: MediaType = MediaType.parse(OpenApi.contentTypeValue)

        fun call(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Parameters? = null, headers: HTTPHeaders? = null): Call<ResponseBody> {
            val p = parameters ?: TreeMap()
            val h = headers ?: TreeMap()

            val retrofit = Retrofit.Builder().baseUrl(url + File.separator).client(client).build()
            val service = retrofit.create(Service::class.java)

            val urlString = OpenApi.sign(url, parameters, headers)

            if (method == HTTPMethod.POST) {
                val value = h[OpenApi.contentTypeKey]
                if (value != null && value.contains(OpenApi.contentTypeValue)) {
                    val body = RequestBody.create(mediaTypeJSON, p.toJSONString())
                    return service.methodJson(urlString, body, h)
                } else {
                    return service.methodPost(urlString, p, h)
                }
            } else {
                return service.methodGet(urlString, p, h)
            }
        }

        fun request(url: String, method: HTTPMethod = HTTPMethod.GET, parameters: Parameters? = TreeMap(), headers: HTTPHeaders? = TreeMap(), callback: Callback<ResponseBody>) {
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
