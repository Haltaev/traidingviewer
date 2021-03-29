package com.traidingviewer.di.component

import dagger.Component
import com.traidingviewer.di.module.AppModule
import com.traidingviewer.di.module.NetworkModule
import com.traidingviewer.di.module.VMModule
import com.traidingviewer.di.module.ViewModelModule
import com.traidingviewer.ui.BaseActivity
import com.traidingviewer.ui.BaseFragment
import com.traidingviewer.ui.home.HomeFragment
import com.traidingviewer.ui.home.viewpagers.favorites.FavouriteFragment
import com.traidingviewer.ui.home.viewpagers.stock.StockFragment
import com.traidingviewer.ui.info.TickerInfoFragment
import com.traidingviewer.ui.search.SearchFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ViewModelModule::class, VMModule::class])
interface AppComponent {
    fun inject(application: BaseActivity)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: StockFragment)
    fun inject(fragment: FavouriteFragment)
    fun inject(fragment: SearchFragment)
    fun inject(baseFragment: BaseFragment)
    fun inject(tickerInfoFragment: TickerInfoFragment)
}