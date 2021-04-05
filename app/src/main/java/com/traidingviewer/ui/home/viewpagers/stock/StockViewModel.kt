package com.traidingviewer.ui.home.viewpagers.stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.api.model.StockResponse
import com.traidingviewer.ui.base.BaseState
import com.traidingviewer.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class StockViewModel @Inject constructor() : BaseViewModel() {

    private val mutableListStockInfoLiveData: MutableLiveData<BaseState<List<HomeStock>>> =
        MutableLiveData()
    val listStockInfoLiveData = mutableListStockInfoLiveData

    private val mutableStocksListLiveData: MutableLiveData<BaseState<List<StockResponse>>> =
        MutableLiveData()
    val stockListLiveData = mutableStocksListLiveData

    fun loadStocks() {
        sendRequest(mutableStocksListLiveData) {
            apiService.getStocks()
        }
    }

    fun loadStocksInfo(stocks: List<StockResponse>) {
        viewModelScope.launch {
            loadHomeStocks(mutableListStockInfoLiveData, stocks)
        }
    }
}