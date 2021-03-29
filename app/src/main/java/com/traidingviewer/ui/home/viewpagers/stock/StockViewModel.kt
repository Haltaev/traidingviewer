package com.traidingviewer.ui.home.viewpagers.stock

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.api.model.StockResponse
import com.traidingviewer.data.db.dao.SymbolDao
import com.traidingviewer.viewmodel.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

sealed class StockState {
    class Success(val stocks: List<StockResponse>) : StockState()
    sealed class Failure : StockState() {
        object LimitExceeded : Failure()
        object UnknownHostException : Failure()
        object OtherError : Failure()
    }
}

sealed class StocksInfoState {
    class Success(val stocksInfo: List<HomeStock>) : StocksInfoState()
    sealed class Failure : StocksInfoState() {
        object LimitExceeded : Failure()
        object UnknownHostException : Failure()
        object OtherError : Failure()
    }
}

class StockViewModel @Inject constructor() : BaseViewModel() {

    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }

    private val mutableListStockInfoLiveData: MutableLiveData<StocksInfoState?> = MutableLiveData()
    val listStockInfoLiveData = mutableListStockInfoLiveData

    private val mutableStocksListLiveData: MutableLiveData<StockState?> = MutableLiveData()
    val stockListLiveData = mutableStocksListLiveData

    fun getStocks() {
        viewModelScope.launch(job) {
            try {
                val response = apiService.getStocks()
                val stockBody = response.body()
                if (response.isSuccessful && stockBody != null) {
                    mutableStocksListLiveData.postValue(StockState.Success(stockBody))
                }
            } catch (e: UnknownHostException) {
                mutableStocksListLiveData.postValue(StockState.Failure.UnknownHostException)
            } catch (e: JsonSyntaxException) {
                mutableStocksListLiveData.postValue(StockState.Failure.LimitExceeded)
            } catch (e: Exception) {
                mutableStocksListLiveData.postValue(StockState.Failure.OtherError)
            }
        }
    }

    fun getStocksInfo(symbolsList: List<StockResponse>, symbolDao: SymbolDao) {
        viewModelScope.launch(job) {
            try {
                val list = mutableListOf<HomeStock>()
                for (stockItem in symbolsList) {
                    val responseProfile = apiService.getCompanyProfile(stockItem.symbol)
                    if (!responseProfile.body().isNullOrEmpty()) {
                        val profile = responseProfile.body()!![0]
                        val homeStock = HomeStock()
                        homeStock.name = stockItem.name
                        homeStock.symbol = stockItem.symbol
                        homeStock.currentPrice = (profile.price).toString()
                        homeStock.difference = profile.changes.toString()
                        homeStock.currency = profile.currency
                        homeStock.percent = percent(profile.price, profile.changes)
                        homeStock.logo = profile.image
                        symbolDao.loadAllSymbols().forEach {
                            if(it.symbol == homeStock.symbol) {
                                homeStock.isFavorite = true
                            }
                        }
                        list.add(homeStock)
                    }
                }
                mutableListStockInfoLiveData.postValue(StocksInfoState.Success(list))
            } catch (e: UnknownHostException) {
                mutableListStockInfoLiveData.postValue(StocksInfoState.Failure.UnknownHostException)
            } catch (e: JsonSyntaxException) {
                mutableListStockInfoLiveData.postValue(StocksInfoState.Failure.LimitExceeded)
            } catch (e: Exception) {
                mutableListStockInfoLiveData.postValue(StocksInfoState.Failure.OtherError)
            }
        }
    }

    fun cancelAll() {
        job.cancel()
        mutableStocksListLiveData.postValue(null)
        mutableListStockInfoLiveData.postValue(null)
    }
}