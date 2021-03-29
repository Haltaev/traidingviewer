package com.traidingviewer.di.module

import android.content.Context
import com.google.gson.Gson
import com.traidingviewer.App
import com.traidingviewer.data.db.dao.PopularTickerDao
import com.traidingviewer.data.db.dao.SearchedTickersDao
import com.traidingviewer.data.db.dao.SymbolDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: App) {
    @Provides
    internal fun provideContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun favoritesDao(): SymbolDao {
        return application.favorites
    }

    @Provides
    @Singleton
    fun popularTickerDao(): PopularTickerDao {
        return application.popularTickers
    }

    @Provides
    @Singleton
    fun searchedTickersDao(): SearchedTickersDao {
        return application.searchedTickers
    }

}