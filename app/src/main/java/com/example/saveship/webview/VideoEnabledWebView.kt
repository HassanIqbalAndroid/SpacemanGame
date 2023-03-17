package com.basicplayer.videoPlayer.webview


import android.webkit.WebView
import android.os.Looper
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import com.example.saveship.webview.VideoEnabledWebChromeClient

class VideoEnabledWebView : WebView {
    inner class JavascriptInterface {
        @android.webkit.JavascriptInterface
        fun notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
        {
            // This code is not executed in the UI thread, so we must force that to happen
            Handler(Looper.getMainLooper()).post {
                if (videoEnabledWebChromeClient != null) {
                    videoEnabledWebChromeClient!!.onHideCustomView()
                }
            }
        }
    }

    private var videoEnabledWebChromeClient: VideoEnabledWebChromeClient? = null
    private var addedJavascriptInterface: Boolean

    constructor(context: Context?) : super(context!!) {
        addedJavascriptInterface = false
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        addedJavascriptInterface = false
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        addedJavascriptInterface = false
    }

    val isVideoFullscreen: Boolean
        get() = videoEnabledWebChromeClient != null && videoEnabledWebChromeClient!!.isVideoFullscreen

    @SuppressLint("SetJavaScriptEnabled")
    override fun setWebChromeClient(client: WebChromeClient?) {
        settings.javaScriptEnabled = true
        if (client is VideoEnabledWebChromeClient) {
            videoEnabledWebChromeClient = client
        }
        super.setWebChromeClient(client)
    }

    override fun loadData(data: String, mimeType: String?, encoding: String?) {
        addJavascriptInterface()
        super.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        addJavascriptInterface()
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun loadUrl(url: String) {
        addJavascriptInterface()
        super.loadUrl(url)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        addJavascriptInterface()
        super.loadUrl(url, additionalHttpHeaders)
    }

    fun addJavascriptInterface() {
        if (!addedJavascriptInterface) {
            addJavascriptInterface(JavascriptInterface(), "_VideoEnabledWebView")
            addedJavascriptInterface = true
        }
    }
}