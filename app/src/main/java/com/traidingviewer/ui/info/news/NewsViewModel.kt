package com.traidingviewer.ui.info.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.traidingviewer.data.api.model.CompanyNewsResponse
import com.traidingviewer.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

sealed class NewsState {
    class Success(val news: List<CompanyNewsResponse>) : NewsState()
    sealed class Failure : NewsState() {
        object LimitExceeded : Failure()
        object UnknownHostException : Failure()
        object OtherError : Failure()
    }
}

class NewsViewModel @Inject constructor() : BaseViewModel() {
    private val mutableNewsLiveData: MutableLiveData<NewsState?> = MutableLiveData()
    val newsLiveData = mutableNewsLiveData

    fun getNews(ticker: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getCompanyNews(ticker)
                val stockBody = response.body()
                if (response.isSuccessful && stockBody != null) {
                    mutableNewsLiveData.postValue(NewsState.Success(stockBody))
                }
            } catch (e: UnknownHostException) {
                mutableNewsLiveData.postValue(NewsState.Failure.UnknownHostException)
            } catch (e: JsonSyntaxException) {
                mutableNewsLiveData.postValue(NewsState.Failure.LimitExceeded)
            } catch (e: Exception) {
                mutableNewsLiveData.postValue(NewsState.Failure.OtherError)
            }
        }
    }
}