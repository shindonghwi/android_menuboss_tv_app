package com.orot.menuboss_tv.domain.di

import com.orot.menuboss_tv.domain.repository.TvRepository
import com.orot.menuboss_tv.domain.usecases.GetTvDeviceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TvModule {

    @Singleton
    @Provides
    fun provideGetTvDeviceUseCase(tvRepository: TvRepository): GetTvDeviceUseCase {
        return GetTvDeviceUseCase(tvRepository)
    }
}