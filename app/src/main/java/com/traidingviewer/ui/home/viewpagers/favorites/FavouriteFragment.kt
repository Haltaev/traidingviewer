package com.traidingviewer.ui.home.viewpagers.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.traidingviewer.App
import com.traidingviewer.MainActivity
import com.traidingviewer.R
import com.traidingviewer.common.injectViewModel
import com.traidingviewer.data.api.model.FavoriteStock
import com.traidingviewer.data.api.model.HomeStock
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

class FavouriteFragment : BaseFragment(),
    OnItemClickListener {

    private var adapter: HomePagesAdapter? = null
    private var favoriteTickers = mutableListOf<HomeStock>()

    private lateinit var viewModel: FavoriteViewModel

    @Inject
    lateinit var symbolDao: SymbolDao

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        App.getComponent().inject(this)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            observeViewModel(viewModel)
            if (symbolDao.getAllFavoriteSymbols().isEmpty()) {
                emptyFavoriteMassage.visibility = View.VISIBLE
                pagerRecyclerView.visibility = View.GONE
            } else {
                showProgressBar(progressBar)
                emptyFavoriteMassage.visibility = View.GONE
                pagerRecyclerView.visibility = View.VISIBLE
                viewModel.getFavoritesInfo(symbolDao)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HomePagesAdapter(favoriteTickers, this)
        pagerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pagerRecyclerView.adapter = adapter
    }

    private fun observeViewModel(viewModel: FavoriteViewModel) {
        viewModel.apply {
            listFavoritesInfoLiveData.observe(viewLifecycleOwner, Observer { state ->
                hideProgressBar(progressBar)
                when (state) {
                    is BaseState.Success -> {
                        state.body?.let {
                            favoriteTickers.clear()
                            favoriteTickers.addAll(it)
                            pagerRecyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                    is BaseState.Failure -> {
                        showErrorToasts(state)
                    }
                }
            })
        }
    }

    companion object {
        internal fun newInstance(): FavouriteFragment {
            return FavouriteFragment()
        }
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