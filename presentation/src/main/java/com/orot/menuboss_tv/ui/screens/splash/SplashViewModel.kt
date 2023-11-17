package com.orot.menuboss_tv.ui.screens.splash

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FORCE_UPDATE_STATE {
    IDLE,
    FORCE_UPDATE,
    NOT_FORCE_UPDATE,
    ERROR
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getDeviceUseCase: GetDeviceUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "SplashViewModel"
    }

    /**
     * @feature: 강제 업데이트 여부를 관리합니다.
     * @author: 2023/11/16 1:41 PM donghwishin
     */
    private val _forceUpdateState = MutableStateFlow<FORCE_UPDATE_STATE>(FORCE_UPDATE_STATE.IDLE)
    val forceUpdateState: StateFlow<FORCE_UPDATE_STATE> get() = _forceUpdateState

    /**
     * @feature: 디바이스 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private val _deviceState = MutableStateFlow<UiState<DeviceModel>>(UiState.Idle)
    val deviceState: StateFlow<UiState<DeviceModel>> get() = _deviceState

    /**
     * @feature: 디바이스 정보 수집을 계속 시도할지 여부를 관리합니다.
     */
    private var isCollectRunning = true


    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    suspend fun requestGetDeviceInfo(uuid: String, appVersion: String) {
        Log.w(TAG, "requestGetDeviceInfo: $uuid")
        var attempt = 0
        isCollectRunning = true

        viewModelScope.launch {
            while (isCollectRunning) {
                getDeviceUseCase(uuid).collect { resource ->
                    Log.w(TAG, "requestGetDeviceInfo - response: $resource")
                    when (resource) {
                        is Resource.Loading -> _deviceState.emit(UiState.Loading)
                        is Resource.Error -> {
                            _deviceState.emit(UiState.Error(resource.message.toString()))
                            delay(calculateDelay(attempt))
                            return@collect
                        }

                        is Resource.Success -> {
                            val latestVersion = extractNumbers(resource.data?.property?.version.toString())
                            val currentVersion = extractNumbers(appVersion)

                            if (appVersion.isEmpty()) { // 앱 버전을 찾을 수 없는 경우
                                _forceUpdateState.emit(FORCE_UPDATE_STATE.ERROR)
                                isCollectRunning = false
                                return@collect
                            } else if (latestVersion > currentVersion) { // 업데이트가 필요한 경우
                                _forceUpdateState.emit(FORCE_UPDATE_STATE.FORCE_UPDATE)
                                isCollectRunning = false
                                return@collect
                            } else { // 업데이트가 필요하지 않은 경우
                                _forceUpdateState.emit(FORCE_UPDATE_STATE.NOT_FORCE_UPDATE)
                                handleSuccess(resource.data)
                            }
                        }
                    }
                }
                attempt++
            }
        }
    }

    private suspend fun handleSuccess(data: DeviceModel?) {
        when (data?.status) {
            "Unlinked" -> {
                isCollectRunning = false
                triggerAuthState(true)
                _deviceState.emit(UiState.Success(data = data))
            }

            "Linked" -> {
                isCollectRunning = false
                triggerMenuState(true)
                _deviceState.emit(UiState.Success(data = data))
            }

            else -> _deviceState.emit(UiState.Error("Not Supported Status"))
        }
    }

    fun extractNumbers(str: String): String {
        val regex = "\\d+".toRegex() // 숫자만 찾는 정규식
        return regex.findAll(str).joinToString(separator = "") { it.value } // 숫자를 연결하여 반환
    }

    override fun initState() {
        super.initState()
        _forceUpdateState.value = FORCE_UPDATE_STATE.IDLE
        _deviceState.value = UiState.Idle
        Log.w(TAG, "initState")
    }
}
