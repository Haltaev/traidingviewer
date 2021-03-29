package com.traidingviewer.data.api

import com.traidingviewer.data.api.interceptor.RequestInterceptor.Companion.API_KEY
import com.traidingviewer.data.api.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/v3/stock/list")
    suspend fun getStocks(
        @Query("apikey") apiKey: String = API_KEY
    ): Response<List<StockResponse>>

    @GET("/api/v3/search")
    suspend fun searchStocks(
        @Query("query") query: String,
        @Query("limit") limit: Int = 6,
        @Query("apikey") apiKey: String = API_KEY
    ): Response<List<StockResponse>>

    @GET("/api/v3/profile/{symbol}")
    suspend fun getCompanyProfile(
        @Path("symbol") symbol: String,
        @Query("apikey") apiKey: String = API_KEY
    ): Response<List<CompanyProfileResponse>>

    @GET("/api/v3/stock_news")
    suspend fun getCompanyNews(
        @Query("tickers") tickers: String,
        @Query("limit") limit: Int = 20,
        @Query("apikey") apiKey: String = API_KEY
    ): Response<List<CompanyNewsResponse>>

    @GET("/api/v3/historical-chart/{time}/{symbol}")
    suspend fun getCharts(
        @Path("time") time: String,
        @Path("symbol") symbol: String,
        @Query("apikey") apiKey: String = API_KEY
    ): Response<List<ChartsResponse>>

    @GET("/api/v3/historical-price-full/{symbol}")
    suspend fun getChartsDaily(
        @Path("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("apikey") apiKey: String = API_KEY
    ): Response<ChartsDailyResponse>

    @GET("/api/v3/historical-price-full/{symbol}")
    suspend fun getChartsDaily(
        @Path("symbol") symbol: String,
        @Query("apikey") apiKey: String = API_KEY
    ): Response<ChartsDailyResponse>
}