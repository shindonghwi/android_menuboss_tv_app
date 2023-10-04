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
        Log.w(TAG, "AuthView requestGetDeviceInfo: $uuid", )
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
            hashMapOf("uuid" to uuid)
        )

        getDeviceUseCase(uuid).onEach {
            Log.w(TAG, "requestGetDeviceInfo status : $it", )
            when (it) {
                is Resource.Loading -> deviceState.emit(UiState.Loading)
                is Resource.Error -> deviceState.emit(UiState.Error(it.message.toString()))
                is Resource.Success -> deviceState.emit(UiState.Success(data = it.data))
            }
        }.launchIn(viewModelScope)
    }
}
