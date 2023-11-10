package com.orot.menuboss_tv.ui.screens.splash

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.logging.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.pow

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
    private val getDeviceUseCase: GetDeviceUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "SplashViewModel"
    }

    /**
     * @feature: accessToken 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    var accessToken: String = ""

    /**
     * @feature: 디바이스 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private val _deviceState = MutableStateFlow<UiState<DeviceModel>>(UiState.Idle)
    val deviceState: StateFlow<UiState<DeviceModel>> get() = _deviceState

    /**
     * @feature: Api가 성공적으로 응답되었는지 여부를 관리합니다. (디바이스 정보 조회 시 사용합니다.)
     */
    var isApiCallSuccess = false


    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    suspend fun requestGetDeviceInfo(uuid: String) {
        Log.w(TAG, "requestGetDeviceInfo: $uuid")
        viewModelScope.launch {

            while (!isApiCallSuccess) {
                accessToken = ""
                delay(calculateDelay(2))
                getDeviceUseCase(uuid).collect { resource ->
                    Log.w(TAG, "requestGetDeviceInfo - response: $resource")
                    when (resource) {
                        is Resource.Loading -> _deviceState.emit(UiState.Loading)
                        is Resource.Error -> _deviceState.emit(UiState.Error(resource.message.toString()))
                        is Resource.Success -> handleSuccess(resource.data)
                    }
                }
            }
        }
    }

    private fun calculateDelay(attempt: Int): Long {
        val maxDelay = 60000L  // 최대 지연 시간 (예: 60초)
        val delay = (2.0.pow(attempt.toDouble()) * 1000L).toLong() // 지수 백오프
        return min(delay, maxDelay)
    }

    private suspend fun handleSuccess(data: DeviceModel?) {
        _deviceState.emit(UiState.Success(data = data))
        when (data?.status) {
            "Unlinked" -> {
                triggerAuthState(false)
                isApiCallSuccess = true
            }

            "Linked" -> {
                triggerMenuState(false)
                accessToken = data.property?.accessToken.toString()
                isApiCallSuccess = true
            }

            else -> _deviceState.emit(UiState.Error("Not Supported Status"))
        }
    }

    override fun initState() {
        super.initState()
        _deviceState.value = UiState.Idle
        Log.w(TAG, "initState")
    }
}
