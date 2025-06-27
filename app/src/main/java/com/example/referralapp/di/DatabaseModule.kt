package com.example.referralapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.referralapp.data.local.PrefManager
import com.example.referralapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


/**
 * Database 및 Local Storage 관련 dependency를 제공하는 Dagger Hilt Module
 * 이 Module은 App 전체에서 단일 instance를 보장하기 위해 SingletonComponent에 설치됨
 */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 민감하지 않은 Data 저장을 위한 일반 SharedPreferences를 제공
     *
     * @param context Application Context
     * @return 일반 App 설정을 위한 SharedPreferences Instance
     */

    @Provides
    @Singleton
    @Named("standard_prefs")
    fun provideStandardSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            Constants.SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    /**
     * 민감한 데이터 저장을 위한 암호화된 SharedPreferences를 제공
     * Android Security Library를 사용하여 암호화 합니다.
     *
     * @param context Application Context
     * @return 민감한 Data를 위한 암호화된 SharedPreferences Instance
     */
    @Provides
    @Singleton
    @Named("encrypted_prefs")
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            EncryptedSharedPreferences.create(
                Constants.ENCRYPTED_PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // 암호화 실패 시 일반 SharedPreferences Instance를 반환
            context.getSharedPreferences(
                "${Constants.ENCRYPTED_PREFS_NAME}_fallback",
                Context.MODE_PRIVATE
            )
        }
    }

    /**
     * 일반 및 암호화된 SharedPreferences를 모두 가진 PrefManager Instance를 제공
     *
     * @param standardPrefs 일반 Data를 위한 표준 SharedPreferences Instance
     * @param encryptedPrefs 민감한 Data를 위한 암호화된 SharedPreferences Instance
     * @return 두 가지 설정 Type으로 구성된 PrefManager Instance
     */

    @Provides
    @Singleton
    fun providePrefManager(
        @Named("standard_prefs") standardPrefs: SharedPreferences,
        @Named("encrypted_prefs") encryptedPrefs: SharedPreferences
    ): PrefManager {
        return PrefManager(
            standardPrefs = standardPrefs,
            encryptedPrefs = encryptedPrefs
        )
    }
}