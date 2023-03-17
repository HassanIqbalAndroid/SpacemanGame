package com.example.saveship.webview

import android.webkit.WebChromeClient
import android.media.MediaPlayer.OnPreparedListener
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.FrameLayout
import android.graphics.Bitmap
import android.widget.VideoView
import android.view.SurfaceView
import android.view.View
import android.webkit.WebView
import com.basicplayer.videoPlayer.webview.VideoEnabledWebView

class VideoEnabledWebChromeClient : WebChromeClient, OnPreparedListener, OnCompletionListener,
    MediaPlayer.OnErrorListener {
    interface ToggledFullscreenCallback {
        fun toggledFullscreen(fullscreen: Boolean)
    }

    private var activityNonVideoView: View? = null
    private var activityVideoView: ViewGroup? = null
    var progressBar: ProgressBar? = null
    private var loadingView: View? = null
    private var webView: VideoEnabledWebView? = null
    var isVideoFullscreen // Indicates if the video is being displayed using a custom view (typically full-screen)
            = false
        private set
    private var videoViewContainer: FrameLayout? = null
    private var videoViewCallback: CustomViewCallback? = null
    private var toggledFullscreenCallback: ToggledFullscreenCallback? = null

    constructor() {}

    override fun getDefaultVideoPoster(): Bitmap? {
        return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
    }

    constructor(activityNonVideoView: View?, activityVideoView: ViewGroup?) {
        this.activityNonVideoView = activityNonVideoView
        this.activityVideoView = activityVideoView
        loadingView = null
        webView = null
        isVideoFullscreen = false
    }

    constructor(activityNonVideoView: View?, activityVideoView: ViewGroup?, loadingView: View?) {
        this.activityNonVideoView = activityNonVideoView
        this.activityVideoView = activityVideoView
        this.loadingView = loadingView
        webView = null
        isVideoFullscreen = false
    }

    constructor(
        activityNonVideoView: View?,
        activityVideoView: ViewGroup?,
        loadingView: View?,
        webView: VideoEnabledWebView?,
        progressBar: ProgressBar?
    ) {
        this.activityNonVideoView = activityNonVideoView
        this.activityVideoView = activityVideoView
        this.loadingView = loadingView
        this.webView = webView
        this.progressBar = progressBar
        isVideoFullscreen = false
    }

    fun setOnToggledFullscreen(callback: ToggledFullscreenCallback?) {
        toggledFullscreenCallback = callback
    }

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        if (view is FrameLayout) {
            // A video wants to be shown
            val frameLayout = view
            val focusedChild = frameLayout.focusedChild

            // Save video related variables
            isVideoFullscreen = true
            videoViewContainer = frameLayout
            videoViewCallback = callback

            // Hide the non-video view, add the video view, and show it
            activityNonVideoView!!.visibility = View.INVISIBLE
            activityVideoView!!.addView(
                videoViewContainer,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            activityVideoView!!.visibility = View.VISIBLE
            if (focusedChild is VideoView) {
                // android.widget.VideoView (typically API level <11)
                val videoView = focusedChild

                // Handle all the required events
                videoView.setOnPreparedListener(this)
                videoView.setOnCompletionListener(this)
                videoView.setOnErrorListener(this)
            } else {
                if (webView != null && webView!!.settings.javaScriptEnabled && focusedChild is SurfaceView) {
                    // Run javascript code that detects the video end and notifies the Javascript interface
                    var js = "javascript:"
                    js += "var _ytrp_html5_video_last;"
                    js += "var _ytrp_html5_video = document.getElementsByTagName('video')[0];"
                    js += "if (_ytrp_html5_video != undefined && _ytrp_html5_video != _ytrp_html5_video_last) {"
                    run {
                        js += "_ytrp_html5_video_last = _ytrp_html5_video;"
                        js += "function _ytrp_html5_video_ended() {"
                        run {
                            js += "_VideoEnabledWebView.notifyVideoEnd();" // Must match Javascript interface name and method of VideoEnableWebView
                        }
                        js += "}"
                        js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);"
                    }
                    js += "}"
                    webView!!.loadUrl(js)
                }
            }

            // Notify full-screen change
            if (toggledFullscreenCallback != null) {
                toggledFullscreenCallback!!.toggledFullscreen(true)
            }
        }
    }

    override fun onShowCustomView(
        view: View,
        requestedOrientation: Int,
        callback: CustomViewCallback
    ) // Available in API level 14+, deprecated in API level 18+
    {
        onShowCustomView(view, callback)
    }

    override fun onHideCustomView() {
        if (isVideoFullscreen) {
            activityVideoView!!.visibility = View.INVISIBLE
            activityVideoView!!.removeView(videoViewContainer)
            activityNonVideoView!!.visibility = View.VISIBLE
            if (videoViewCallback != null && !videoViewCallback!!.javaClass.name.contains(".chromium.")) {
                videoViewCallback!!.onCustomViewHidden()
            }
            isVideoFullscreen = false
            videoViewContainer = null
            videoViewCallback = null
            if (toggledFullscreenCallback != null) {
                toggledFullscreenCallback!!.toggledFullscreen(false)
            }
        }
    }

    override fun getVideoLoadingProgressView(): View? {
        return if (loadingView != null) {
            loadingView!!.visibility = View.VISIBLE
            loadingView
        } else {
            super.getVideoLoadingProgressView()
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        if (loadingView != null) {
            loadingView!!.visibility = View.GONE
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        onHideCustomView()
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (newProgress >= 99) {
            progressBar!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.VISIBLE
        }
    }

    override fun onError(
        mp: MediaPlayer,
        what: Int,
        extra: Int
    ): Boolean // Error while playing video, only called in the case of android.widget.VideoView (typically API level <11)
    {
        return false // By returning false, onCompletion() will be called
    }

    fun onBackPressed(): Boolean {
        return if (isVideoFullscreen) {
            onHideCustomView()
            true
        } else {
            false
        }
    }
}