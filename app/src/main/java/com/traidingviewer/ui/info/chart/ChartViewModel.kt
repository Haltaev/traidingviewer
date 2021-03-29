package com.traidingviewer.ui.info.chart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.traidingviewer.data.api.model.Point
import com.traidingviewer.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

sealed class ChartsState {
    class Success(val charts: List<Point>) : ChartsState()
    sealed class Failure : ChartsState() {
        object LimitExceeded : Failure()
        object UnknownHostException : Failure()
        object OtherError : Failure()
    }
}

class ChartViewModel @Inject constructor() : BaseViewModel() {
    private val mutableChartsListLiveData: MutableLiveData<ChartsState> = MutableLiveData()
    val chartsListLiveData = mutableChartsListLiveData

    fun getCharts(time: String, symbol: String, pointsSize: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getCharts(time, symbol)
                val chartsBody = response.body()
                if (response.isSuccessful && chartsBody != null) {
                    val listEntry = mutableListOf<Point>()
                    if(chartsBody.size <= pointsSize) {
                        for (i in chartsBody.indices) {
                            listEntry.add(Point(chartsBody[i].date, chartsBody[i].close))
                        }
                    } else {
                        for (i in 0 until pointsSize) {
                            Log.e("points", chartsBody[i].date )
                            listEntry.add(Point(chartsBody[i].date, chartsBody[i].close))
                        }
                    }
                    mutableChartsListLiveData.postValue(ChartsState.Success(listEntry))
                }
            } catch (e: UnknownHostException) {
                mutableChartsListLiveData.postValue(ChartsState.Failure.UnknownHostException)
            } catch (e: JsonSyntaxException) {
                mutableChartsListLiveData.postValue(ChartsState.Failure.LimitExceeded)
            } catch (e: Exception) {
                mutableChartsListLiveData.postValue(ChartsState.Failure.OtherError)
            }
        }
    }

    fun getChartsDaily(symbol: String, from: String, to: String) {
        viewModelScope.launch {
            try {
                val response =
                    if (from.isNotEmpty() && to.isNotEmpty()) {
                        apiService.getChartsDaily(symbol, from, to)
                    } else {
                        apiService.getChartsDaily(symbol)
                    }
                val chartsBody = response.body()?.historical
                if (response.isSuccessful && chartsBody != null) {
                    val listEntry = mutableListOf<Point>()
                    for (i in chartsBody.indices)
                        listEntry.add(Point(chartsBody[i].date, chartsBody[i].close))
                    mutableChartsListLiveData.postValue(ChartsState.Success(listEntry))
                }
            } catch (e: UnknownHostException) {
                mutableChartsListLiveData.postValue(ChartsState.Failure.UnknownHostException)
            } catch (e: JsonSyntaxException) {
                mutableChartsListLiveData.postValue(ChartsState.Failure.LimitExceeded)
            } catch (e: Exception) {
                mutableChartsListLiveData.postValue(ChartsState.Failure.OtherError)
            }
        }
    }
}