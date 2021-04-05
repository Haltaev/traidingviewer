package com.traidingviewer.ui.home.viewpagers.stock

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.traidingviewer.App
import com.traidingviewer.MainActivity
import com.traidingviewer.R
import com.traidingviewer.common.injectViewModel
import com.traidingviewer.data.api.model.FavoriteStock
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.api.model.StockResponse
import com.traidingviewer.data.db.dao.SymbolDao
import com.traidingviewer.ui.base.BaseFragment
import com.traidingviewer.ui.base.BaseState
import com.traidingviewer.ui.home.viewpagers.HomePagesAdapter
import com.traidingviewer.ui.home.viewpagers.OnItemClickListener
import com.traidingviewer.ui.info.TickerInfoFragment
import kotlinx.android.synthetic.main.fragment_home_list.*
import kotlinx.android.synthetic.main.view_progress.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class StockFragment : BaseFragment(),
    OnItemClickListener {

    private var adapter: HomePagesAdapter? = null
    private var stockTickers = mutableListOf<HomeStock>()

    var baseStockList = listOf<StockResponse>()

    // variables for pagination
    private var startPoint = 0
    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 5
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0

    private lateinit var viewModel: StockViewModel

    @Inject
    lateinit var symbolDao: SymbolDao

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_list
    }

    override fun onStart() {
        super.onStart()
        observeViewModel(viewModel)
        if (stockTickers.isEmpty()) {
            showProgressBar(progressBar)
            viewModel.loadStocks()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        App.getComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomePagesAdapter(stockTickers, this)
        val mLayoutManager = LinearLayoutManager(requireContext())
        pagerRecyclerView.layoutManager = mLayoutManager
        pagerRecyclerView.adapter = adapter

        pagerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount = pagerRecyclerView.childCount
                totalItemCount = mLayoutManager.itemCount
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount + 1
                    }
                }
                if (!loading && totalItemCount - visibleItemCount
                    <= firstVisibleItem + visibleThreshold
                ) {
                    showRecyclerProgress()
                    viewModel.loadStocksInfo(
                        baseStockList.subList(startPoint, getNewEndPoint(startPoint))
                    )
                    loading = true
                }
            }
        })
    }

    private fun observeViewModel(viewModel: StockViewModel) {
        viewModel.apply {
            stockListLiveData.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    is BaseState.Success -> {
                        state.body?.let {
                            baseStockList = it
                            loadStocksInfo(
                                baseStockList.subList(startPoint, getNewEndPoint(startPoint))
                            )
                        }
                    }
                    is BaseState.Failure -> {
                        hideProgressBar(progressBar)
                        showErrorToasts(state)
                    }
                }
            })
            listStockInfoLiveData.observe(viewLifecycleOwner, Observer { state ->
                if (state != null) {
                    hideProgressBar(progressBar)
                    when (state) {
                        is BaseState.Success -> {
                            state.body?.let {
                                if (stockTickers.isNotEmpty()) hideRecyclerProgress()
                                stockTickers.addAll(it)
                                startPoint = getNewEndPoint(startPoint) + 1
                                pagerRecyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                        is BaseState.Failure -> {
                            showErrorToasts(state)
                        }
                    }
                    listStockInfoLiveData.postValue(null)
                }
            })
        }
    }

    fun showRecyclerProgress() {
        stockTickers.add(HomeStock.EMPTY_HOME_STOCK)
        pagerRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun hideRecyclerProgress() {
        if (stockTickers.last().symbol.isBlank()) stockTickers.removeAt(stockTickers.lastIndex)
    }

    companion object {
        internal fun newInstance(): StockFragment {
            return StockFragment()
        }
    }

    private fun getNewEndPoint(lastEndPoint: Int): Int {
        if ((lastEndPoint + 16) >= baseStockList.size) {
            return baseStockList.lastIndex
        }
        return lastEndPoint + 16
    }

    override fun onItemClickListener(symbol: String, name: String, isFavorite: Boolean) {
        val arguments = Bundle()
        arguments.putString(TickerInfoFragment.SYMBOL, symbol)
        arguments.putString(TickerInfoFragment.NAME, name)
        arguments.putBoolean(TickerInfoFragment.IS_FAVORITE, isFavorite)
        (activity as MainActivity).openFragmentWithBackStack(TickerInfoFragment(), arguments)
    }

    override fun onFavoriteStarClickListener(item: HomeStock, state: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (state) {
                symbolDao.insert(FavoriteStock(item.symbol, item.name))
            } else {
                symbolDao.deleteTicker(FavoriteStock(item.symbol, item.name))
            }
        }
    }
}