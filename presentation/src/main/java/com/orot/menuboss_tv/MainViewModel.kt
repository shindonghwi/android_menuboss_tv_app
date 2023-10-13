package com.orot.menuboss_tv

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.GetPlaylistUseCase
import com.orot.menuboss_tv.domain.usecases.GetScheduleUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeConnectStreamUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeContentStreamUseCase
import com.orot.menuboss_tv.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.model.SimpleScreenModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.utils.coroutineScopeOnDefault
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val subscribeConnectStreamUseCase: SubscribeConnectStreamUseCase,
    private val subscribeContentStreamUseCase: SubscribeContentStreamUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
    private val getDeviceUseCase: GetDeviceUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val navigateToAuthScreen = MutableStateFlow(false)
    fun triggerAuthState(flag: Boolean) = run { navigateToAuthScreen.value = flag }


    val navigateToMenuBoardScreen = MutableStateFlow(false)
    fun triggerMenuBoardState(flag: Boolean) = run { navigateToMenuBoardScreen.value = flag }


    /**
     * @feature: 디바이스의 코드와 QR URL을 관리합니다.
     * @author: 2023/10/12 12:33 PM donghwishin
     */
    var code = MutableStateFlow<String?>(null)
    var qrUrl = MutableStateFlow<String?>(null)

    private fun _updateCodeAndQrUrl(code: String?, qrUrl: String?) {
        this.code.value = code
        this.qrUrl.value = qrUrl
        Log.w(TAG, "updateCodeAndQrUrl: $code $qrUrl")
    }

    /**
     * @feature: 디바이스의 고유한 식별자입니다.
     * @author: 2023/10/03 11:37 AM donghwishin
     */
    var uuid: String = ""

    val deviceState = MutableStateFlow<UiState<DeviceModel>>(UiState.Idle)
    val screenState = MutableStateFlow<UiState<SimpleScreenModel>>(UiState.Idle)

    /**
     * @feature: GRPC 연결 스트림을 구독합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private var openedConnectedStream = false
    private var openedContentStream = false
    private val _grpcStatusCode = MutableStateFlow<Int?>(null)
    val grpcStatusCode: StateFlow<Int?> get() = _grpcStatusCode

    fun triggerEntryStatus(number: Int?) = run { _grpcStatusCode.value = number }
    fun triggerDeviceStatus(state: UiState<DeviceModel>) = run { deviceState.value = state }

    fun subscribeConnectStream() {
        try {
            Log.w(TAG, "subscribeConnectStream: RUN", )
            if (openedConnectedStream) return
            coroutineScopeOnDefault {
                delay(500)
                Log.w(TAG, "subscribeConnectStream: RUN : subscribeConnectStream START", )

                subscribeConnectStreamUseCase(uuid).collect { response ->
                    if (response is Resource.Success) {
                        Log.w(TAG, "subscribeConnectStream: Success : ${response.data}", )
                        openedConnectedStream = true
                        if (response.data == ConnectEventResponse.ConnectEvent.ENTRY) {
                            _grpcStatusCode.value = ConnectEventResponse.ConnectEvent.ENTRY.number
                            openedConnectedStream = false
                            return@collect
                        }
                    } else if (response is Resource.Error) {
                        Log.w(TAG, "subscribeConnectStream: Fail", )
                        _grpcStatusCode.value = null
                        openedConnectedStream = false
                        return@collect
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in subscribeConnectStream: ${e.message}")
        }
    }

    /**
     * @feature: GRPC 컨텐츠 스트림을 구독합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     *
     * @Description:{
     *
     * ContentEventResponse.ContentEvent.CONTENT_CHANGED -> 컨텐츠 변경 이벤트
     *
     * }
     */
    fun subscribeContentStream(accessToken: String) {
        try {
            Log.w(TAG, "subscribeContentStream: RUN: $accessToken", )
            if (openedContentStream) return
            coroutineScopeOnDefault {
                Log.w(TAG, "subscribeContentStream: RUN START", )
                subscribeContentStreamUseCase(accessToken).collect { response ->

                    Log.w("Asddsadsasda", "subscribeContentStream: ${response}", )

                    if (response is Resource.Success) {
                        openedContentStream = true
                    } else if (response is Resource.Error) {
                        _grpcStatusCode.value = null
                        openedContentStream = false
                        Log.w(TAG, "subscribeContentStream: RUN COLLECT END", )
                        return@collect
                    }

                    when (response.data) {
                        ContentEventResponse.ContentEvent.CONTENT_CHANGED -> {
                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.CONTENT_CHANGED.number
                        }

                        ContentEventResponse.ContentEvent.CONTENT_EMPTY -> {
                            screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.CONTENT_EMPTY.number
                        }

                        ContentEventResponse.ContentEvent.SCREEN_DELETED -> {
                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.SCREEN_DELETED.number
                            openedContentStream = false
                            return@collect
                        }

                        ContentEventResponse.ContentEvent.SCREEN_EXPIRED -> {
                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.SCREEN_EXPIRED.number
                        }

                        else -> {
                            UiState.Error(response.message.toString())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in subscribeContentStream: ${e.message}")
        }
    }

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    suspend fun requestGetDeviceInfo(
        delayTime: Long = 0L,
        executePostApiGetContent: Boolean = false
    ) {
        delay(delayTime)

        Log.w(TAG, "MainViewModel requestGetDeviceInfo: $uuid")
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO, hashMapOf("uuid" to uuid)
        )

        getDeviceUseCase(uuid).onEach {
            Log.w(TAG, "requestGetDeviceInfo: $it")
            when (it) {
                is Resource.Loading -> {
                    deviceState.emit(UiState.Loading)
                    screenState.emit(UiState.Loading)
                }

                is Resource.Error -> {
                    deviceState.emit(UiState.Error(it.message.toString()))
                    screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    deviceState.emit(UiState.Success(data = it.data))


                    it.data?.let { response ->
                        if (response.status == "Unlinked") {
                            _updateCodeAndQrUrl(
                                code = response.linkProfile?.pinCode,
                                qrUrl = response.linkProfile?.qrUrl
                            )
                        }
                    }

                    if (executePostApiGetContent) {
                        val accessToken = it.data?.property?.accessToken.toString()
                        if (it.data?.playing?.contentType == "Playlist") {
                            requestGetDevicePlaylist(accessToken)
                        } else if (it.data?.playing?.contentType == "Schedule") {
                            requestGetDeviceSchedule(accessToken)
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * @feature: 디바이스의 스케줄을 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceSchedule(accessToken: String) {
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_SCHEDULE_INFO, hashMapOf("uuid" to uuid)
        )

        getScheduleUseCase(uuid, accessToken).onEach {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    screenState.emit(
                        UiState.Success(
                            data = SimpleScreenModel(
                                isPlaylist = false,
                                scheduleModel = it.data,
                            )
                        ),
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    /**

     * @feature: 디바이스의 플레이리스트를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDevicePlaylist(accessToken: String) {
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_PLAYLIST_INFO, hashMapOf("uuid" to uuid)
        )

        getPlaylistUseCase(uuid, accessToken).onEach {
            Log.w(TAG, "asdsadsad requestGetDevicePlaylist: ${it}")
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    screenState.emit(
                        UiState.Success(
                            data = SimpleScreenModel(
                                isPlaylist = true,
                                playlistModel = it.data,
                            )
                        ),
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}
