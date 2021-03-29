package com.traidingviewer.ui.home.viewpagers.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.db.dao.SymbolDao
import com.traidingviewer.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

sealed class FavoritesInfoState {
    class Success(val stocksInfo: List<HomeStock>) : FavoritesInfoState()
    sealed class Failure : FavoritesInfoState() {
        object LimitExceeded : Failure()
        object UnknownHostException : Failure()
        object OtherError : Failure()
    }
}

class FavoriteViewModel @Inject constructor() : BaseViewModel() {
    private val mutableListFavoritesInfoLiveData: MutableLiveData<FavoritesInfoState> =
        MutableLiveData()
    val listFavoritesInfoLiveData = mutableListFavoritesInfoLiveData

    fun getFavoritesInfo(symbolDao: SymbolDao) {
        viewModelScope.launch {
            try {
                val symbolsList = symbolDao.loadAllSymbols()
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
                            if (it.symbol == homeStock.symbol) {
                                homeStock.isFavorite = true
                            }
                        }
                        list.add(homeStock)
                    }
                }
                mutableListFavoritesInfoLiveData.postValue(FavoritesInfoState.Success(list))
                return@launch
            } catch (e: UnknownHostException) {
                mutableListFavoritesInfoLiveData.postValue(FavoritesInfoState.Failure.UnknownHostException)
            } catch (e: JsonSyntaxException) {
                mutableListFavoritesInfoLiveData.postValue(FavoritesInfoState.Failure.LimitExceeded)
            } catch (e: Exception) {
                mutableListFavoritesInfoLiveData.postValue(FavoritesInfoState.Failure.OtherError)
            }
        }
    }
}