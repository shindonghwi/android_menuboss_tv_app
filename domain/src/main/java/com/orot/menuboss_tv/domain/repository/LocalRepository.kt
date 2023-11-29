package com.orot.menuboss_tv.domain.repository

interface LocalRepository {

    suspend fun setUpdatedByUUID(isUpdated: Boolean)

    suspend fun getUpdatedByUUID(): Boolean
}