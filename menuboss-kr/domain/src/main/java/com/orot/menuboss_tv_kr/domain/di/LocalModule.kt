package com.orot.menuboss_tv_kr.domain.di

import com.orot.menuboss_tv_kr.domain.repository.LocalRepository
import com.orot.menuboss_tv_kr.domain.usecases.GetUpdatedByUuidUseCase
import com.orot.menuboss_tv_kr.domain.usecases.PatchUpdatedByUuidUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun provideGetUpdatedByUuidUseCase(localRepository: LocalRepository): GetUpdatedByUuidUseCase {
        return GetUpdatedByUuidUseCase(localRepository)
    }

    @Singleton
    @Provides
    fun providePatchUpdatedByUuidUseCase(localRepository: LocalRepository): PatchUpdatedByUuidUseCase {
        return PatchUpdatedByUuidUseCase(localRepository)
    }
}