package com.traidingviewer.di.module

import androidx.lifecycle.ViewModelProvider
import com.traidingviewer.common.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}