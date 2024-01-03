package com.orot.menuboss_tv_kr.ui.screens.common.expired

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orot.menuboss_tv_kr.presentation.R
import com.orot.menuboss_tv_kr.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv_kr.ui.theme.AdjustedRegularText
import com.orot.menuboss_tv_kr.utils.adjustedDp


@Composable
fun ExpiredScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AdjustedBoldText(
            text = stringResource(id = R.string.content_expired_title),
            fontSize = adjustedDp(48.dp),
        )

        AdjustedRegularText(
            modifier = Modifier.padding(top = adjustedDp(24.dp)),
            text = "${stringResource(id = R.string.content_expired_description1)}\n${stringResource(id = R.string.content_expired_description2)}",
            fontSize = adjustedDp(20.dp),
        )
    }
}