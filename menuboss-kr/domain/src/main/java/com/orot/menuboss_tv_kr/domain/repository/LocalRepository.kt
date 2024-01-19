package com.orot.menuboss_tv_kr.domain.repository

interface LocalRepository {

    suspend fun setUUID(uuid: String)

    suspend fun getUUID(): String
}