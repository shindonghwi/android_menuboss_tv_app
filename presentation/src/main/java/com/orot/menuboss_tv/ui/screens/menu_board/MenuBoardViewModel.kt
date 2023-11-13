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
     * @feature: 서버 연결 상태 체크. 연결되어 있으면 true, 연결되어 있지 않으면 false 입니다.
     */
    private var isContentSteamConnected = false

    suspend fun startProcess(uuid: String) {
        Log.w(TAG, "startProcess: $uuid")
        _screenState.emit(UiState.Loading)
        requestGetDeviceInfo(uuid, inContentChangedEvent = false)
    }

    private var currentConnectStreamJob: Job? = null
    private var deviceApiJob: Job? = null

    private val _screenState = MutableStateFlow<UiState<SimpleScreenModel>>(UiState.Idle)
    val screenState: StateFlow<UiState<SimpleScreenModel>> get() = _screenState

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
        currentConnectStreamJob?.cancel()

        currentConnectStreamJob = viewModelScope.launch {
            try {
                subscribeContentStreamUseCase(accessToken).collect { response ->
                    Log.w(TAG, "subscribeContentStream: $accessToken - (EVENT: ${response.first}) Collect")

                    response.second.let {
                        when (it) {
                            1 -> {
                                Log.w(TAG, "subscribeConnectStream: 연결성공")
                                isContentSteamConnected = true
                                handleSuccess()
                            }

                            2 -> {
                                Log.w(TAG, "subscribeConnectStream: 연결실패")
                                isContentSteamConnected = false
                                throw Exception()
                            }

                            else -> {
                                Log.w(TAG, "not supported status code: $it")
                            }
                        }
                    }

                    response.first?.let { event ->
                        when (event) {
                            ContentEventResponse.ContentEvent.CONTENT_CHANGED -> {
                                requestGetDeviceInfo(uuid, inContentChangedEvent = true)
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
            } catch (e: Exception) {
                Log.w(TAG, "subscribeContentStream: 에러 수신")
                delay(3000)
                startProcess(uuid)
            }
        }
    }

    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceInfo(
        uuid: String,
        inContentChangedEvent: Boolean
    ) {
        deviceApiJob?.cancel()
        Log.w(TAG, "requestGetDeviceInfo: $uuid | inContentChangedEvent: $inContentChangedEvent")
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
                        if (isContentSteamConnected || inContentChangedEvent) {
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
