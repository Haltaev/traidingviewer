package com.traidingviewer.viewmodel

import androidx.lifecycle.ViewModel
import com.traidingviewer.data.api.ApiService
import java.text.DecimalFormat
import javax.inject.Inject

open class BaseViewModel : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    fun percent(currentPrice: Float?, changes: Float?): String {
        if (currentPrice == null || changes == null) {
            return "0"
        } else if (currentPrice.isNaN() || changes.isNaN()) {
            return "0"
        }
        val previousPrice = currentPrice - changes
        return if (currentPrice >= previousPrice) {
            if (currentPrice == 0f) "0"
            else DecimalFormat("##.##").format((1 - previousPrice / currentPrice) * 100)
        } else {
            if (previousPrice == 0f) "0"
            else DecimalFormat("##.##").format((1 - currentPrice / previousPrice) * 100)
        }
    }
}