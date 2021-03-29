package com.traidingviewer.data.api.model

class CompanyNewsResponse(
    val symbol: String,
    val publishedDate: String,
    val title: String,
    val image: String,
    val site: String,
    val text: String,
    val url: String
) {
    var isPicked = false
}