package com.devx.mailey.di

import com.devx.mailey.data.firebase.AuthService
import com.devx.mailey.data.firebase.DatabaseService
import com.devx.mailey.data.firebase.impl.FirebaseSource
import com.devx.mailey.data.firebase.StorageService
import com.devx.mailey.data.repository.DatabaseRepository
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
    fun provideFirebaseAuthInstance(): AuthService {
        return FirebaseSource
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageInstance(): StorageService {
        return FirebaseSource
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabaseInstance(): DatabaseService {
        return FirebaseSource
    }

//    @Provides
//    @Singleton
//    fun provideAuthRepository(authService: AuthService): AuthRepository{
//        return AuthRepository(authService)
//    }
    @Provides
    @Singleton
    fun provideDatabaseRepository(databaseService: DatabaseService): DatabaseRepository {
        return DatabaseRepository(databaseService)
    }


}
