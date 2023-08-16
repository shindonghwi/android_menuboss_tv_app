package com.orot.menuboss_tv.ui.screens.menu_board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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

@HiltViewModel
class MenuBoardScreenViewModel @Inject constructor(
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main
    private val job = Job()
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    /**
     * @feature: WebSocket 연결하기
     * @author: 2023/08/09 4:56 PM donghwishin
     */
    fun connectToWebSocket(
        accessToken: String = "w8Fg6ggwOje",
        socketUrl: String = "wss://dev-screen.menuboss.tv/v1/stream"
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
                Log.w("Asdasdasd", "onMessage $text: ")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.w("Asdasdasd", "onFailure $t : $response ")
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
     * @feature: ViewModel이 종료 시
     *
     * @author: 2023/08/09 4:54 PM donghwishin
     * @description{
     *   1.  WebSocket도 종료시키기
     * }
     */
    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, "ViewModel cleared")
    }

}
