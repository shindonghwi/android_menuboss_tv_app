package com.orot.menuboss_tv_kr.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.orot.menuboss_tv_kr.domain.repository.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(context: Context) : LocalRepository {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun getUpdatedByUUID(): Boolean {
        return sharedPreferences.getBoolean(KEY_UPDATED_BY_UUID, false)
    }
    override suspend fun setUpdatedByUUID(isUpdated: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_UPDATED_BY_UUID, isUpdated)
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "my_preferences"
        private const val KEY_UPDATED_BY_UUID = "updated_by_uuid"
    }

}