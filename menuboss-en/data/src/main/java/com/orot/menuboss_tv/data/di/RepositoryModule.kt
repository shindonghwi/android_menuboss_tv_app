package com.orot.menuboss_tv.data.di

import com.orot.menuboss_tv.data.repository.TvRepositoryImpl
import com.orot.menuboss_tv.domain.repository.TvRepository
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
