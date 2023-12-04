package com.orot.menuboss_tv.ui.screens.common.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orot.menuboss_tv.presentation.R
import com.orot.menuboss_tv.ui.theme.AdjustedBoldText
import com.orot.menuboss_tv.ui.theme.AdjustedRegularText
import com.orot.menuboss_tv.ui.theme.colorGray100
import com.orot.menuboss_tv.utils.adjustedDp


@Composable
fun EmptyContentScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AdjustedBoldText(
            text = stringResource(id = R.string.content_empty_title),
            fontSize = adjustedDp(48.dp),
        )

        AdjustedRegularText(
            modifier = Modifier.padding(top = adjustedDp(24.dp)),
            text = stringResource(id = R.string.content_empty_subtitle),
            fontSize = adjustedDp(20.dp),
            color = colorGray100
        )
    }
}
