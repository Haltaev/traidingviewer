package com.traidingviewer.ui.info.news

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.traidingviewer.R
import com.traidingviewer.common.injectViewModel
import com.traidingviewer.data.api.model.CompanyNewsResponse
import com.traidingviewer.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_list.*

class NewsFragment : BaseFragment() {

    private var adapter: NewsAdapter? = null
    var ticker = ""
    private lateinit var viewModel: NewsViewModel

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)

        val bundle = this.arguments
        if (bundle != null) {
            ticker = bundle.getString(TICKER, "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar()
        observeViewModel(viewModel)
        viewModel.getNews(ticker)
    }

    private fun showProgressBar() {
        circularProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        circularProgressBar.visibility = View.GONE
    }

    private fun setAdapter(items: List<CompanyNewsResponse>) {
        adapter = NewsAdapter(items)
        pagerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pagerRecyclerView.adapter = adapter
    }

    private fun observeViewModel(viewModel: NewsViewModel) {
        viewModel.apply {
            newsLiveData.observe(viewLifecycleOwner, Observer {
                hideProgressBar()
                when (it) {
                    is NewsState.Success -> {
                        setAdapter(it.news)
                    }
                    NewsState.Failure.UnknownHostException -> {
                        showToast(R.string.error_unknown_host)
                    }
                    NewsState.Failure.LimitExceeded -> {
                        showToast(R.string.error_limit_exceeded)
                    }
                    NewsState.Failure.OtherError -> {
                        showToast(R.string.error_some_error)
                    }
                }
            })
        }
    }

    companion object {
        private const val TICKER = "ticker"
        internal fun newInstance(ticker: String): NewsFragment {
            val pageFragment =
                NewsFragment()
            val arguments = Bundle()
            arguments.putString(TICKER, ticker)
            pageFragment.arguments = arguments
            return pageFragment
        }
    }
}