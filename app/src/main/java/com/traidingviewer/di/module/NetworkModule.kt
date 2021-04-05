package com.traidingviewer.di.module

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import com.traidingviewer.data.api.ApiService
import com.traidingviewer.data.api.interceptor.RequestInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val REQUEST_TIMEOUT = 120.toLong()
const val MAX_CACHE_SIZE = (10 * 1024 * 1024).toLong()

@Module(includes = [AppModule::class])
class NetworkModule {
    @Provides
    @Singleton
    fun provideInterceptor(): RequestInterceptor {
        return RequestInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        context: Context,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {
        val cache = Cache(context.cacheDir, MAX_CACHE_SIZE)

        val builder = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .addInterceptor(ChuckInterceptor(context))
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .cache(cache)

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRestAdapter(okHttpClient: OkHttpClient): Retrofit {
        val builder = Retrofit.Builder()
        builder.client(okHttpClient)
            .baseUrl("https://financialmodelingprep.com/")
            .addConverterFactory(GsonConverterFactory.create())
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideApiService(restAdapter: Retrofit): ApiService {
        return restAdapter.create<ApiService>(ApiService::class.java)
    }
}