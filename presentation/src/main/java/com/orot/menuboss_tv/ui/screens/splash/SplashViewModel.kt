package com.orot.menuboss_tv.ui.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
    private val getDeviceUseCase: GetDeviceUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "SplashViewModel"
    }

    /**
     * @feature: 인증화면으로 이동하는 기능
     * @author: 2023/10/15 12:40 PM donghwishin
     */
    private val _navigateToAuthState = MutableStateFlow(false)
    val navigateToAuthState: StateFlow<Boolean> get() = _navigateToAuthState
    fun triggerAuthState(flag: Boolean) = run { _navigateToAuthState.value = flag }

    /**
     * @feature: 메뉴화면으로 이동하는 기능
     * @author: 2023/10/15 12:41 PM donghwishin
     */
    private val _navigateToMenuState = MutableStateFlow(false)
    val navigateToMenuState: StateFlow<Boolean> get() = _navigateToMenuState
    fun triggerMenuState(flag: Boolean) = run { _navigateToMenuState.value = flag }

    /**
     * @feature: 디바이스 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private val _deviceState = MutableStateFlow<UiState<DeviceModel>>(UiState.Idle)
    val deviceState: StateFlow<UiState<DeviceModel>> get() = _deviceState

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    suspend fun requestGetDeviceInfo(uuid: String) {
        Log.w(TAG, "requestGetDeviceInfo: $uuid")
        viewModelScope.launch {
            delay(3000)
            Log.w(TAG, "requestGetDeviceInfo: $uuid")
            firebaseAnalyticsUtil.recordEvent(
                FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
                hashMapOf("uuid" to uuid)
            )

            getDeviceUseCase(uuid).onEach {
                Log.w(TAG, "requestGetDeviceInfo - response: $it", )
                when (it) {
                    is Resource.Loading -> _deviceState.emit(UiState.Loading)
                    is Resource.Error -> _deviceState.emit(UiState.Error(it.message.toString()))
                    is Resource.Success -> _deviceState.emit(UiState.Success(data = it.data))
                }
            }.launchIn(this)
        }
    }

    fun initState(){
        Log.w(TAG, "initState", )
        triggerAuthState(false)
        triggerMenuState(false)
        _deviceState.value = UiState.Idle
    }
}
