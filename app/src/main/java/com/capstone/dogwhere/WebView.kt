package com.capstone.dogwhere

import android.app.Dialog
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.WebView
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        location_editText.setOnClickListener {
            showKakaoAddressWebView()
        }
    }
    private fun showKakaoAddressWebView() {

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
        }

        webView.apply {
            addJavascriptInterface(WebViewData(), "Leaf")
            webViewClient = client
            webChromeClient = chromeClient
            loadUrl("https://maniatoo99.cafe24.com/DogWhere/webview.php")
        }
    }
    private val client: WebViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: android.webkit.WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }

        override fun onReceivedSslError(
            view: android.webkit.WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed()
        }
    }

    private inner class WebViewData {
        @JavascriptInterface
        fun getAddress(zoneCode: String, roadAddress: String, buildingName: String) {

            CoroutineScope(Dispatchers.Default).launch {

                withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {

                    location_editText.setText("($zoneCode) $roadAddress $buildingName")

                }
            }
        }
    }

    private val chromeClient = object : WebChromeClient() {

        override fun onCreateWindow(
            view: android.webkit.WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {

            val newWebView = WebView(this@WebView)

            newWebView.settings.javaScriptEnabled = true

            val dialog = Dialog(this@WebView)

            dialog.setContentView(newWebView)

            val params = dialog.window!!.attributes

            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.attributes = params
            dialog.show()

            newWebView.webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(
                    view: android.webkit.WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    super.onJsAlert(view, url, message, result)
                    return true
                }

                override fun onCloseWindow(window: android.webkit.WebView?) {
                    dialog.dismiss()
                }
            }
            Log.e("yy", "안떠>?")

            (resultMsg!!.obj as android.webkit.WebView.WebViewTransport).webView = newWebView
            resultMsg.sendToTarget()

            return true
        }
    }
}