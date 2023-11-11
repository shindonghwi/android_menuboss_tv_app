package com.orot.menuboss_tv.ui.screens.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.logging.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.screens.splash.SplashViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.pow

@HiltViewModel
class AuthViewModel @Inject constructor(
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
     * @feature: 디바이스 정보 수집을 계속 시도할지 여부를 관리합니다.
     */
    private var isCollectRunning = true

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/15 1:34 PM donghwishin
     */
    suspend fun requestGetDeviceInfo(uuid: String) {
        Log.w(TAG, "requestGetDeviceInfo: $uuid")
        var attempt = 0
        isCollectRunning = true

        viewModelScope.launch {
            while (isCollectRunning) {  // 무한 루프
                getDeviceUseCase(uuid).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _pairCodeInfo.emit(UiState.Loading)
                        is Resource.Error -> {
                            _pairCodeInfo.emit(UiState.Error(resource.message.toString()))
                            delay(calculateDelay(attempt))
                            return@collect
                        }
                        is Resource.Success -> {
                            handleSuccess(resource.data)
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
                delay(500) // 인증 코드 변경시 화면 깜빡거림 방지
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
                isCollectRunning = false
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
