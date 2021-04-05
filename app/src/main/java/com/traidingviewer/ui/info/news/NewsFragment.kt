package com.traidingviewer.ui.info.news

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.traidingviewer.R
import com.traidingviewer.common.injectViewModel
import com.traidingviewer.data.api.model.CompanyNewsResponse
import com.traidingviewer.ui.base.BaseFragment
import com.traidingviewer.ui.base.BaseState
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.view_progress.*

class NewsFragment : BaseFragment(), OnNewsLinkClickListener {

    private var adapter: NewsAdapter? = null
    var ticker = ""
    private lateinit var viewModel: NewsViewModel

    override fun getLayoutId(): Int {
        return R.layout.fragment_news_list
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
        showProgressBar(progressBar)
        observeViewModel(viewModel)
        viewModel.getNews(ticker)
    }

    private fun setAdapter(items: List<CompanyNewsResponse>) {
        adapter = NewsAdapter(items, this)
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsRecyclerView.adapter = adapter
    }

    private fun observeViewModel(viewModel: NewsViewModel) {
        viewModel.apply {
            newsLiveData.observe(viewLifecycleOwner, Observer { state ->
                hideProgressBar(progressBar)
                when (state) {
                    is BaseState.Success -> {
                        state.body?.let {
                            setAdapter(it)
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

    override fun onNewsLinkClickListener(url: String) {
        WebViewActivity.openWebViewActivity(requireContext(), url)
    }
}