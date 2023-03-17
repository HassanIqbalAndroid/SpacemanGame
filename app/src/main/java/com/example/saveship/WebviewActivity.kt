package com.example.saveship

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.saveship.MainActivity.MyClass.themeIndex
import com.example.saveship.MainActivity.MyClass.themesList
import com.example.saveship.databinding.ActivityWebviewBinding
import com.example.saveship.webview.WebViewClient

class WebviewActivity : Activity() {
    lateinit var binding: ActivityWebviewBinding
    var dialog: Dialog? = null
    var builder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val editor = getSharedPreferences("Themes", MODE_PRIVATE)
        themeIndex = editor.getInt("themeIndex", 0)

        setTheme(themesList[themeIndex])
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.LLWeb.visibility = View.GONE
        binding.ProgressBar.visibility = View.VISIBLE

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.settings.domStorageEnabled = true
        binding.webView.webViewClient = WebViewClient(this@WebviewActivity, binding.webView)
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if(newProgress > 50){
                    binding.LLWeb.visibility = View.VISIBLE
                    binding.ProgressBar.visibility = View.GONE
                }
            }
        }
        binding.webView.loadUrl(Constants.FirstURL)

    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            builder = AlertDialog.Builder(this)
            val view: View = LayoutInflater.from(this@WebviewActivity).inflate(R.layout.popup_exit, null)
            builder!!.setView(view)
            val Yes = view.findViewById<AppCompatButton>(R.id.Yes)
            val No = view.findViewById<AppCompatButton>(R.id.No)
            Yes.setOnClickListener {
                dialog!!.dismiss()
                finish()
            }
            No.setOnClickListener { dialog!!.dismiss() }
            dialog = builder!!.create()
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.show()
        }
    }
}