package com.traidingviewer.ui.info

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.traidingviewer.App
import com.traidingviewer.R
import com.traidingviewer.data.api.model.FavoriteStock
import com.traidingviewer.data.db.dao.SymbolDao
import com.traidingviewer.ui.base.BaseFragment
import com.traidingviewer.ui.info.chart.ChartFragment
import com.traidingviewer.ui.info.news.NewsFragment
import com.traidingviewer.utils.ConvertUtils
import kotlinx.android.synthetic.main.fragment_ticker_info.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class TickerInfoFragment : BaseFragment() {
    private var infoViewPagerPosition = 0
    private var symbol = ""
    private var name = ""
    private var isFavorite = false

    private var pagerAdapter: HomePagerAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_ticker_info
    }

    @Inject
    lateinit var symbolDao: SymbolDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
        val bundle = this.arguments
        if (bundle != null) {
            symbol = bundle.getString(SYMBOL, "")
            name = bundle.getString(NAME, "")
            isFavorite = bundle.getBoolean(IS_FAVORITE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tickerInfoSymbol.text = symbol
        tickerInfoName.text = name
        setViewPager()
        tickerInfoFavorite.isActivated = isFavorite
        tickerInfoFavorite.setOnClickListener {
            tickerInfoFavorite.isActivated = !tickerInfoFavorite.isActivated
            if (tickerInfoFavorite.isActivated) {
                viewLifecycleOwner.lifecycleScope.launch {
                    symbolDao.insert(
                        FavoriteStock(symbol, name)
                    )
                }
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    symbolDao.deleteTicker(
                        FavoriteStock(symbol, name)
                    )
                }
            }
        }

        tickerInfoBackButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    inner class HomePagerAdapter(
        private val fragments: ArrayList<BaseFragment>,
        fm: FragmentManager
    ) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return titles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

    }

    private fun setViewPager() {
        childFragmentManager.let {
            val fragmentsArray = arrayListOf<BaseFragment>()
            for (i in 0 until titles.size) {
                when (i) {
                    0 -> fragmentsArray.add(
                        ChartFragment.newInstance(symbol)
                    )
                    1 -> fragmentsArray.add(
                        NewsFragment.newInstance(symbol)
                    )
                }
            }
            pagerAdapter = HomePagerAdapter(fragmentsArray, it)

            tickerInfoViewPager.adapter = pagerAdapter
            tickerInfoViewPager.currentItem = infoViewPagerPosition
        }

        tickerInfoViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                infoViewPagerPosition = position
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        tickerInfoTabs.setupWithViewPager(tickerInfoViewPager)
        for (i in 0 until tickerInfoTabs.tabCount) {
            val tab: TabLayout.Tab? = tickerInfoTabs.getTabAt(i)
            tab?.let {
                val tabTextView = TextView(requireContext())
                tab.customView = tabTextView
                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.text = tab.text
                tabTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorGray
                    )
                )
                if (i == infoViewPagerPosition) {
                    tabTextView.setTypeface(null, Typeface.BOLD)
                    tabTextView.setPadding(ConvertUtils.dpToPx(10, requireContext()), 0, 0, 0)
                    tabTextView.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorBlack
                        )
                    )
                }
            }
        }
        tickerInfoTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val text = tab.customView as TextView?
                text?.let {
                    it.setTypeface(null, Typeface.BOLD)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val text = tab.customView as TextView?
                text?.let {
                    it.setTypeface(null, Typeface.NORMAL)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
                }

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    companion object {
        const val SYMBOL = "symbol"
        const val NAME = "name"
        const val IS_FAVORITE = "is_favorite"
        val titles = arrayListOf(
            "Chart",
            "News"
        )
    }
}