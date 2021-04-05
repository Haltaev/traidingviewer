package com.traidingviewer.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.traidingviewer.App
import com.traidingviewer.MainActivity
import com.traidingviewer.R
import com.traidingviewer.ui.base.BaseFragment
import com.traidingviewer.ui.home.viewpagers.favorites.FavouriteFragment
import com.traidingviewer.ui.home.viewpagers.stock.StockFragment
import com.traidingviewer.ui.search.SearchFragment
import com.traidingviewer.utils.ConvertUtils
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {
    private var selectedPagePosition = 0

    private var pagerAdapter: HomePagerAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
        searchView.setOnClickListener {
            (activity as MainActivity).openFragmentWithBackStack(SearchFragment())
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
            return newsTitles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return newsTitles[position]
        }
    }

    private fun setViewPager() {
        childFragmentManager.let {
            val fragmentsArray = arrayListOf<BaseFragment>()
            for (i in 0 until newsTitles.size) {
                when (i) {
                    0 -> fragmentsArray.add(
                        StockFragment.newInstance()
                    )
                    1 -> fragmentsArray.add(
                        FavouriteFragment.newInstance()
                    )
                }
            }
            pagerAdapter = HomePagerAdapter(fragmentsArray, it)
            homeViewPager.adapter = pagerAdapter
            homeViewPager.currentItem = selectedPagePosition
        }

        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                selectedPagePosition = position
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                selectedPagePosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        homeTabs.setupWithViewPager(homeViewPager)
        for (i in 0 until homeTabs.tabCount) {
            val tab: TabLayout.Tab? = homeTabs.getTabAt(i)
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
                tabTextView.textSize = 18f
                if (i == selectedPagePosition) {
                    tabTextView.setTypeface(null, Typeface.BOLD)
                    tabTextView.setPadding(ConvertUtils.dpToPx(10, requireContext()), 0, 0, 0)
                    tabTextView.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorBlack
                        )
                    )
                    tabTextView.textSize = 28f
                }
            }
        }
        homeTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val text = tab.customView as TextView?
                text?.let {
                    it.setTypeface(null, Typeface.BOLD)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
                    it.textSize = 28f
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val text = tab.customView as TextView?
                text?.let {
                    it.setTypeface(null, Typeface.NORMAL)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))
                    it.textSize = 18f
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    companion object {
        val newsTitles = arrayListOf(
            "Stocks",
            "Favorite"
        )
    }
}