package com.orot.menuboss_tv

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeConnectStreamUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeContentStreamUseCase
import com.orot.menuboss_tv.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.utils.DeviceInfoUtil
import com.orot.menuboss_tv.utils.coroutineScopeOnDefault
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val subscribeConnectStreamUseCase: SubscribeConnectStreamUseCase,
    private val subscribeContentStreamUseCase: SubscribeContentStreamUseCase,
    private val deviceInfoUtil: DeviceInfoUtil,
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
    private val getDeviceUseCase: GetDeviceUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    /**
     * @feature: 디바이스의 고유한 식별자입니다.
     * @author: 2023/10/03 11:37 AM donghwishin
    */
    private var uuid: String = ""

    /**
     * @feature: 컨텐츠 스트림의 상태를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
    */
    private val _contentStatus =
        MutableStateFlow<Resource<ContentEventResponse.ContentEvent>?>(null)
    val contentStatus: MutableStateFlow<Resource<ContentEventResponse.ContentEvent>?> get() = _contentStatus

    /**
     * @feature: 연결 스트림의 상태를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
    */
    private val _connectionStatus =
        MutableStateFlow<Resource<ConnectEventResponse.ConnectEvent>?>(null)
    val connectionStatus: StateFlow<Resource<ConnectEventResponse.ConnectEvent>?> get() = _connectionStatus

    /**
     * @feature: 디바이스 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
    */
    private val _authState =
        MutableStateFlow<DeviceModel?>(null)
    val authState: StateFlow<DeviceModel?> get() = _authState

    init {
        uuid = getXUniqueId()
    }

    /**
     * @feature: GRPC 연결 스트림을 구독합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
    */
    fun subscribeConnectStream() {
        try {
            coroutineScopeOnDefault {
                subscribeConnectStreamUseCase(uuid).collect { response ->
                    _connectionStatus.value = response
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in subscribeConnectStream: ${e.message}")
        }
    }

    /**
     * @feature: GRPC 컨텐츠 스트림을 구독합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
    */
    fun subscribeContentStream(accessToken: String) {
        try {
            coroutineScopeOnDefault {
                subscribeContentStreamUseCase(accessToken).collect { response ->
                    _contentStatus.value = response
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in subscribeContentStream: ${e.message}")
        }
    }


    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
    */
    suspend fun requestGetDeviceInfo() {
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
            hashMapOf("uuid" to uuid)
        )

        getDeviceUseCase(uuid).onEach {
            if (it is Resource.Success) {
                _authState.value = it.data
            } else {
                _authState.value = null
            }
        }.launchIn(viewModelScope)
    }

    /**
     * @feature: 디바이스의 고유한 식별자를 생성합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
    */
    private fun getXUniqueId(): String {
        deviceInfoUtil.run {
            val uuid1 = generateUniqueUUID(
                getMacAddress(),
                "${Build.PRODUCT}${Build.BRAND}${Build.HARDWARE}"
            )
            val uuid2 = generateUniqueUUID(
                getMacAddress(),
                "${Build.MANUFACTURER}${Build.MODEL}${Build.DEVICE}"
            )
            val uuid3 = generateUniqueUUID(getMacAddress(), Build.FINGERPRINT)
            uuid =
                generateUniqueUUID(uuid1.toString(), "$uuid2$uuid3").toString()
        }
        Log.w(TAG, "getXUniqueId: $uuid", )
        return uuid
    }
}
