package com.traidingviewer.ui.home.viewpagers.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.db.dao.SymbolDao
import com.traidingviewer.ui.base.BaseState
import com.traidingviewer.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteViewModel @Inject constructor() : BaseViewModel() {
    private val mutableListFavoritesInfoLiveData: MutableLiveData<BaseState<List<HomeStock>>> =
        MutableLiveData()
    val listFavoritesInfoLiveData = mutableListFavoritesInfoLiveData

    fun getFavoritesInfo(symbolDao: SymbolDao) {
        viewModelScope.launch {
            loadFavoritesHomeStocks(
                mutableListFavoritesInfoLiveData,
                symbolDao.getAllFavoriteSymbols()
            )
        }
    }
}