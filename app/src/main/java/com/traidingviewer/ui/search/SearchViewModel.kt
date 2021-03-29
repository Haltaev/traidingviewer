package com.traidingviewer.ui.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.db.dao.SearchedTickersDao
import com.traidingviewer.data.db.entities.SearchedTickers
import com.traidingviewer.ui.home.viewpagers.stock.StockViewModel
import com.traidingviewer.viewmodel.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

sealed class SearchState {
    class Success(val stocks: List<HomeStock>) : SearchState()
    sealed class Failure : SearchState() {
        object NothingToShow : Failure()
        object LimitExceeded : Failure()
        object UnknownHostException : Failure()
        object OtherError : Failure()
    }
}

class SearchViewModel @Inject constructor(var context: Context) : BaseViewModel() {
    private val mutableSearchedStockLiveData: MutableLiveData<SearchState> = MutableLiveData()
    val searchedStockLiveData = mutableSearchedStockLiveData

    fun searchStocks(searchString: String) {
        viewModelScope.launch() {
            try {
                val list = mutableListOf<HomeStock>()
                val response = apiService.searchStocks(searchString)
                val searchResult = response.body()
                if (searchResult.isNullOrEmpty()) {
                    mutableSearchedStockLiveData.postValue(SearchState.Failure.NothingToShow)
                    return@launch
                }
                if (response.isSuccessful && !searchResult.isNullOrEmpty()) {
                    for (stockItem in searchResult) {
                        val responseProfile = apiService.getCompanyProfile(stockItem.symbol)

                        if (responseProfile.body() != null) {
                            val profile = responseProfile.body()!![0]
                            val homeStock = HomeStock()
                            homeStock.name = stockItem.name
                            homeStock.symbol = stockItem.symbol
                            homeStock.currentPrice = profile.price.toString()
                            homeStock.difference = profile.changes.toString()
                            homeStock.percent = percent(profile.price, profile.changes)
                            homeStock.logo = profile.image
                            list.add(homeStock)
                        }
                    }
                    mutableSearchedStockLiveData.postValue(SearchState.Success(list))
                }
            } catch (e: UnknownHostException) {
                mutableSearchedStockLiveData.postValue(SearchState.Failure.UnknownHostException)
            } catch (e: JsonSyntaxException) {
                mutableSearchedStockLiveData.postValue(SearchState.Failure.LimitExceeded)
            } catch (e: Exception) {
                mutableSearchedStockLiveData.postValue(SearchState.Failure.OtherError)
            }
        }
    }
}