package com.orot.menuboss_tv.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.data.model.Resource
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

    val authState = MutableStateFlow<String?>(null)

    suspend fun requestGetDeviceInfo(uuid: String) {
//        getTvDeviceUseCase(uuid).onEach {
//            if (it is Resource.Success) {
//                authState.emit(it.data.toString())
//            } else {
//                authState.emit(null)
//            }
//        }.launchIn(viewModelScope)

    }
}
