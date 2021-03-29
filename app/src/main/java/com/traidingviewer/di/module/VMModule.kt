package com.traidingviewer.di.module

import androidx.lifecycle.ViewModel
import com.traidingviewer.di.ViewModelKey
import com.traidingviewer.ui.home.viewpagers.favorites.FavoriteViewModel
import com.traidingviewer.ui.home.viewpagers.stock.StockViewModel
import com.traidingviewer.ui.info.chart.ChartViewModel
import com.traidingviewer.ui.info.news.NewsViewModel
import com.traidingviewer.ui.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class VMModule {
    @Binds
    @IntoMap
    @ViewModelKey(StockViewModel::class)
    internal abstract fun stockViewModel(viewModel: StockViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun searchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel::class)
    internal abstract fun favoriteViewModel(viewModel: FavoriteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    internal abstract fun newsViewModel(viewModel: NewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChartViewModel::class)
    internal abstract fun chartViewModel(viewModel: ChartViewModel): ViewModel
}
