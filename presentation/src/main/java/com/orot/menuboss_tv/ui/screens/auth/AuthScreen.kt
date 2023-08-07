package com.orot.menuboss_tv.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import com.orot.menuboss_tv.ui.theme.colorBackground
import com.orot.menuboss_tv.utils.DeviceInfo


@Composable
fun AuthScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "AUTH CODE: ${DeviceInfo.getMacAddress()}")

//        Image(
//            painter = rememberQrBitmapPainter("AUTH CODE: ${getAndroidId()}}"),
//            contentDescription = "QR Code",
//            contentScale = ContentScale.FillBounds,
//            modifier = Modifier.size(205.dp),
//        )
    }
}