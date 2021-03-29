package com.traidingviewer.data.api.model

class ChartsResponse(
    val date: String,
    val open: Float,
    val low: Float,
    val high: Float,
    val close: Float,
    val volume: Long
) {
    companion object {
        const val FREQUENCY_POINTS_5_MIN = "5min"
        const val FREQUENCY_POINTS_1_HOUR = "1hour"
    }
}
