package com.traidingviewer

import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.traidingviewer.ui.BaseActivity
import com.traidingviewer.ui.home.HomeFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openFragment(HomeFragment())

    }

    fun openFragment(fragment: Fragment, arguments: Bundle? = null) {
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

    fun openKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}