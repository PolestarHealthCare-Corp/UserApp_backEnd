package com.example.referralapp.di

import com.example.referralapp.data.api.ApiService
import com.example.referralapp.data.local.PrefManager
import com.example.referralapp.data.repository.UserRepository
import com.example.referralapp.data.repository.HospitalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        prefManager: PrefManager
    ): UserRepository {
        return UserRepository(apiService, prefManager)
    }

    @Provides
    @Singleton
    fun provideHospitalRepository(apiService: ApiService): HospitalRepository {
        return HospitalRepository(apiService)
    }
}