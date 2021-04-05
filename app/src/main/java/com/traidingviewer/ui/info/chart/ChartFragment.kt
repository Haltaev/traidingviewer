package com.traidingviewer.ui.info.chart

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.traidingviewer.R
import com.traidingviewer.common.injectViewModel
import com.traidingviewer.data.api.model.ChartsResponse.Companion.FREQUENCY_POINTS_1_HOUR
import com.traidingviewer.data.api.model.ChartsResponse.Companion.FREQUENCY_POINTS_5_MIN
import com.traidingviewer.data.api.model.MyEntry
import com.traidingviewer.data.api.model.Point
import com.traidingviewer.ui.base.BaseFragment
import com.traidingviewer.ui.base.BaseState
import kotlinx.android.synthetic.main.fragment_ticker_info_chart.*
import java.text.SimpleDateFormat
import java.util.*


class ChartFragment : BaseFragment() {
    private var symbol = ""

    private lateinit var viewModel: ChartViewModel

    override fun getLayoutId(): Int {
        return R.layout.fragment_ticker_info_chart
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        val bundle = this.arguments
        if (bundle != null) {
            symbol = bundle.getString(SYMBOL, "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel(viewModel)
        changeButtonsState(buttonD)
        viewModel.loadCharts(FREQUENCY_POINTS_5_MIN, symbol, 1)

        startDrawChart()
        buttonD.setOnClickListener {
            changeButtonsState(buttonD)
            viewModel.loadCharts(FREQUENCY_POINTS_5_MIN, symbol, 1)
        }
        buttonW.setOnClickListener {
            changeButtonsState(buttonW)
            viewModel.loadCharts(FREQUENCY_POINTS_1_HOUR, symbol, 7)
        }
        buttonM.setOnClickListener {
            changeButtonsState(buttonM)
            val currentTime = System.currentTimeMillis()
            val monthAgo = currentTime - 30 * 24 * 60 * 60 * 1000L
            val pairRange = getRange(monthAgo, currentTime)
            viewModel.loadChartsDaily(symbol, pairRange.first, pairRange.second)
        }
        button6m.setOnClickListener {
            changeButtonsState(button6m)
            val currentTime = System.currentTimeMillis()
            val monthAgo = currentTime - 6 * 30 * 24 * 60 * 60 * 1000L
            val pairRange = getRange(monthAgo, currentTime)
            viewModel.loadChartsDaily(symbol, pairRange.first, pairRange.second)
        }
        button1y.setOnClickListener {
            changeButtonsState(button1y)
            val currentTime = System.currentTimeMillis()
            val monthAgo = currentTime - 365 * 24 * 60 * 60 * 1000L
            val pairRange = getRange(monthAgo, currentTime)
            viewModel.loadChartsDaily(symbol, pairRange.first, pairRange.second)
        }
        buttonAll.setOnClickListener {
            changeButtonsState(buttonAll)
            viewModel.loadChartsDaily(symbol, "", "")
        }
    }

    private fun observeViewModel(viewModel: ChartViewModel) {
        viewModel.apply {
            chartsListLiveData.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    is BaseState.Success -> {
                        state.body?.let {
                            setData(it)
                        }
                    }
                    is BaseState.Failure -> {
                        showErrorToasts(state)
                    }
                }
            })
        }
    }

    private fun Long.toStringDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale("EN"))

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return formatter.format(calendar.time)
    }

    private fun getRange(from: Long, to: Long): Pair<String, String> {
        return Pair(from.toStringDate(), to.toStringDate())
    }

    private fun TextView.setDefaultState() {
        this.isEnabled = true
        this.setTextColor(requireContext().resources.getColor(R.color.colorBlackLight, null))
    }

    private fun changeButtonsState(view: TextView) {
        buttonD.setDefaultState()
        buttonW.setDefaultState()
        buttonM.setDefaultState()
        button6m.setDefaultState()
        button1y.setDefaultState()
        buttonAll.setDefaultState()
        view.isEnabled = false
        view.setTextColor(requireContext().resources.getColor(R.color.white, null))
    }

    private fun startDrawChart() {
        chart.setViewPortOffsets(0f, 150f, 0f, 0f)
        chart.setBackgroundColor(resources.getColor(R.color.white, null))
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.xAxis.isEnabled = false

        val mv = MyMarkerView(requireContext())
        mv.chartView = chart
        chart.marker = mv

        chart.legend.isEnabled = false
        chart.animateXY(2000, 2000)
    }

    private fun setData(values: List<Point>) {
        val set1: LineDataSet
        val list = mutableListOf<Entry>()
        for ((x, i) in (values.lastIndex - 1 downTo 0).withIndex()) {
            list.add(MyEntry(x, values[i]))
        }
        set1 = LineDataSet(list, "DataSet 1")
        set1.color = Color.BLACK
        set1.setDrawCircleHole(false)
        set1.setDrawIcons(false)
        set1.setDrawHighlightIndicators(false)
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.setDrawFilled(true)
        set1.setDrawCircles(true)
        set1.lineWidth = 1.8f
        set1.setCircleColor(Color.BLACK)
        set1.color = Color.BLACK
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_black)
        set1.fillDrawable = drawable
        set1.setDrawHighlightIndicators(false)
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.setDrawValues(false)
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)
        val lineData = LineData(dataSets)
        chart.data = lineData
        chart.invalidate()
    }

    companion object {
        const val SYMBOL = "symbol"
        internal fun newInstance(symbol: String): ChartFragment {
            val pageFragment =
                ChartFragment()
            val arguments = Bundle()
            arguments.putString(SYMBOL, symbol)
            pageFragment.arguments = arguments
            return pageFragment
        }
    }
}


