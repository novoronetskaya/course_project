package com.course_project.voronetskaya.barcode

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class BarcodeApiCaller {
    private val api = "https://ean-db.com/api/v1/product/"
    private val authorizationHeader = "Authorization"
    private val authorizationKey =
        ""

    public fun getDataByCode(code: String): Product? {
        val client = OkHttpClient()
        val url =
            HttpUrl.parse(api + code)!!.newBuilder().build()
        val request = Request.Builder().url(url).addHeader(authorizationHeader, authorizationKey).build()
        val response = client.newCall(request).execute()

        if (response.code() != 200 || response.body() == null) {
            return null
        }

        val resp = Gson().fromJson(response.body()!!.string(), BarcodeResponse::class.java)
        return resp.product
    }
}