package com.orot.menuboss_tv.ui.screens.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
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
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> get() = _accessToken

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/15 1:34 PM donghwishin
    */
    suspend fun requestGetDeviceInfo(uuid: String) {
        Log.w(TAG, "requestGetDeviceInfo: $uuid")

        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
            hashMapOf("uuid" to uuid)
        )

        getDeviceUseCase(uuid).onEach {
            Log.w(TAG, "requestGetDeviceInfo - response: $it")
            when (it) {
                is Resource.Loading -> _pairCodeInfo.emit(UiState.Loading)
                is Resource.Error -> {
                    delay(3000)
                    requestGetDeviceInfo(uuid)
                }

                is Resource.Success -> {
                    val data = it.data
                    if (data?.status == "Unlinked") {
                        _pairCodeInfo.value = UiState.Success(
                            data = Pair(
                                data.linkProfile?.pinCode.toString(),
                                data.linkProfile?.qrUrl.toString()
                            )
                        )
                    }else if (data?.status == "Linked"){
                        _accessToken.value = data.property?.accessToken
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun initState() {
        super.initState()
        _accessToken.value = null
        Log.w(TAG, "initState")
    }
}
