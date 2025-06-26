package com.example.referralapp.utils

/**
 * Application 전체에서 사용하는 상수들을 관리하는 객체
 */

object Constants {
    // SharedPreferences File Name
    const val SHARED_PREFS_NAME = "referral_app_prefs"
    const val ENCRYPTED_PREFS_NAME = "referral_app_encrypted_prefs"

    // 기본 설정 키값들
    const val PREF_USER_TOKEN = "user_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_IS_FIRST_LAUNCH = "is_first_launch"
    const val PREF_REFERRAL_CODE = "referral_code"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREFLANGUAGE = "language"
    const val PREF_NOTIFICATION_ENABLED = "notification_enabled"
}