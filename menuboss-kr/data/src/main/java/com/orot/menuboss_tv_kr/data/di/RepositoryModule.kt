package com.orot.menuboss_tv_kr.data.di

import com.orot.menuboss_tv_kr.data.repository.TvRepositoryImpl
import com.orot.menuboss_tv_kr.domain.repository.TvRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindTvRepository(tvRepositoryImpl: TvRepositoryImpl): TvRepository
}
