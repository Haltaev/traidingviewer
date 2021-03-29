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
import com.traidingviewer.ui.BaseFragment
import com.traidingviewer.ui.home.viewpagers.HomePagesAdapter
import com.traidingviewer.ui.home.viewpagers.OnItemClickListener
import com.traidingviewer.ui.info.TickerInfoFragment
import kotlinx.android.synthetic.main.fragment_home_list.*
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
            showProgressBar()
            viewModel.getStocks()
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
                    viewModel.getStocksInfo(
                        baseStockList.subList(startPoint, getNewEndPoint(startPoint)),
                        symbolDao
                    )
                    loading = true
                }
            }
        })
    }

    private fun observeViewModel(viewModel: StockViewModel) {
        viewModel.apply {
            stockListLiveData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    when (it) {
                        is StockState.Success -> {
                            baseStockList = it.stocks
                            getStocksInfo(
                                baseStockList.subList(startPoint, getNewEndPoint(startPoint)),
                                symbolDao
                            )
                        }
                        StockState.Failure.UnknownHostException -> {
                            hideProgressBar()
                            showToast(R.string.error_unknown_host)
                        }
                        StockState.Failure.LimitExceeded -> {
                            hideProgressBar()
                            showToast(R.string.error_limit_exceeded)
                        }
                        StockState.Failure.OtherError -> {
                            hideProgressBar()
                            showToast(R.string.error_some_error)
                        }
                    }
                    stockListLiveData.postValue(null)
                }
            })
            listStockInfoLiveData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    hideProgressBar()
                    when (it) {
                        is StocksInfoState.Success -> {
                            if (stockTickers.isNotEmpty()) hideRecyclerProgress()
                            stockTickers.addAll(it.stocksInfo)
                            startPoint = getNewEndPoint(startPoint) + 1
                            pagerRecyclerView.adapter?.notifyDataSetChanged()
                        }
                        StocksInfoState.Failure.UnknownHostException -> {
                            showToast(R.string.error_unknown_host)
                        }
                        StocksInfoState.Failure.LimitExceeded -> {
                            showToast(R.string.error_limit_exceeded)
                        }
                        StocksInfoState.Failure.OtherError -> {
                            showToast(R.string.error_some_error)
                        }
                    }
                    listStockInfoLiveData.postValue(null)
                }
            })
        }
    }

    override fun onStop() {
        removeObservers(viewModel)
        viewModel.cancelAll()
        super.onStop()
    }

    private fun removeObservers(viewModel: StockViewModel) {
        viewModel.apply {
            listStockInfoLiveData.removeObservers(viewLifecycleOwner)
            stockListLiveData.removeObservers(viewLifecycleOwner)
        }
    }

    fun showRecyclerProgress() {
        stockTickers.add(HomeStock())
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

    private fun showProgressBar() {
        circularProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        circularProgressBar.visibility = View.GONE
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
        if (state) {
            viewLifecycleOwner.lifecycleScope.launch {
                symbolDao.insert(
                    FavoriteStock(
                        item.symbol,
                        item.name ?: ""
                    )
                )
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                symbolDao.deleteTicker(
                    FavoriteStock(
                        item.symbol,
                        item.name ?: ""
                    )
                )
            }
        }
    }
}