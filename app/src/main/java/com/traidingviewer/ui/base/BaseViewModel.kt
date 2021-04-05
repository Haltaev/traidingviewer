package com.traidingviewer.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traidingviewer.data.api.ApiService
import com.traidingviewer.data.api.model.CompanyProfileResponse
import com.traidingviewer.data.api.model.FavoriteStock
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.api.model.StockResponse
import com.traidingviewer.data.db.dao.SymbolDao
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.UnknownHostException
import java.text.DecimalFormat
import javax.inject.Inject

open class BaseViewModel : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var symbolDao: SymbolDao

    fun <T> sendRequest(
        liveData: MutableLiveData<BaseState<T>>,
        request: (suspend () -> Response<T>)
    ) {
        viewModelScope.launch {
            try {
                val response = request.invoke()
                val body = response.body()
                val errorBody = response.errorBody()?.string()
                when {
                    response.code() == 403 && errorBody
                        ?.contains(LIMIT_REACHED) == true -> {
                        liveData.postValue(BaseState.Failure.LimitExceeded())
                    }
                    response.isSuccessful && body != null -> {
                        liveData.postValue(BaseState.Success(body))
                    }
                    else -> {
                        liveData.postValue(BaseState.Failure.OtherError())
                    }
                }
            } catch (e: UnknownHostException) {
                liveData.postValue(BaseState.Failure.UnknownHostException())
            } catch (e: Exception) {
                liveData.postValue(BaseState.Failure.OtherError())
            }
        }
    }

    suspend fun loadHomeStocks(
        liveData: MutableLiveData<BaseState<List<HomeStock>>>,
        stocks: List<StockResponse>
    ) {
        try {
            val list = mutableListOf<HomeStock>()
            var isAllRequestsSuccessful = true
            loop@ for (stock in stocks) {
                val response = apiService.getCompanyProfile(stock.symbol)
                val body = response.body()
                val errorBody = response.errorBody()?.string()
                when {
                    response.code() == 403 && errorBody
                        ?.contains(LIMIT_REACHED) == true -> {
                        liveData.postValue(BaseState.Failure.LimitExceeded())
                        isAllRequestsSuccessful = false
                        break@loop
                    }
                    response.isSuccessful && body != null -> {
                        // Согласно документации, в ответ на запрос ".getCompanyProfile(stockItem.symbol)"
                        // приходит массив с одним элементом, поэтому всегда беру 0-ой элемент
                        if (body.isNotEmpty()) {
                            val profile = body[0]
                            list.add(
                                mixToHomeStock(
                                    stock.name,
                                    profile,
                                    (symbolDao.isFavorite(stock.symbol) != null)
                                )
                            )
                        }
                    }
                    else -> {
                        liveData.postValue(BaseState.Failure.OtherError())
                        isAllRequestsSuccessful = false
                        break@loop
                    }
                }
            }
            if (isAllRequestsSuccessful) liveData.postValue(BaseState.Success(list))
        } catch (e: UnknownHostException) {
            liveData.postValue(BaseState.Failure.UnknownHostException())
        } catch (e: Exception) {
            liveData.postValue(BaseState.Failure.OtherError())
        }
    }

    suspend fun loadFavoritesHomeStocks(
        liveData: MutableLiveData<BaseState<List<HomeStock>>>,
        stocks: List<FavoriteStock>
    ) {
        try {
            val list = mutableListOf<HomeStock>()
            var isAllRequestsSuccessful = true
            loop@ for (stock in stocks) {
                val response = apiService.getCompanyProfile(stock.symbol)
                val body = response.body()
                val errorBody = response.errorBody()?.string()
                when {
                    response.code() == 403 && errorBody
                        ?.contains(LIMIT_REACHED) == true -> {
                        liveData.postValue(BaseState.Failure.LimitExceeded())
                        isAllRequestsSuccessful = false
                        break@loop
                    }
                    response.isSuccessful && body != null -> {
                        // Согласно документации, в ответ на запрос ".getCompanyProfile(stockItem.symbol)"
                        // приходит массив с одним элементом, поэтому всегда беру 0-ой элемент
                        if (body.isNotEmpty()) {
                            val profile = body[0]
                            list.add(
                                mixToHomeStock(
                                    stock.name,
                                    profile,
                                    (symbolDao.isFavorite(stock.symbol) != null)
                                )
                            )
                        }
                    }
                    else -> {
                        liveData.postValue(BaseState.Failure.OtherError())
                        isAllRequestsSuccessful = false
                        break@loop
                    }
                }
            }
            if (isAllRequestsSuccessful) liveData.postValue(BaseState.Success(list))
        } catch (e: UnknownHostException) {
            liveData.postValue(BaseState.Failure.UnknownHostException())
        } catch (e: Exception) {
            liveData.postValue(BaseState.Failure.OtherError())
        }
    }

    suspend fun <T> getResponse(
        request: (suspend () -> Response<T>)
    ): BaseState<T> {
        try {
            val response = request.invoke()
            val body = response.body()
            val errorBody = response.errorBody()?.string()
            return when {
                response.code() == 403 && errorBody?.contains(LIMIT_REACHED) == true -> {
                    BaseState.Failure.LimitExceeded()
                }
                response.isSuccessful && body != null -> {
                    BaseState.Success(body)
                }
                else -> {
                    BaseState.Failure.OtherError()
                }
            }
        } catch (e: UnknownHostException) {
            return BaseState.Failure.UnknownHostException()
        } catch (e: Exception) {
            return BaseState.Failure.OtherError()
        }
    }

    private fun percent(currentPrice: Float?, changes: Float?): String {
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

    private fun mixToHomeStock(
        stockSymbol: String,
        profile: CompanyProfileResponse?,
        isFavorite: Boolean = true
    ): HomeStock {
        val homeStock = HomeStock(
            name = stockSymbol,
            symbol = profile?.symbol ?: "",
            currentPrice = (profile?.price).toString(),
            difference = profile?.changes.toString(),
            currency = profile?.currency ?: "",
            percent = percent(profile?.price, profile?.changes),
            logo = profile?.image ?: ""
        )
        homeStock.isFavorite = isFavorite
        return homeStock
    }

    companion object {
        const val LIMIT_REACHED = "Limit Reach"
    }
}