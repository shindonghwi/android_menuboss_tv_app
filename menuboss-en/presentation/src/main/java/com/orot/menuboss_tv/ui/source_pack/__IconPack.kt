package com.orot.menuboss_tv.ui.source_pack

import androidx.compose.ui.graphics.vector.ImageVector
import com.orot.menuboss_tv.ui.source_pack.iconpack.Logo
import kotlin.collections.List as ____KtList

public object IconPack

private var __Logo: ____KtList<ImageVector>? = null

public val IconPack.Logo: ____KtList<ImageVector>
  get() {
    if (__Logo != null) {
      return __Logo!!
    }
    __Logo= listOf(Logo)
    return __Logo!!
  }
