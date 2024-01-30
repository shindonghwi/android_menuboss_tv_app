package com.orot.menuboss_tv_kr.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.min
import kotlin.math.pow

open class BaseViewModel : ViewModel() {

    companion object {
        private const val TAG = "BaseViewModel"
    }

    /**
     * @feature: 인증화면으로 이동하는 기능
     * @author: 2023/10/15 12:40 PM donghwishin
     */
    private val _navigateToAuthState = MutableStateFlow(false)
    val navigateToAuthState: StateFlow<Boolean> get() = _navigateToAuthState
    fun triggerAuthState(flag: Boolean) = run {
        Log.d(TAG, "triggerAuthState: $flag")
        _navigateToAuthState.value = flag
    }

    /**
     * @feature: 메뉴화면으로 이동하는 기능
     * @author: 2023/10/15 12:41 PM donghwishin
     */
    private val _navigateToMenuState = MutableStateFlow(false)
    val navigateToMenuState: StateFlow<Boolean> get() = _navigateToMenuState
    fun triggerMenuState(flag: Boolean) = run {
        Log.d(TAG, "triggerMenuState: $flag")
        _navigateToMenuState.value = flag
    }

    /**
     * @feature: 지수 백오프 알고리즘을 사용하여 재시도 지연 시간을 계산합니다.
     * @author: 2023/11/11 2:44 PM donghwishin
     */
    fun calculateDelay(attempt: Int): Long {
        val maxDelay = 10000L  // 최대 지연 시간 (예: 30초)
        val delay = (1.3.pow(attempt.toDouble()) * 1000L).toLong() // 지수 백오프
        return min(delay, maxDelay)
    }

    open fun initState() {
        triggerAuthState(false)
        triggerMenuState(false)
    }

}