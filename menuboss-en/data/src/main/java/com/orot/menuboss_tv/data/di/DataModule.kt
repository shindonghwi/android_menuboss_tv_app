package com.orot.menuboss_tv.data.di

import android.content.Context
import com.orot.menuboss_tv.data.repository.LocalRepositoryImpl
import com.orot.menuboss_tv.data.repository.ScreenEventsRepositoryImpl
import com.orot.menuboss_tv.data.services.GrpcScreenEventClient
import com.orot.menuboss_tv.data.services.TvApi
import com.orot.menuboss_tv.domain.constants.BASE_URL
import com.orot.menuboss_tv.domain.repository.LocalRepository
import com.orot.menuboss_tv.domain.repository.ScreenEventsRepository
import com.orot.menuboss_tv.utils.DeviceInfoUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRetrofit(deviceInfoUtil: DeviceInfoUtil): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(deviceInfoUtil))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    class AppInterceptor @Inject constructor(
        private val deviceInfoUtil: DeviceInfoUtil
    ): Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response =
            with(chain) {

                val currentLocale = Locale.getDefault()
                val currentTimeZone = TimeZone.getDefault().id

                val newRequest = request().newBuilder().apply {
                    addHeader("Accept-Language", "${currentLocale.language}-${currentLocale.country}")
                    addHeader("Application-Time-Zone", currentTimeZone)
                    if (deviceInfoUtil.isAmazonDevice()){
                        addHeader("x-client-id", "MSGZ")
                    }else{
                        addHeader("x-client-id", "MSGA")
                    }
                }.build()
                proceed(newRequest)
            }
    }

    private fun provideOkHttpClient(deviceInfoUtil: DeviceInfoUtil): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(AppInterceptor(deviceInfoUtil))
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            connectTimeout(100, TimeUnit.SECONDS)
            readTimeout(300, TimeUnit.SECONDS)
            writeTimeout(300, TimeUnit.SECONDS)
            build()
        }

    @Provides
    fun provideTvApi(retrofit: Retrofit): TvApi =
        retrofit.create(TvApi::class.java)

    @Provides
    @Singleton
    fun provideGrpcScreenEventClient(deviceInfoUtil: DeviceInfoUtil) = GrpcScreenEventClient(deviceInfoUtil)

    @Provides
    fun provideScreenEventsRepository(grpcClient: GrpcScreenEventClient): ScreenEventsRepository =
        ScreenEventsRepositoryImpl(grpcClient)

    @Provides
    @Singleton
    fun provideLocalRepository(@ApplicationContext context: Context): LocalRepository {
        return LocalRepositoryImpl(context)
    }

}