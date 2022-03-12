package com.devx.mailey.di

import android.app.Application
import com.devx.mailey.data.firebase.FirebaseService
import com.devx.mailey.data.firebase.FirebaseSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseInstance(): FirebaseService{
        return FirebaseSource()
    }
}