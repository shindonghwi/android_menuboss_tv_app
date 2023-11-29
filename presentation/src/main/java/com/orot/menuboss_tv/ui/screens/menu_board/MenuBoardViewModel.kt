package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.GetPlaylistUseCase
import com.orot.menuboss_tv.domain.usecases.GetScheduleUseCase
import com.orot.menuboss_tv.domain.usecases.SendEventPlayingStreamUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeContentStreamUseCase
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.SimpleScreenModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
import com.orotcode.menuboss.grpc.lib.PlayingEventRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuBoardViewModel @Inject constructor(
    private val subscribeContentStreamUseCase: SubscribeContentStreamUseCase,
    private val sendEventPlayingStreamUseCase: SendEventPlayingStreamUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getDeviceUseCase: GetDeviceUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "MenuBoardViewModel"
    }

    /**
     * @feature: SCREEN NAME
     * @description{
     *    Screen 정보 조회 후 받아 올 수 있다.
     * }
     */
    var screenName = ""
    private var _uuid = ""
    private var _isForeground = false
    private var _currentScheduleId: Int? = null
    private var _currentPlaylistId: Int? = null
    private var _currentContentId: String? = null

    fun getUUID(): String = _uuid
    fun updateUUID(uuid: String) = kotlin.run { _uuid = uuid }
    fun updateForeground(isForeground: Boolean) = kotlin.run { _isForeground = isForeground }
    fun updateCurrentScheduleId(scheduleId: Int?) = kotlin.run { _currentScheduleId = scheduleId }
    fun updateCurrentPlaylistId(playlistId: Int?) = kotlin.run { _currentPlaylistId = playlistId }
    fun updateCurrentContentId(contentId: String?) = kotlin.run { _currentContentId = contentId }

    /**
     * @feature: 서버 연결 상태 체크. 연결되어 있으면 true, 연결되어 있지 않으면 false 입니다.
     */
    private var isContentSteamConnected = false

    /**
     * @feature: 스크린이 삭제되었을때 이벤트
     */
    private var lastEvent: ContentEventResponse.ContentEvent? = null

    suspend fun startProcess() {
        Log.w(TAG, "startProcess: $_uuid")
        _screenState.emit(UiState.Loading)
        requestGetDeviceInfo()
    }

    private var currentContentStreamJob: Job? = null
    private var deviceApiJob: Job? = null

    private val _screenState = MutableStateFlow<UiState<SimpleScreenModel>>(UiState.Idle)
    val screenState: StateFlow<UiState<SimpleScreenModel>> get() = _screenState

    private val _eventCode = MutableSharedFlow<ContentEventResponse.ContentEvent?>(
        replay = 1, extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventCode: Flow<ContentEventResponse.ContentEvent?> get() = _eventCode.asSharedFlow()


    /**
     * @feature: GRPC 컨텐츠 스트림을 구독합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */

    private var showingContents = false

    private suspend fun subscribeContentStream(
        accessToken: String,
        handleSuccess: suspend () -> Unit
    ) {
        Log.w(TAG, "subscribeContentStream: START: $accessToken")
        // 기존 코루틴이 실행 중이라면 취소
        currentContentStreamJob?.cancel()

        currentContentStreamJob = viewModelScope.launch {
            try {
                subscribeContentStreamUseCase(accessToken).collect { response ->
                    Log.w(TAG, "subscribeContentStream: $accessToken - (EVENT: ${response.first}) Collect")

                    response.second.let {
                        when (it) {
                            2 -> {
                                Log.w(TAG, "subscribeContentStream: 연결실패")
                                isContentSteamConnected = false
                                delay(3000)
                                cancel()
                                throw Exception()
                            }

                            else -> {
                                Log.w(TAG, "not supported status code: $it")
                            }
                        }
                    }

                    response.first?.let { event ->
                        lastEvent = event
                        when (event) {
                            ContentEventResponse.ContentEvent.SCREEN_PASSED -> {
                                Log.w(TAG, "subscribeContentStream: 연결성공")
                                isContentSteamConnected = true
                                handleSuccess()
                            }

                            ContentEventResponse.ContentEvent.CONTENT_CHANGED -> {
                                requestGetDeviceInfo()
                            }

                            ContentEventResponse.ContentEvent.SHOW_SCREEN_NAME -> {
                                Log.w(TAG, "subscribeContentStream: SHOW_SCREEN_NAME")
                                requestGetDeviceInfo()
                            }

                            ContentEventResponse.ContentEvent.CONTENT_EMPTY -> {
                                showingContents = false
                                _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                            }

                            ContentEventResponse.ContentEvent.SCREEN_DELETED -> {
                                cancel()
                                return@collect
                            }

                            ContentEventResponse.ContentEvent.SCREEN_EXPIRED -> {
                                _screenState.emit(UiState.Success(data = SimpleScreenModel(isExpired = true)))
                                cancel()
                            }

                            else -> {}
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "subscribeContentStream: 에러 수신: $e")
                if (lastEvent == ContentEventResponse.ContentEvent.SCREEN_DELETED) {
                    Log.w(TAG, "subscribeContentStream: 스크린 삭제 이벤트 수신")
                    _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                    isContentSteamConnected = false
                    triggerAuthState(true)
                    Log.w(TAG, "subscribeContentStream: 스크린 삭제 이벤트 수신 후 인증화면으로 이동")
                } else if (!isContentSteamConnected) {
                    Log.w(TAG, "subscribeContentStream: 연결 재시도 준비")
                    startProcess()
                    Log.w(TAG, "subscribeContentStream: 연결 재시도 !")
                }
            }
        }
    }

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceInfo() {
        deviceApiJob?.cancel()
        Log.w(TAG, "requestGetDeviceInfo: $_uuid")
        deviceApiJob = viewModelScope.launch {
            getDeviceUseCase(_uuid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (!showingContents) {
                            _screenState.emit(UiState.Loading)
                        }
                    }

                    is Resource.Error -> {
                        _screenState.emit(UiState.Error(resource.message.toString()))
                        delay(3000)
                        startProcess()
                    }

                    is Resource.Success -> {
                        screenName = resource.data?.property?.name.toString()
                        _eventCode.emit(lastEvent)

                        if (isContentSteamConnected) {
                            handleSuccess(resource.data)
                        } else {
                            subscribeContentStream(
                                resource.data?.property?.accessToken.toString()
                            ) {
                                handleSuccess(resource.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleSuccess(
        data: DeviceModel?
    ) {
        when (data?.status) {
            "Linked" -> {
                when (data.playing?.contentType) {
                    "Playlist" -> {
                        requestGetDevicePlaylist(
                            data.property?.accessToken.toString()
                        )
                    }

                    "Schedule" -> {
                        requestGetDeviceSchedule(
                            data.property?.accessToken.toString()
                        )
                    }

                    null -> {
                        _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                    }
                }
            }

            "Unlinked" -> {
                showingContents = false
                triggerAuthState(true)
            }

            else -> _screenState.emit(UiState.Error("Not Supported Status"))
        }
    }

    /**
     * @feature: 디바이스의 스케줄을 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceSchedule(
        accessToken: String
    ) {
        getScheduleUseCase(_uuid, accessToken).onEach {
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
    private suspend fun requestGetDevicePlaylist(
        accessToken: String
    ) {
        getPlaylistUseCase(_uuid, accessToken).onEach {
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

    /**
     * @feature: Log Event 보내는 기능
     * @author: 2023/11/16 8:53 PM donghwishin
     */
    suspend fun sendEvent(event: PlayingEventRequest.PlayingEvent) {
        if (event == PlayingEventRequest.PlayingEvent.PLAYING) {
            if (!_isForeground) // 화면이 Foreground 상태가 아니면 play 이벤트를 보내지 않습니다.
                return
        }
        try {
            sendEventPlayingStreamUseCase(
                PlayingEventRequest
                    .newBuilder().apply {
                        uuid = _uuid
                        _currentScheduleId?.let { setScheduleId(it.toLong()) }
                        _currentPlaylistId?.let { setPlaylistId(it.toLong()) }
                        _currentContentId?.let { contentId = it }
                    }
                    .setEvent(event)
                    .build()
            )
//            Log.w(
//                TAG,
//                "sendEvent: event: $event, " +
//                        "scheduleId : $_currentScheduleId , " +
//                        "playlistId: $_currentPlaylistId , " +
//                        "contentId: $_currentContentId"
//            )
        } catch (e: Exception) {
            Log.w(TAG, "sendEvent: $e")
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentContentStreamJob?.cancel()
        deviceApiJob?.cancel()
    }
}
