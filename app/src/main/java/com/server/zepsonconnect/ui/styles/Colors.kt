package com.server.zepsonconnect.ui.styles

import android.graphics.Color

val com.server.zepsonconnect.domain.ProcessingState.color: Int
    get() = when (this) {
        com.server.zepsonconnect.domain.ProcessingState.Pending -> Color.parseColor("#FFBB86FC")
        com.server.zepsonconnect.domain.ProcessingState.Processed -> Color.parseColor("#FF6200EE")
        com.server.zepsonconnect.domain.ProcessingState.Sent -> Color.parseColor("#FF3700B3")
        com.server.zepsonconnect.domain.ProcessingState.Delivered -> Color.parseColor("#FF03DAC5")
        com.server.zepsonconnect.domain.ProcessingState.Failed -> Color.parseColor("#FF018786")
    }