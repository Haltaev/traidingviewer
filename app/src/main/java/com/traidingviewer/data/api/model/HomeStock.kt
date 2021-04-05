package com.traidingviewer.data.api.model

class HomeStock(
    val name: String,
    val symbol: String,
    val currentPrice: String,
    val difference: String,
    val percent: String,
    val currency: String,
    val logo: String
) {
    var isFavorite: Boolean = false

    companion object {
        val EMPTY_HOME_STOCK = HomeStock(
            name = "",
            symbol = "",
            currentPrice = "",
            difference = "",
            percent = "",
            currency = "",
            logo = ""
        )
    }
}