package com.traidingviewer.ui.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.api.model.StockResponse
import com.traidingviewer.ui.base.BaseState
import com.traidingviewer.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(var context: Context) : BaseViewModel() {
    private val mutableSearchedStockLiveData: MutableLiveData<BaseState<List<HomeStock>>> =
        MutableLiveData()
    val searchedStockLiveData = mutableSearchedStockLiveData
    private val mutableSearchingListResultLiveData: MutableLiveData<BaseState<List<StockResponse>>> =
        MutableLiveData()
    val searchingListResultLiveData = mutableSearchingListResultLiveData

    fun searchStocks(searchString: String) {
        viewModelScope.launch {
            val response = getResponse {
                apiService.searchStocks(searchString)
            }
            when (response) {
                is BaseState.Success -> {
                    response.body?.let {
                        loadHomeStocks(mutableSearchedStockLiveData, it)
                    }
                }
                is BaseState.Failure -> mutableSearchingListResultLiveData.postValue(response)
            }
        }
    }
}