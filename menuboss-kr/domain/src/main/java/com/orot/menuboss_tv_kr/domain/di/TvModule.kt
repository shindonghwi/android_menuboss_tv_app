package com.orot.menuboss_tv_kr.domain.di

import com.orot.menuboss_tv_kr.domain.repository.TvRepository
import com.orot.menuboss_tv_kr.domain.usecases.remote.GetDeviceUseCase
import com.orot.menuboss_tv_kr.domain.usecases.remote.GetPlaylistUseCase
import com.orot.menuboss_tv_kr.domain.usecases.remote.GetScheduleUseCase
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
    fun provideGetTvDeviceUseCase(tvRepository: TvRepository): GetDeviceUseCase {
        return GetDeviceUseCase(tvRepository)
    }

    @Singleton
    @Provides
    fun provideGetScheduleUseCase(tvRepository: TvRepository): GetScheduleUseCase {
        return GetScheduleUseCase(tvRepository)
    }

    @Singleton
    @Provides
    fun provideGetPlaylistUseCase(tvRepository: TvRepository): GetPlaylistUseCase {
        return GetPlaylistUseCase(tvRepository)
    }
}