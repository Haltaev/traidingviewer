package com.traidingviewer.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.traidingviewer.App
import com.traidingviewer.R
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutId(), container, false).apply {

    }

    private fun showToast(id: Int) {
        Toast.makeText(
            requireContext(),
            requireContext().resources.getString(id),
            Toast.LENGTH_LONG
        ).show()
    }

    fun showProgressBar(progressBar: View) {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar(progressBar: View) {
        progressBar.visibility = View.GONE
    }

    fun <T> showErrorToasts(state: BaseState.Failure<T>) {
        when (state) {
            is BaseState.Failure.UnknownHostException -> {
                showToast(R.string.error_unknown_host)
            }
            is BaseState.Failure.LimitExceeded -> {
                showToast(R.string.error_limit_exceeded)
            }
            is BaseState.Failure.OtherError -> {
                showToast(R.string.error_some_error)
            }
        }
    }

    @LayoutRes
    abstract fun getLayoutId(): Int
}