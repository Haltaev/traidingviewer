package com.traidingviewer.data.api.model

class ChartsDailyHistorical (
    val date: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val adjClose: Float,
    val volume: Float,
    val unadjustedVolume: Float,
    val change: Float,
    val changePercent: Float,
    val vwap: Float,
    val label: String,
    val changeOverTime: Float
)