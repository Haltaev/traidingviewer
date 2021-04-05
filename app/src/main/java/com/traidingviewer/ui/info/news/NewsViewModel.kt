package com.traidingviewer.ui.info.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.traidingviewer.data.api.model.CompanyNewsResponse
import com.traidingviewer.ui.base.BaseState
import com.traidingviewer.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

class NewsViewModel @Inject constructor() : BaseViewModel() {
    private val mutableNewsLiveData: MutableLiveData<BaseState<List<CompanyNewsResponse>>> = MutableLiveData()
    val newsLiveData = mutableNewsLiveData

    fun getNews(ticker: String) {
        sendRequest(mutableNewsLiveData) {
            apiService.getCompanyNews(ticker)
        }
    }
}