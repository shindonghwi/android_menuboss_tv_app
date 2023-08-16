package com.orot.menuboss_tv

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class MainViewModel(
) : ViewModel() {

    val randomData = Random(1200).nextInt()

}