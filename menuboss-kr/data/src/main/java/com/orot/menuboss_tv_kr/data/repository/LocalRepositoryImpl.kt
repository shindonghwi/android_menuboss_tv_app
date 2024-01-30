package com.orot.menuboss_tv_kr.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.orot.menuboss_tv_kr.domain.repository.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(context: Context) :
    LocalRepository {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun getUUID(): String {
        return sharedPreferences.getString(KEY_UPDATED_BY_UUID, "") ?: ""
    }

    override suspend fun setUUID(uuid: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_UPDATED_BY_UUID, uuid)
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "my_preferences"
        private const val KEY_UPDATED_BY_UUID = "uuid"
    }

}