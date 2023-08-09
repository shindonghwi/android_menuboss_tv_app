package com.orot.menuboss_tv.data.di

import com.orot.menuboss_tv.data.mapper.DeviceInfoMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideDeviceInfoMapper(): DeviceInfoMapper = DeviceInfoMapper()

}