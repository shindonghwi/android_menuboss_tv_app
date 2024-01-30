package com.orot.menuboss_tv.domain.repository

interface LocalRepository {

    suspend fun setUUID(uuid: String)

    suspend fun getUUID(): String
}