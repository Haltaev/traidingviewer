package com.traidingviewer

import android.app.Application
import com.traidingviewer.data.db.PopularTickersDatabase
import com.traidingviewer.data.db.SearchedTickersDatabase
import com.traidingviewer.data.db.StockDatabase
import com.traidingviewer.di.component.AppComponent
import com.traidingviewer.di.component.DaggerAppComponent
import com.traidingviewer.di.module.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val stockDatabase by lazy { StockDatabase.getDatabase(this) }
    private val popularTickersDatabase by lazy {
        PopularTickersDatabase.getDatabase(
            this,
            applicationScope
        )
    }
    private val searchedTickersDatabase by lazy { SearchedTickersDatabase.getDatabase(this) }
    val favorites by lazy { stockDatabase.symbolDao() }
    val popularTickers by lazy { popularTickersDatabase.popularTickerDao() }
    val searchedTickers by lazy { searchedTickersDatabase.searchedTickersDao() }

    companion object {
        private lateinit var component: AppComponent
        private lateinit var app: App

        fun getComponent(): AppComponent {
            return component
        }

        fun buildAppComponent() {
            component = DaggerAppComponent.builder()
                .appModule(AppModule(app))
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        buildAppComponent()
    }
}