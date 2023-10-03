package com.orot.menuboss_tv

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceInfo
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetTvDeviceUseCase
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
    private val getTvDeviceUseCase: GetTvDeviceUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private var uuid: String = ""

    private val _contentStatus =
        MutableStateFlow<Resource<ContentEventResponse.ContentEvent>?>(null)
    val contentStatus: MutableStateFlow<Resource<ContentEventResponse.ContentEvent>?> get() = _contentStatus

    private val _connectionStatus =
        MutableStateFlow<Resource<ConnectEventResponse.ConnectEvent>?>(null)
    val connectionStatus: StateFlow<Resource<ConnectEventResponse.ConnectEvent>?> get() = _connectionStatus

    private val _authState =
        MutableStateFlow<DeviceInfo?>(null)
    val authState: StateFlow<DeviceInfo?> get() = _authState

    init {
        uuid = getXUniqueId()
    }

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


    suspend fun requestGetDeviceInfo() {
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
            hashMapOf("uuid" to uuid)
        )

        getTvDeviceUseCase(uuid).onEach {
            if (it is Resource.Success) {
                _authState.value = it.data
            } else {
                _authState.value = null
            }
        }.launchIn(viewModelScope)
    }

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
