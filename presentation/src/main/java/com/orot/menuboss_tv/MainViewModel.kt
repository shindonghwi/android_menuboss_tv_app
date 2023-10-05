package com.orot.menuboss_tv

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orot.menuboss_tv.domain.entities.DevicePlaylistModel
import com.orot.menuboss_tv.domain.entities.DeviceScheduleModel
import com.orot.menuboss_tv.domain.entities.Resource
import com.orot.menuboss_tv.domain.usecases.GetDeviceUseCase
import com.orot.menuboss_tv.domain.usecases.GetPlaylistUseCase
import com.orot.menuboss_tv.domain.usecases.GetScheduleUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeConnectStreamUseCase
import com.orot.menuboss_tv.domain.usecases.SubscribeContentStreamUseCase
import com.orot.menuboss_tv.domain.usecases.UnSubscribeConnectStreamUseCase
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
    private val unSubscribeConnectStreamUseCase: UnSubscribeConnectStreamUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil,
    private val getDeviceUseCase: GetDeviceUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    /**
     * @feature: 디바이스의 고유한 식별자입니다.
     * @author: 2023/10/03 11:37 AM donghwishin
     */
    var uuid: String = ""

    val screenState = MutableStateFlow<UiState<SimpleScreenModel>>(UiState.Idle)

    /**
     * @feature: 연결 스트림의 상태를 관리합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    private val _connectionStatus =
        MutableStateFlow<Resource<ConnectEventResponse.ConnectEvent>?>(null)
    val connectionStatus: StateFlow<Resource<ConnectEventResponse.ConnectEvent>?> get() = _connectionStatus

    /**
     * @feature: GRPC 연결 스트림을 구독합니다.
     * @author: 2023/10/03 11:38 AM donghwishin
     */
    fun subscribeConnectStream() {
        try {
            coroutineScopeOnDefault {
                subscribeConnectStreamUseCase(uuid).collect { response ->
                    _connectionStatus.value = response
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
            coroutineScopeOnDefault {
                subscribeContentStreamUseCase(accessToken).collect { response ->
                    when (response.data) {
                        ContentEventResponse.ContentEvent.CONTENT_CHANGED -> {
                            requestGetDeviceInfo()
                        }

                        ContentEventResponse.ContentEvent.CONTENT_EMPTY -> {
                            screenState.emit(
                                UiState.Success(
                                    data = SimpleScreenModel(
                                        isPlaylist = null
                                    )
                                )
                            )
                        }
                        ContentEventResponse.ContentEvent.SCREEN_DELETED -> {
                            screenState.emit(
                                UiState.Success(
                                    data = SimpleScreenModel(
                                        isDeleted = true
                                    )
                                )
                            )
                        }
                        ContentEventResponse.ContentEvent.SCREEN_EXPIRED -> {
                            screenState.emit(
                                UiState.Success(
                                    data = SimpleScreenModel(
                                        isExpired = true
                                    )
                                )
                            )
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

    fun unSubscribeConnectStream() {
        try {
            coroutineScopeOnDefault {
                unSubscribeConnectStreamUseCase.invoke()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in unSubscribeConnectStream: ${e.message}")
        }
    }


    /**
     * @feature: 디바이스 정보를 조회합니다.
     * @author: 2023/10/03 11:39 AM donghwishin
     */
    private suspend fun requestGetDeviceInfo() {
        firebaseAnalyticsUtil.recordEvent(
            FirebaseAnalyticsUtil.Event.GET_DEVICE_INFO,
            hashMapOf("uuid" to uuid)
        )

        getDeviceUseCase(uuid).onEach {
            Log.w(TAG, "requestGetDeviceInfo: $it")
            when (it) {
                is Resource.Loading -> {
                    screenState.emit(UiState.Loading)
                }

                is Resource.Error -> {
                    screenState.emit(UiState.Error(it.message.toString()))
                }

                is Resource.Success -> {
                    coroutineScopeOnDefault {
                        delay(1000)
                        val accessToken =
                            it.data?.property?.accessToken.toString()
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
            FirebaseAnalyticsUtil.Event.GET_DEVICE_SCHEDULE_INFO,
            hashMapOf("uuid" to uuid)
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
            FirebaseAnalyticsUtil.Event.GET_DEVICE_PLAYLIST_INFO,
            hashMapOf("uuid" to uuid)
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
