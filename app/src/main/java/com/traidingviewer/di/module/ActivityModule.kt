package com.traidingviewer.di.module

import android.app.Activity
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule::class])
class ActivityModule(private var activity: Activity) {
    @Provides
    fun provideActivity(): Activity {
        return activity
    }
}