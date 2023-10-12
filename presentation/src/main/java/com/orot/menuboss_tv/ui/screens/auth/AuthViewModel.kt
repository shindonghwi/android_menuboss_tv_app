package com.orot.menuboss_tv.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
    private val getDeviceUseCase: GetDeviceUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _navigateToMenuBoardScreen = MutableStateFlow(false)
    val navigateToMenuBoardScreen: StateFlow<Boolean> = _navigateToMenuBoardScreen

    fun triggerNavigateToMenuBoardScreen() {
        _navigateToMenuBoardScreen.value = true
    }

    fun initNavigateToMenuBoardScreen() {
        _navigateToMenuBoardScreen.value = false
    }

    val deviceState = MutableStateFlow<UiState<DeviceModel>>(UiState.Idle)

    suspend fun requestGetDeviceInfo(uuid: String) {
        Log.d(TAG, "AuthViewModel requestGetDeviceInfo: $uuid")

        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
            hashMapOf("uuid" to uuid)
        )

        getDeviceUseCase(uuid).onEach {
            Log.d(TAG, "requestGetDeviceInfo status : $it")
            when (it) {
                is Resource.Loading -> deviceState.emit(UiState.Loading)
                is Resource.Error -> {
                    val errorMessage = it.message ?: "Unknown error occurred"
                    deviceState.emit(UiState.Error(errorMessage))
                }
                is Resource.Success -> deviceState.emit(UiState.Success(data = it.data))
            }
        }.launchIn(viewModelScope)
    }

    fun initDeviceState(){
        deviceState.value = UiState.Idle
    }

}
