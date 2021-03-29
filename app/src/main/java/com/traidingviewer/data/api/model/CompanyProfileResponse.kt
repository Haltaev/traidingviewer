package com.traidingviewer.data.api.model

class CompanyProfileResponse(
    val symbol: String,
    val price: Float,
    val beta: Float,
    val volAvg: Long,
    val mktCap: Long,
    val lastDiv: Float,
    val range: String,
    val changes: Float,
    val companyName: String,
    val currency: String,
    val cik: String,
    val isin: String,
    val cusip: String,
    val exchange: String,
    val exchangeShortName: String,
    val industry: String,
    val website: String,
    val description: String,
    val ceo: String,
    val sector: String,
    val country: String,
    val fullTimeEmployees: String,
    val phone: String,
    val address: String,
    val city: String,
    val state: String,
    val zip: String,
    val dcfDiff: Float,
    val dcf: Float,
    val image: String,
    val ipoDate: String,
    val defaultImage: Boolean,
    val isEtf: Boolean,
    val isActivelyTrading: Boolean
)