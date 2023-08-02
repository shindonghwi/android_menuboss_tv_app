package com.orot.menuboss_tv.ui.screens.auth

import android.content.ContentResolver
import android.mtp.MtpDeviceInfo
import android.provider.Settings
import android.provider.Settings.Secure
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Text
import com.orot.menuboss_tv.ui.theme.colorBackground


@Composable
fun AuthScreen() {

    val context = LocalContext.current
//    val deviceId = Settings.Secure.getString(context.contentResolver, Secure.ANDROID_ID)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "AUTH CODE:")

//        Image(
//            painter = rememberQrBitmapPainter("AUTH CODE: ${getAndroidId()}}"),
//            contentDescription = "QR Code",
//            contentScale = ContentScale.FillBounds,
//            modifier = Modifier.size(205.dp),
//        )
    }
}