package com.traidingviewer.ui.info.news

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import com.traidingviewer.R
import kotlinx.android.synthetic.main.activity_web_view.*


@SuppressLint("SetJavaScriptEnabled")
class WebViewActivity : Activity() {

    var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val bundle = this.intent
        if (bundle != null) {
            url = bundle.getStringExtra(KEY_URL) ?: ""
        }
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }

    companion object {
        const val KEY_URL = "key_url"

        fun openWebViewActivity(context: Context, url: String) {
            val i = Intent(context, WebViewActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra(KEY_URL, url)
            context.startActivity(i)
        }
    }
}