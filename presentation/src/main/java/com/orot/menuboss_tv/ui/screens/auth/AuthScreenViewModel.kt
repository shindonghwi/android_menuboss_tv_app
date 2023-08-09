package com.orot.menuboss_tv.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceInfo
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetTvDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val getTvDeviceUseCase: GetTvDeviceUseCase
) : ViewModel() {

    val authState = MutableStateFlow<DeviceInfo?>(null)

    suspend fun requestGetDeviceInfo(uuid: String) {
        getTvDeviceUseCase(uuid).onEach {
            if (it is Resource.Success) {
                authState.emit(it.data)
            } else {
                authState.emit(null)
            }
        }.launchIn(viewModelScope)
    }
}
