package com.traidingviewer.ui.info.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.traidingviewer.data.api.model.Point
import com.traidingviewer.ui.base.BaseState
import com.traidingviewer.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ChartViewModel @Inject constructor() : BaseViewModel() {
    private val mutableChartsListLiveData: MutableLiveData<BaseState<List<Point>>> =
        MutableLiveData()
    val chartsListLiveData = mutableChartsListLiveData

    fun loadCharts(time: String, symbol: String, days: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getCharts(time, symbol)
                val chartsBody = response.body()
                val errorBody = response.errorBody()
                when {
                    response.code() == 403 && errorBody?.string()
                        ?.contains(LIMIT_REACHED) == true -> {
                        mutableChartsListLiveData.postValue(BaseState.Failure.LimitExceeded())
                    }
                    response.isSuccessful && chartsBody != null -> {
                        val listEntry = mutableListOf<Point>()
                        var lastDate = chartsBody.first().date
                        var daysCounter = 0
                        for (chart in chartsBody) {
                            if (!isSameDay(chart.date, lastDate)) {
                                lastDate = chart.date
                                daysCounter++
                            }
                            if (daysCounter < days) {
                                listEntry.add(Point(chart.date, chart.close))
                            } else {
                                break
                            }
                        }
                        mutableChartsListLiveData.postValue(BaseState.Success(listEntry))
                    }
                    else -> {
                        mutableChartsListLiveData.postValue(BaseState.Failure.OtherError())
                    }
                }
            } catch (e: UnknownHostException) {
                mutableChartsListLiveData.postValue(BaseState.Failure.UnknownHostException())
            } catch (e: Exception) {
                mutableChartsListLiveData.postValue(BaseState.Failure.OtherError())
            }
        }
    }

    fun loadChartsDaily(symbol: String, from: String, to: String) {
        viewModelScope.launch {
            try {
                val response =
                    if (from.isNotEmpty() && to.isNotEmpty()) {
                        apiService.getChartsDaily(symbol, from, to)
                    } else {
                        apiService.getChartsDaily(symbol)
                    }
                val chartsBody = response.body()?.historical
                val errorBody = response.errorBody()
                when {
                    response.code() == 403 && errorBody?.string()
                        ?.contains(LIMIT_REACHED) == true -> {
                        mutableChartsListLiveData.postValue(BaseState.Failure.LimitExceeded())
                    }
                    response.isSuccessful && chartsBody != null -> {
                        val listEntry = mutableListOf<Point>()
                        for (chart in chartsBody)
                            listEntry.add(Point(chart.date, chart.close))
                        mutableChartsListLiveData.postValue(BaseState.Success(listEntry))
                    }
                    else -> {
                        mutableChartsListLiveData.postValue(BaseState.Failure.OtherError())
                    }
                }
            } catch (e: UnknownHostException) {
                mutableChartsListLiveData.postValue(BaseState.Failure.UnknownHostException())
            } catch (e: Exception) {
                mutableChartsListLiveData.postValue(BaseState.Failure.OtherError())
            }
        }
    }

    fun isSameDay(currentDate: String, lastDate: String): Boolean {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("EN"))
        val outputDateFormat = SimpleDateFormat("dd", Locale("EN"))
        val currentDay = inputDateFormat.parse(currentDate)
        val lastDay = inputDateFormat.parse(lastDate)
        return if (currentDay != null && lastDay != null) {
            outputDateFormat.format(currentDay) == outputDateFormat.format(lastDay)
        } else {
            false
        }
    }
}