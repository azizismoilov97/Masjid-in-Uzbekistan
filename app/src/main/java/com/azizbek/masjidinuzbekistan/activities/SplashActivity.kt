package com.azizbek.masjidinuzbekistan.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.azizbek.masjidinuzbekistan.R
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setImageAnim()

    }

    private fun setImageAnim() {

        img.animate().alpha(1f).duration = 1000

        val t = Thread {
            try {
                Thread.sleep(1500)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        t.start()
    }

}