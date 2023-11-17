package com.orot.menuboss_tv.ui.screens.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeConnectStreamUseCase
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val subscribeConnectStreamUseCase: SubscribeConnectStreamUseCase,
    private val getDeviceUseCase: GetDeviceUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    /**
     * @feature: QR code, QR Url 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private val _pairCodeInfo = MutableStateFlow<UiState<Pair<String, String>>>(UiState.Idle)
    val pairCodeInfo: StateFlow<UiState<Pair<String, String>>> get() = _pairCodeInfo

    /**
     * @feature: accessToken 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    var accessToken: String = ""

    /**
     * @feature: 서버 연결 상태 체크. 연결되어 있으면 true, 연결되어 있지 않으면 false 입니다.
     */
    private var isConnectSteamConnected = false

    suspend fun startProcess(uuid: String) {
        Log.w(TAG, "startProcess: $uuid")
        _pairCodeInfo.emit(UiState.Loading)

        if (isConnectSteamConnected) {
            requestGetDeviceInfo(uuid)
        } else {
            subscribeConnectStream(uuid)
        }
    }

    private var currentConnectStreamJob: Job? = null
    private var deviceApiJob: Job? = null

    /**
     * @feature: GRPC Connect Stream을 구독합니다.
     * @author: 2023/11/06 9:49 AM donghwishin
     */
    private suspend fun subscribeConnectStream(uuid: String) {
        Log.w(TAG, "subscribeConnectStream: START: $uuid")
        // 기존 코루틴이 실행 중이라면 취소
        currentConnectStreamJob?.cancel()

        currentConnectStreamJob = viewModelScope.launch {
            try {
                subscribeConnectStreamUseCase(uuid).collect { response ->
                    Log.w(TAG, "subscribeConnectStream: $uuid - (EVENT: ${response.first}) Collect")

                    response.second.let {
                        when (it) {
                            2 -> {
                                Log.w(TAG, "subscribeConnectStream: 연결실패")
                                isConnectSteamConnected = false
                                delay(3000)
                                cancel()
                                throw Exception()
                            }

                            else -> {
                                Log.w(TAG, "not supported status code: $it")
                            }
                        }
                    }

                    response.first?.let {
                        if (it == ConnectEventResponse.ConnectEvent.WELCOME) {
                            Log.w(TAG, "subscribeConnectStream: WELCOME 수신 : 연결성공")
                            isConnectSteamConnected = true
                            requestGetDeviceInfo(uuid)
                        } else if (it == ConnectEventResponse.ConnectEvent.ENTRY) {
                            Log.w(TAG, "subscribeConnectStream: ENTRY 수신")
                            triggerMenuState(true)
                            cancel()
                            return@collect
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "subscribeConnectStream: 에러 수신")
                if (!isConnectSteamConnected) {
                    Log.w(TAG, "subscribeConnectStream: 연결 재시도 준비")
                    startProcess(uuid)
                    Log.w(TAG, "subscribeConnectStream: 연결 재시도 !")
                }
            }
        }
    }

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/15 1:34 PM donghwishin
     */
    suspend fun requestGetDeviceInfo(uuid: String) {
        deviceApiJob?.cancel()
        Log.w(TAG, "requestGetDeviceInfo: $uuid")
        deviceApiJob = viewModelScope.launch {
            getDeviceUseCase(uuid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _pairCodeInfo.emit(UiState.Loading)
                    is Resource.Error -> {
                        _pairCodeInfo.emit(UiState.Error(resource.message.toString()))
                        delay(3000)
                        startProcess(uuid)
                    }

                    is Resource.Success -> {
                        handleSuccess(resource.data)
                    }
                }
            }
        }
    }

    private suspend fun handleSuccess(data: DeviceModel?) {
        when (data?.status) {
            "Unlinked" -> {
                _pairCodeInfo.emit(
                    UiState.Success(
                        data = Pair(
                            data.linkProfile?.pinCode.toString(),
                            data.linkProfile?.qrUrl.toString()
                        )
                    )
                )
            }

            "Linked" -> {
                accessToken = data.property?.accessToken.toString()
                triggerMenuState(true)
            }

            else -> _pairCodeInfo.emit(UiState.Error("Not Supported Status"))
        }
    }


    override fun initState() {
        super.initState()
        Log.w(TAG, "initState")
    }
}
