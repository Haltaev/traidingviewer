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
        const val API_KEY = "71879ea95772d7359d261d39ee2e4453"
    }
}