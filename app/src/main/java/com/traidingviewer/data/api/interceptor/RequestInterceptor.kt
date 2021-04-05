package com.traidingviewer.data.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .build()

        return chain.proceed(request)
    }

    companion object {
        const val API_KEY = "10e368b6d26392459d78bb4d21b4d4f3"
    }
}