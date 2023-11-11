package com.orot.menuboss_tv

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.GetPlaylistUseCase
import com.orot.menuboss_tv.domain.usecases.GetScheduleUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeConnectStreamUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeContentStreamUseCase
import com.orot.menuboss_tv.logging.firebase.FirebaseAnalyticsUtil
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.SimpleScreenModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orotcode.menuboss.grpc.lib.ConnectEventResponse
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val subscribeConnectStreamUseCase: SubscribeConnectStreamUseCase,
    private val subscribeContentStreamUseCase: SubscribeContentStreamUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
    private val getDeviceUseCase: GetDeviceUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    /**
     * @feature: 디바이스의 고유한 식별자입니다.
     * @author: 2023/10/03 11:37 AM donghwishin
     */
    private var _uuid: String = ""

    fun updateUUID(uuid: String) = run { this._uuid = uuid }
    fun getUUID(): String = _uuid

    /**
     * @feature: 콘텐츠 스트림의 accessToken
     * @author: 2023/10/03 11:37 AM donghwishin
     */
    private var _accessToken: String = ""

    fun updateAccessToken(accessToken: String) = run { this._accessToken = accessToken }


    private val _screenState = MutableStateFlow<UiState<SimpleScreenModel>>(UiState.Idle)
    val screenState: StateFlow<UiState<SimpleScreenModel>> get() = _screenState

    /**
     * @feature: GRPC 연결 스트림을 구독합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private val _grpcConnectEvent = MutableStateFlow<ConnectEventResponse.ConnectEvent?>(null)
    val grpcConnectEvent: StateFlow<ConnectEventResponse.ConnectEvent?> get() = _grpcConnectEvent


    private val _isConnectStreamConnected = MutableStateFlow<Boolean>(false)
    val isConnectStreamConnected: StateFlow<Boolean> get() = _isConnectStreamConnected

    /**
     * @feature: GRPC Connect Stream을 구독합니다.
     * @author: 2023/11/06 9:49 AM donghwishin
     */
    suspend fun subscribeConnectStream(uuid: String = getUUID()) {
        Log.w(TAG, "subscribeConnectStream: START: $uuid")

        viewModelScope.launch {
            subscribeConnectStreamUseCase(uuid).collect { response, ->
                Log.w(TAG, "subscribeConnectStream: $uuid - (EVENT: ${response.first}) Collect")

                response.second.let {
                    if (it == 1) {
                        _isConnectStreamConnected.value = true
                        return@collect
                    }
                }

                response.first?.let {
                    if (it == ConnectEventResponse.ConnectEvent.ENTRY) {
                        _grpcConnectEvent.value = ConnectEventResponse.ConnectEvent.ENTRY
                        return@collect
                    }
                }
            }
        }
    }

    /**
     * @feature: GRPC 컨텐츠 스트림을 구독합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */

    var showingContents = false

    suspend fun subscribeContentStream() {
        if (_accessToken.isEmpty()) return
        Log.w(TAG, "subscribeContentStream: RUN: $_accessToken")

        subscribeContentStreamUseCase(_accessToken).collect { response ->
            response?.let {
                Log.w(TAG, "subscribeContentStream: response $it")

                val event = it.first
                val status = it.second

                if (status == 701) {
                    contentStreamFailInit()
                    return@collect
                } else {
                    Log.w(TAG, "subscribeContentStream: event: $event")
                    when (event) {
                        ContentEventResponse.ContentEvent.CONTENT_CHANGED -> {
//                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.CONTENT_CHANGED.number
                            requestGetDeviceInfo(executeContentsCallApiAction = true)
                        }

                        ContentEventResponse.ContentEvent.CONTENT_EMPTY -> {
                            showingContents = false
                            _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
//                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.CONTENT_EMPTY.number
                        }

                        ContentEventResponse.ContentEvent.SCREEN_DELETED -> {
                            _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
//                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.SCREEN_DELETED.number
                            triggerAuthState(true)
                            return@collect
                        }

                        ContentEventResponse.ContentEvent.SCREEN_EXPIRED -> {
//                            _grpcStatusCode.value = ContentEventResponse.ContentEvent.SCREEN_EXPIRED.number
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    /**
     * @feature: GRPC 컨텐츠 스트림이 실패했을 때 초기화합니다.
     * @author: 2023/11/06 9:54 AM donghwishin
     */
    private suspend fun contentStreamFailInit() {
        _accessToken = ""
//        _grpcStatusCode.value = null
        requestGetDeviceInfo(executeContentsCallApiAction = false)
        delay(1000)
        subscribeContentStream()
    }


    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceInfo(
        executeContentsCallApiAction: Boolean,
    ) {
        val uuid = getUUID()
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO, hashMapOf("uuid" to uuid)
        )

        getDeviceUseCase(uuid).onEach {
            Log.w(TAG, "requestGetDeviceInfo: $it")
            when (it) {
                is Resource.Loading -> {
                    if (!showingContents) {
                        _screenState.emit(UiState.Loading)
                    }
                }

                is Resource.Error -> {}
                is Resource.Success -> {
                    updateAccessToken(it.data?.property?.accessToken.toString())
                    if (it.data?.status == "Linked") {
                        if (executeContentsCallApiAction) {
                            if (it.data?.playing?.contentType == "Playlist") {
                                requestGetDevicePlaylist()
                            } else if (it.data?.playing?.contentType == "Schedule") {
                                requestGetDeviceSchedule()
                            }
                        }
                    } else {
                        _screenState.emit(UiState.Idle)
                        showingContents = false
//                        _grpcStatusCode.value = null
                        updateAccessToken("")
                        triggerAuthState(true)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * @feature: 디바이스의 스케줄을 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceSchedule() {
        val uuid = getUUID()
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_SCHEDULE_INFO, hashMapOf("uuid" to uuid)
        )

        getScheduleUseCase(uuid, _accessToken).onEach {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    _screenState.emit(
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
    private suspend fun requestGetDevicePlaylist() {
        val uuid = getUUID()

        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_PLAYLIST_INFO, hashMapOf("uuid" to uuid)
        )

        getPlaylistUseCase(uuid, _accessToken).onEach {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    showingContents = true
                    _screenState.emit(
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
