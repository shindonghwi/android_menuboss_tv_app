package com.orot.menuboss_tv_kr.domain.di

import com.orot.menuboss_tv_kr.domain.repository.TvRepository
import com.orot.menuboss_tv_kr.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv_kr.domain.usecases.GetPlaylistUseCase
import com.orot.menuboss_tv_kr.domain.usecases.GetScheduleUseCase
import com.orot.menuboss_tv_kr.domain.usecases.UpdateUuidUseCase
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

    @Singleton
    @Provides
    fun provideUpdateUuidUseCase(tvRepository: TvRepository): UpdateUuidUseCase {
        return UpdateUuidUseCase(tvRepository)
    }
}