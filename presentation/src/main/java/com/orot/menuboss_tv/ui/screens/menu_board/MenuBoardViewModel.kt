package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DeviceModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.GetPlaylistUseCase
import com.orot.menuboss_tv.domain.usecases.GetScheduleUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeContentStreamUseCase
import com.orot.menuboss_tv.ui.base.BaseViewModel
import com.orot.menuboss_tv.ui.model.SimpleScreenModel
import com.orot.menuboss_tv.ui.model.UiState
import com.orot.menuboss_tv.ui.screens.auth.AuthViewModel
import com.orotcode.menuboss.grpc.lib.ContentEventResponse
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

    /**
     * @feature: 서버 연결 상태 체크. 연결되어 있으면 true, 연결되어 있지 않으면 false 입니다.
     */
    private var isContentSteamConnected = false

    suspend fun startProcess(uuid: String) {
        Log.w(TAG, "startProcess: $uuid")
        _screenState.emit(UiState.Loading)
        requestGetDeviceInfo(uuid)
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
        uuid: String,
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
                        when (event) {
                            ContentEventResponse.ContentEvent.SCREEN_PASSED -> {
                                Log.w(TAG, "subscribeContentStream: 연결성공")
                                isContentSteamConnected = true
                                handleSuccess()
                            }

                            ContentEventResponse.ContentEvent.CONTENT_CHANGED -> {
                                requestGetDeviceInfo(uuid)
                            }

                            ContentEventResponse.ContentEvent.SHOW_SCREEN_NAME -> {
                                Log.w(TAG, "subscribeContentStream: SHOW_SCREEN_NAME")
                                _eventCode.emit(response.first)
                            }

                            ContentEventResponse.ContentEvent.CONTENT_EMPTY -> {
                                showingContents = false
                                _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                            }

                            ContentEventResponse.ContentEvent.SCREEN_DELETED -> {
                                _screenState.emit(UiState.Success(data = SimpleScreenModel(isPlaylist = null)))
                                triggerAuthState(true)
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
                Log.w(TAG, "subscribeContentStream: 에러 수신")
                if (!isContentSteamConnected){
                    Log.w(TAG, "subscribeContentStream: 연결 재시도 준비")
                    startProcess(uuid)
                    Log.w(TAG, "subscribeContentStream: 연결 재시도 !")
                }
            }
        }
    }

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceInfo(
        uuid: String,
    ) {
        deviceApiJob?.cancel()
        Log.w(TAG, "requestGetDeviceInfo: $uuid")
        deviceApiJob = viewModelScope.launch {
            getDeviceUseCase(uuid).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (!showingContents) {
                            _screenState.emit(UiState.Loading)
                        }
                    }

                    is Resource.Error -> {
                        _screenState.emit(UiState.Error(resource.message.toString()))
                        delay(3000)
                        startProcess(uuid)
                    }

                    is Resource.Success -> {
                        screenName = resource.data?.property?.name.toString()

                        if (isContentSteamConnected) {
                            handleSuccess(uuid, resource.data)
                        } else {
                            subscribeContentStream(
                                uuid,
                                resource.data?.property?.accessToken.toString()
                            ) {
                                handleSuccess(uuid, resource.data)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleSuccess(
        uuid: String,
        data: DeviceModel?
    ) {
        when (data?.status) {
            "Linked" -> {
                when (data.playing?.contentType) {
                    "Playlist" -> {
                        requestGetDevicePlaylist(
                            uuid,
                            data.property?.accessToken.toString()
                        )
                    }

                    "Schedule" -> {
                        requestGetDeviceSchedule(
                            uuid,
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
        uuid: String,
        accessToken: String
    ) {
        getScheduleUseCase(uuid, accessToken).onEach {
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
        uuid: String,
        accessToken: String
    ) {
        getPlaylistUseCase(uuid, accessToken).onEach {
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
