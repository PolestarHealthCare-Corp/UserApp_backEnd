package com.example.referralapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.referralapp.data.local.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("referral_app_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePrefManager(sharedPreferences: SharedPreferences): PrefManager {
        return PrefManager(sharedPreferences)
    }
}