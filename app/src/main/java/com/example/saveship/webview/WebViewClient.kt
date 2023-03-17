package com.example.saveship.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Build
import android.content.Intent
import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.webkit.*
import android.webkit.WebViewClient
import com.basicplayer.videoPlayer.webview.VideoEnabledWebView
import java.util.HashMap

class WebViewClient @SuppressLint("JavascriptInterface") constructor(
    var act: Activity,
    var mainView: VideoEnabledWebView
) : WebViewClient() {
    private val loadedUrls: Map<String, Boolean> = HashMap()

    init {
        mainView.addJavascriptInterface(this, "FBDownloader")
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        view.loadUrl(
            "javascript:(function prepareVideo() { "
                    + "var el = document.querySelectorAll('div[data-sigil]');"
                    + "for(var i=0;i<el.length; i++)"
                    + "{"
                    + "var sigil = el[i].dataset.sigil;"
                    + "if(sigil.indexOf('inlineVideo') > -1){"
                    + "delete el[i].dataset.sigil;"
                    + "console.log(i);"
                    + "var jsonData = JSON.parse(el[i].dataset.store);"
                    + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                    + "}" + "}" + "})()"
        )
        view.loadUrl(
            "javascript:( window.onload=prepareVideo;"
                    + ")()"
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.settings.mediaPlaybackRequiresUserGesture = false
        }
    }

    override fun onPageFinished(view: WebView, url: String) {
        view.loadUrl(
            "javascript:(function prepareVideo() { "
                    + "var el = document.querySelectorAll('div[data-sigil]');"
                    + "for(var i=0;i<el.length; i++)"
                    + "{"
                    + "var sigil = el[i].dataset.sigil;"
                    + "if(sigil.indexOf('inlineVideo') > -1){"
                    + "delete el[i].dataset.sigil;"
                    + "console.log(i);"
                    + "var jsonData = JSON.parse(el[i].dataset.store);"
                    + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                    + "}" + "}" + "})()"
        )
        view.loadUrl(
            "javascript:( window.onload=prepareVideo;"
                    + ")()"
        )
    }

    @JavascriptInterface
    fun processVideo(vidData: String, vidID: String?) {
        Log.i("url", "processVideo $vidData")
    }

    override fun onLoadResource(view: WebView, url: String) {
        super.onLoadResource(view, url)
        Log.i("allurl", url)
        view.loadUrl(
            "javascript:(function prepareVideo() { "
                    + "var el = document.querySelectorAll('div[data-sigil]');"
                    + "for(var i=0;i<el.length; i++)"
                    + "{"
                    + "var sigil = el[i].dataset.sigil;"
                    + "if(sigil.indexOf('inlineVideo') > -1){"
                    + "delete el[i].dataset.sigil;"
                    + "console.log(i);"
                    + "var jsonData = JSON.parse(el[i].dataset.store);"
                    + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                    + "}" + "}" + "})()"
        )
        view.loadUrl(
            "javascript:( window.onload=prepareVideo;"
                    + ")()"
        )
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        if (url.startsWith(WebView.SCHEME_TEL)
            || url.startsWith("sms:")
            || url.startsWith(WebView.SCHEME_MAILTO)
            || url.startsWith(WebView.SCHEME_GEO)
        ) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            act.startActivity(intent)
        } else {
            view.loadUrl(url)
        }
        return true
    }

    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
        return super.shouldInterceptRequest(view, url)
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        super.onReceivedError(view, request, error)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (error.errorCode == -6) {
                opencheckdialog("Error", error.description.toString(), view)
            } else {
            }
        }
    }

    fun opencheckdialog(error: String, description: String, view: WebView) {
        val alertDialog = AlertDialog.Builder(act).create()
        alertDialog.setTitle("$error ")
        alertDialog.setMessage("$description\nThis site may be blocked in your country.\nIf you want to unlock this site click on Get VPN")
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again") { dialog, which ->
            view.reload()
            dialog.dismiss()
        }
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Go back") { dialog, which ->
            dialog.dismiss()
            //   startActivity(getIntent());
        }
        alertDialog.show()
    }

    companion object {
        fun getMimeType(url: String?): String? {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return type
        }
    }
}