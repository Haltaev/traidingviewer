package com.traidingviewer.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.traidingviewer.App
import com.traidingviewer.MainActivity
import com.traidingviewer.R
import com.traidingviewer.common.injectViewModel
import com.traidingviewer.data.api.model.FavoriteStock
import com.traidingviewer.data.api.model.HomeStock
import com.traidingviewer.data.db.dao.PopularTickerDao
import com.traidingviewer.data.db.dao.SearchedTickersDao
import com.traidingviewer.data.db.dao.SymbolDao
import com.traidingviewer.data.db.entities.SearchedTickers
import com.traidingviewer.ui.base.BaseFragment
import com.traidingviewer.ui.base.BaseState
import com.traidingviewer.ui.home.viewpagers.HomePagesAdapter
import com.traidingviewer.ui.home.viewpagers.OnItemClickListener
import com.traidingviewer.ui.info.TickerInfoFragment
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragment : BaseFragment(), OnItemClickListener, OnSearchQueriesClickListener {

    @Inject
    lateinit var searchedTickersDao: SearchedTickersDao

    @Inject
    lateinit var popularTickerDao: PopularTickerDao

    @Inject
    lateinit var symbolDao: SymbolDao

    private var imm: InputMethodManager? = null
    private lateinit var viewModel: SearchViewModel
    private var exampleTickersAdapter: SearchAdapter? = null
    private var searchResultAdapter: HomePagesAdapter? = null
    var searchedString = ""

    val handlerSearch = Handler(Looper.getMainLooper())

    override fun getLayoutId(): Int {
        return R.layout.fragment_search
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        App.getComponent().inject(this)
    }

    override fun onResume() {
        super.onResume()
        searchEditText.requestFocus()
        imm = getSystemService<InputMethodManager>(requireContext(), InputMethodManager::class.java)
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel(viewModel)

        viewLifecycleOwner.lifecycleScope.launch {
            setPopularRequests(popularTickerDao.loadAllPopularTickers())
            setSearchedRequests(searchedTickersDao.loadAllSearchedTickers())
        }

        searchBackButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                handlerSearch.removeCallbacksAndMessages(null)
                if (s.toString().isNotBlank()) {
                    if (s.toString().length > searchedString.length) searchedString = s.toString()
                    handlerSearch
                        .postDelayed({
                            changeStateSearchProgress(true)
                            viewModel.searchStocks(s.toString())
                        }, 700)
                } else {
                    showPreview()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        searchEditText.addTextChangedListener(textWatcher)

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                searchEditText.text.clear()
            }
        }
    }

    fun changeStateSearchProgress(toShow: Boolean) {
        if (toShow) searchProgressBar.visibility = View.VISIBLE
        else searchProgressBar.visibility = View.GONE
    }

    private fun reverseList(list: List<String>): List<String> {
        val outList = mutableListOf<String>()
        for (i in list.lastIndex downTo 0) {
            outList.add(list[i])
        }
        return outList
    }

    private fun setPopularRequests(list: List<String>) {
        exampleTickersAdapter = SearchAdapter(list, this)

        popularSearchRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        popularSearchRecyclerView.adapter = exampleTickersAdapter
    }

    private fun setSearchedRequests(list: List<String>) {
        if (list.isEmpty()) {
            searchedItemsTitle.visibility = View.GONE
        } else {
            searchedItemsTitle.visibility = View.VISIBLE
        }
        exampleTickersAdapter = SearchAdapter(reverseList(list), this)

        searchedRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        searchedRecyclerView.adapter = exampleTickersAdapter
    }

    private fun observeViewModel(viewModel: SearchViewModel) {
        viewModel.apply {
            searchedStockLiveData.observe(viewLifecycleOwner, Observer { resp ->
                changeStateSearchProgress(false)
                when (resp) {
                    is BaseState.Success -> {
                        resp.body?.let {
                            if (it.isNotEmpty()) setResultList(it)
                            else showEmptySearchResult()
                        }
                    }
                    is BaseState.Failure -> {
                        showErrorToasts(resp)
                    }
                }
            })
            searchingListResultLiveData.observe(viewLifecycleOwner, Observer { resp ->
                when (resp) {
                    is BaseState.Success -> {
                    }
                    is BaseState.Failure -> {
                        changeStateSearchProgress(false)
                        showErrorToasts(resp)
                    }
                }
            })
        }
    }

    private fun showEmptySearchResult() {
        nothingShowText.visibility = View.VISIBLE
        searchPreviewLayout.visibility = View.GONE
        searchResultRecyclerView.visibility = View.GONE
    }

    fun showPreview() {
        nothingShowText.visibility = View.GONE
        searchPreviewLayout.visibility = View.VISIBLE
        searchResultRecyclerView.visibility = View.GONE
    }

    private fun setResultList(list: List<HomeStock>) {
        nothingShowText.visibility = View.GONE
        searchPreviewLayout.visibility = View.GONE
        searchResultRecyclerView.visibility = View.VISIBLE

        searchResultAdapter = HomePagesAdapter(list, this)

        searchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchResultRecyclerView.adapter = searchResultAdapter
    }

    override fun onItemClickListener(symbol: String, name: String, isFavorite: Boolean) {
        val arguments = Bundle()
        arguments.putString(TickerInfoFragment.SYMBOL, symbol)
        arguments.putString(TickerInfoFragment.NAME, name)
        arguments.putBoolean(TickerInfoFragment.IS_FAVORITE, isFavorite)
        activity?.supportFragmentManager?.popBackStack()
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

    override fun onStop() {
        handlerSearch.removeCallbacksAndMessages(null)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        viewLifecycleOwner.lifecycleScope.launch {
            if (searchedString.isNotBlank() && !searchedTickersDao.loadAllSearchedTickers()
                    .contains(searchedString)
            ) {
                searchedTickersDao.insert(SearchedTickers(searchedString))
            }
        }
        super.onStop()
    }

    override fun onSearchQueryClickListener(query: String) {
        searchEditText.setText(query)
        changeStateSearchProgress(true)
        viewModel.searchStocks(query)
        viewLifecycleOwner.lifecycleScope.launch {
            if (searchedTickersDao.loadAllSearchedTickers().contains(query)) {
                searchedTickersDao.deleteTicker(SearchedTickers(query))
            }
            searchedTickersDao.insert(SearchedTickers(query))
        }
    }
}