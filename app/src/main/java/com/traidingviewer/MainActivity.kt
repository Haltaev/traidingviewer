package com.traidingviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.traidingviewer.ui.base.BaseActivity
import com.traidingviewer.ui.home.HomeFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openFragment(HomeFragment())
    }

    private fun openFragment(fragment: Fragment, arguments: Bundle? = null) {
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openFragmentWithBackStack(fragment: Fragment, arguments: Bundle? = null) {
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}