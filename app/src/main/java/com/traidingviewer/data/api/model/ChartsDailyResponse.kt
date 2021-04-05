package com.traidingviewer.data.api.model

class ChartsDailyResponse(
    val symbol: String,
    val historical: List<ChartsDailyHistorical>
)