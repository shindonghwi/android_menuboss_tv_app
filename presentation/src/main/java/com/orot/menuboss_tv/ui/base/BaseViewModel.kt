package com.orot.menuboss_tv.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    open fun initState() {
        triggerAuthState(false)
        triggerMenuState(false)
    }

}