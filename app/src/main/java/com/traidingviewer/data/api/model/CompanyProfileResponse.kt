package com.traidingviewer.data.api.model

class CompanyProfileResponse(
    val symbol: String,
    val price: Float,
    val changes: Float,
    val currency: String,
    val state: String,
    val image: String
)