package com.orot.menuboss_tv.ui.screens.splash

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.GetUuidUseCase
import com.orot.menuboss_tv.domain.usecases.PatchUuidUseCase
import com.orot.menuboss_tv.domain.usecases.UpdateUuidUseCase
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.utils.Brand
import com.orot.menuboss_tv.utils.DeviceInfoUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
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
    private val deviceInfoUtil: DeviceInfoUtil,
    private val patchUuidUseCase: PatchUuidUseCase,
    private val getUuidUseCase: GetUuidUseCase
) : BaseViewModel() {

    companion object {
        private const val TAG = "SplashViewModel"
    }

    /**
     * @feature: 강제 업데이트 여부를 관리합니다.
     * @author: 2023/11/16 1:41 PM donghwishin
     */
    private val _forceUpdateState = MutableStateFlow(FORCE_UPDATE_STATE.IDLE)
    val forceUpdateState: StateFlow<FORCE_UPDATE_STATE> get() = _forceUpdateState

    /**
     * @feature: 디바이스 정보를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private val _deviceState =
        MutableStateFlow<UiState<DeviceModel>>(UiState.Idle)
    val deviceState: StateFlow<UiState<DeviceModel>> get() = _deviceState

    private var _currentUUID: String = ""
    fun getCurrentUUID(): String = _currentUUID
    private fun updateCurrentUUID(newUuid: String) =
        kotlin.run { _currentUUID = newUuid }

    /**
     * @feature: 디바이스 정보 수집을 계속 시도할지 여부를 관리합니다.
     */
    suspend fun requestUpdateUUID(context: Context) {
        getUuidUseCase.invoke().takeIf { it.isNotEmpty() }?.let {
            Log.w(TAG, "requestUpdateUUID: 로컬 사용 : $it", )
            updateCurrentUUID(it)
            return
        } ?: kotlin.run {
            val newUuid = getAndroidUniqueId(context)
            patchUuidUseCase.invoke(newUuid)
            Log.w(TAG, "requestUpdateUUID: 새로운 아이디 사용 $newUuid", )
            updateCurrentUUID(newUuid)
        }
    }

    /**
     * @feature: 디바이스 정보 수집을 계속 시도할지 여부를 관리합니다.
     */
    private var isDeviceCollectRunning = true


    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    suspend fun requestGetDeviceInfo(appVersion: String) {
        Log.w(TAG, "requestGetDeviceInfo: $_currentUUID")
        var attempt = 0
        isDeviceCollectRunning = true

        viewModelScope.launch {
            while (isDeviceCollectRunning) {
                getDeviceUseCase(_currentUUID).collect { resource ->
                    Log.w(TAG, "requestGetDeviceInfo - response: $resource")
                    when (resource) {
                        is Resource.Loading -> _deviceState.emit(UiState.Loading)
                        is Resource.Error -> {
                            _deviceState.emit(UiState.Error(resource.message.toString()))
                            delay(calculateDelay(attempt))
                            return@collect
                        }

                        is Resource.Success -> {
                            val latestVersion =
                                extractNumbers(resource.data?.property?.version.toString())
                            val currentVersion = extractNumbers(appVersion)

                            if (appVersion.isEmpty()) { // 앱 버전을 찾을 수 없는 경우
                                _forceUpdateState.emit(FORCE_UPDATE_STATE.ERROR)
                                isDeviceCollectRunning = false
                                return@collect
                            } else if (latestVersion > currentVersion) { // 업데이트가 필요한 경우
                                _forceUpdateState.emit(FORCE_UPDATE_STATE.FORCE_UPDATE)
                                isDeviceCollectRunning = false
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
                isDeviceCollectRunning = false
                triggerAuthState(true)
                _deviceState.emit(UiState.Success(data = data))
            }

            "Linked" -> {
                isDeviceCollectRunning = false
                triggerMenuState(true)
                _deviceState.emit(UiState.Success(data = data))
            }

            else -> _deviceState.emit(UiState.Error("Not Supported Status"))
        }
    }

    private fun extractNumbers(str: String): String {
        val regex = "\\d+".toRegex() // 숫자만 찾는 정규식
        return regex.findAll(str)
            .joinToString(separator = "") { it.value } // 숫자를 연결하여 반환
    }

    override fun initState() {
        super.initState()
        _forceUpdateState.value = FORCE_UPDATE_STATE.IDLE
        _deviceState.value = UiState.Idle
        Log.w(TAG, "initState")
    }

    private fun getAndroidUniqueId(context: Context): String {
        val newUuid = deviceInfoUtil.getAndroidUniqueId(context)
        val domain = if (deviceInfoUtil.isAmazonDevice()) {
            Brand.AMAZON.domain
        } else {
            Brand.GOOGLE.domain
        }
        return "${domain}${creativeUUID(newUuid)}"
    }

    private fun creativeUUID(value: String): String {
        return deviceInfoUtil.run {
            val uuid1 = generateUniqueUUID(
                value, "${Build.PRODUCT}${Build.BRAND}${Build.HARDWARE}"
            )
            val uuid2 = generateUniqueUUID(
                value, "${Build.MANUFACTURER}${Build.MODEL}${Build.DEVICE}"
            )
            val uuid3 = generateUniqueUUID(value, Build.FINGERPRINT)
            val uuid = generateUniqueUUID("$uuid1", "$uuid2$uuid3").toString()
            Log.w(TAG, "getOldUuid: $uuid")
            return@run uuid
        }
    }
}