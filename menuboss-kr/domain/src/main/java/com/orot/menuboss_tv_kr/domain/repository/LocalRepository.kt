package com.orot.menuboss_tv_kr.domain.repository

interface LocalRepository {

    suspend fun setUpdatedByUUID(isUpdated: Boolean)

    suspend fun getUpdatedByUUID(): Boolean
}