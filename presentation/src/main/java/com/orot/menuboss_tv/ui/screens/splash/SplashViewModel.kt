package com.orot.menuboss_tv.ui.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.screens.auth.AuthViewModel
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

    private val _navigateToAuthScreen = MutableStateFlow(false)
    val navigateToAuthScreen: StateFlow<Boolean> = _navigateToAuthScreen

    fun triggerNavigateToAuthScreen() {
        _navigateToAuthScreen.value = true
    }

    private val _navigateToMenuBoardScreen = MutableStateFlow(false)
    val navigateToMenuBoardScreen: StateFlow<Boolean> = _navigateToMenuBoardScreen

    fun triggerNavigateToMenuBoardScreen() {
        _navigateToMenuBoardScreen.value = true
    }

    /**
     * @feature: 디바이스 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    val deviceState = MutableStateFlow<UiState<DeviceModel>>(UiState.Idle)

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    suspend fun requestGetDeviceInfo(uuid: String) {
        Log.w(TAG, "SplashViewModel requestGetDeviceInfo: $uuid", )
        viewModelScope.launch {
            delay(3000)
            Log.w(TAG, "Splash requestGetDeviceInfo: $uuid")
            firebaseAnalyticsUtil.recordEvent(
                FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
                hashMapOf("uuid" to uuid)
            )

            getDeviceUseCase(uuid).onEach {
                when (it) {
                    is Resource.Loading -> deviceState.emit(UiState.Loading)
                    is Resource.Error -> deviceState.emit(UiState.Error(it.message.toString()))
                    is Resource.Success -> deviceState.emit(UiState.Success(data = it.data))
                }
            }.launchIn(this)
        }
    }

    fun resetStates() {
        _navigateToAuthScreen.value = false
        _navigateToMenuBoardScreen.value = false
        deviceState.value = UiState.Idle
    }
}
