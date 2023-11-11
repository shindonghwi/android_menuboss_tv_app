package com.orot.menuboss_tv

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
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
import com.orot.menuboss_tv.ui.screens.splash.SplashViewModel
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

    /**
     * @feature: 커넥트 스트림 서버 연결상태
     * @author: 2023/11/11 2:36 PM donghwishin
     */
    private val _isConnectStreamConnected = MutableStateFlow<Boolean>(false)
    val isConnectStreamConnected: StateFlow<Boolean> get() = _isConnectStreamConnected

    /**
     * @feature: 컨텐츠 스트림 서버 연결상태
     * @author: 2023/11/11 2:36 PM donghwishin
     */
    private val _isContentStreamConnected = MutableStateFlow<Boolean>(false)
    val isContentStreamConnected: StateFlow<Boolean> get() = _isContentStreamConnected

    /**
     * @feature: GRPC Connect Stream을 구독합니다.
     * @author: 2023/11/06 9:49 AM donghwishin
     */
    suspend fun subscribeConnectStream(uuid: String = getUUID()) {
        Log.w(TAG, "subscribeConnectStream: START: $uuid")

        viewModelScope.launch {
            subscribeConnectStreamUseCase(uuid).collect { response ->
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

    private var showingContents = false

    suspend fun subscribeContentStream() {
        if (_accessToken.isEmpty()) return
        Log.w(TAG, "subscribeContentStream: RUN: $_accessToken")

        subscribeContentStreamUseCase(_accessToken).collect { response ->
            Log.w(TAG, "subscribeContentStream: $_accessToken - (EVENT: ${response?.first}) Collect")

            response?.second.let {
                if (it == 1) {
                    _isContentStreamConnected.value = true
                    return@collect
                }
            }

            response?.first?.let { event ->
                when (event) {
                    ContentEventResponse.ContentEvent.CONTENT_CHANGED -> {
                        requestGetDeviceInfo(executeContentsCallApiAction = true)
                    }

                    ContentEventResponse.ContentEvent.CONTENT_EMPTY -> {
                        showingContents = false
                        _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                    }

                    ContentEventResponse.ContentEvent.SCREEN_DELETED -> {
                        _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                        triggerAuthState(true)
                        return@collect
                    }

                    ContentEventResponse.ContentEvent.SCREEN_EXPIRED -> {
                        _screenState.emit(UiState.Success(data = SimpleScreenModel(isExpired = true)))
                    }

                    else -> {}
                }
            }

        }
    }

    /**
     * @feature: 디바이스 정보 수집을 계속 시도할지 여부를 관리합니다.
     */
    private var isCollectRunning = true

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceInfo(
        executeContentsCallApiAction: Boolean,
    ) {
        val uuid = getUUID()
        var attempt = 0
        isCollectRunning = true

        Log.w(TAG, "requestGetDeviceInfo: $uuid")

        viewModelScope.launch {
            while (isCollectRunning) {
                updateAccessToken("")
                getDeviceUseCase(uuid).collect { resource ->
                    Log.w(TAG, "requestGetDeviceInfo - response: $resource")
                    when (resource) {
                        is Resource.Loading -> if (!showingContents) {
                            _screenState.emit(UiState.Loading)
                        }

                        is Resource.Error -> {
                            _screenState.emit(UiState.Error(resource.message.toString()))
                            delay(calculateDelay(attempt))
                            return@collect
                        }

                        is Resource.Success -> handleSuccess(
                            resource.data,
                            executeContentsCallApiAction
                        )
                    }
                }
                attempt++
            }
        }
    }

    private suspend fun handleSuccess(
        data: DeviceModel?,
        executeContentsCallApiAction: Boolean
    ) {
        updateAccessToken(data?.property?.accessToken.toString())
        when (data?.status) {
            "Linked" -> {
                if (executeContentsCallApiAction) {
                    when (data.playing?.contentType) {
                        "Playlist" -> {
                            requestGetDevicePlaylist()
                        }
                        "Schedule" -> {
                            requestGetDeviceSchedule()
                        }
                        null -> {
                            isCollectRunning = false
                            _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                        }
                    }
                }
                isCollectRunning = false
            }

            "Unlinked" -> {
                _screenState.emit(UiState.Idle)
                showingContents = false
                updateAccessToken("")
                triggerAuthState(true)
            }

            else -> _screenState.emit(UiState.Error("Not Supported Status"))
        }
    }

    /**
     * @feature: 디바이스의 스케줄을 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceSchedule() {
        val uuid = getUUID()

        getScheduleUseCase(uuid, _accessToken).onEach {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    isCollectRunning = false
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

        getPlaylistUseCase(uuid, _accessToken).onEach {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    showingContents = true
                    isCollectRunning = false
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
