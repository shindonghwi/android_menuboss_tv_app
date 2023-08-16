package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.orot.menuboss_tv.domain.entities.SocketMsg
import com.orot.menuboss_tv.presentation.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

@HiltViewModel
class MenuBoardScreenViewModel @Inject constructor(
) : ViewModel(), CoroutineScope {

    private val socketUrl: String = if (BuildConfig.DEBUG) {
        "wss://dev-screen.menuboss.tv/v1/stream"
    } else {
        "wss://screen.menuboss.tv/v1/stream"
    }

    override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main
    private val job = Job()
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var connectionAttempt = 0

    val screenItems = MutableStateFlow<List<Pair<Long, String>>>(listOf())

    /**
     * @feature: WebSocket 연결하기
     * @author: 2023/08/09 4:56 PM donghwishin
     */
    fun connectToWebSocket(
        accessToken: String = "w8Fg6ggwOje",
    ) {
        val request = Request.Builder()
            .url(socketUrl)
            .addHeader("Authorization", accessToken)
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                startPing()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                val msg = Gson().fromJson(text, SocketMsg::class.java)

                if (msg.`object` == "screen") {
                    screenItems.value = msg.pages.map { Pair(it.conversionTime * 1000L, it.imageUrl) }
                }

                Log.w(TAG, "onMessage: ${msg}")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                reconnectToWebSocket(accessToken)
                Log.w(TAG, "onFailure $t : $response ")
            }
        })
    }

    /**
     * @feature: WebSocket으로 메세지 보내기ㅣ
     * @author: 2023/08/09 4:51 PM donghwishin
     */
    private fun sendMessage(message: String) = webSocket?.send(message)


    /**
     * @feature: 주기적으로 ping 보내기
     * @author: 2023/08/09 4:52 PM donghwishin
     * @description{
     *   3 ~ 4 초마다 한번 씩 보내야한다.
     * }
     */

    private fun startPing() {
        val pingCommand = hashMapOf("command" to "ping")
        val pingMessage = Gson().toJson(pingCommand)
        viewModelScope.launch {
            while (isActive) {
                sendMessage(pingMessage)
                delay(3500)
            }
        }
    }

    /**
     * @feature: 지수 백오프 알고리즘을 사용하여 재연결 시간 계산하기
     *
     * @author: 2023/08/16 2:47 PM donghwishin
    */
    private fun getBackoffTime(attempt: Int): Long {
        val maxBackoff = 60 * 1000L // Maximum backoff time, in milliseconds
        val baseBackoff = 1000L // Initial backoff time, in milliseconds
        val backoffFactor = 2 // Backoff factor

        val calculatedBackoff = baseBackoff * (backoffFactor.toDouble().pow(attempt.coerceAtMost(10))).toLong()

        return calculatedBackoff.coerceAtMost(maxBackoff)
    }

    /**
     * @feature: WebSocket 재연결하기
     *
     * @author: 2023/08/16 2:46 PM donghwishin
    */
    private fun reconnectToWebSocket(accessToken: String) {
        webSocket?.cancel()
        webSocket = null

        viewModelScope.launch {
            delay(getBackoffTime(connectionAttempt))
            connectionAttempt++
            connectToWebSocket(accessToken)
        }
    }



    /**
     * @feature: ViewModel이 종료 시
     *
     * @author: 2023/08/09 4:54 PM donghwishin
     * @description{
     *   1.  WebSocket도 종료시키기
     * }
     */
    override fun onCleared() {
        super.onCleared()
        closeWebSocket()
    }

    private fun closeWebSocket() {
        webSocket?.cancel()
        webSocket?.close(1000, "ViewModel cleared")
        webSocket = null
        connectionAttempt = 0
    }

    companion object{
        private const val TAG = "MenuBossScreenViewModel"
    }
}
