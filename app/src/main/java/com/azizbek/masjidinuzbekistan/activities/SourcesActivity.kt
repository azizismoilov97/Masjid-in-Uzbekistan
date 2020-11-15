package com.azizbek.masjidinuzbekistan.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.View
import com.azizbek.masjidinuzbekistan.BuildConfig
import com.azizbek.masjidinuzbekistan.R
import java.lang.Exception

class SourcesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)
    }

    fun dicbacpressed(view: View?) {
        finish()
    }

    fun masjiduztelegram(view: View?) {
        gotoUrl("https://t.me/masjiduz")
    }

    private fun gotoUrl(s: String) {
        val uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun arabicuz(view: View?) {
        gotoUrl("https://t.me/arabicuz")
    }

    fun hayotfalsafasi(view: View?) {
        gotoUrl("https://t.me/FALSAFASI_HAYOT")
    }

    fun madinastudio(view: View?) {
        gotoUrl("https://www.youtube.com/c/MADINASTUDIOTV1")
    }

    fun masjiduzyou(view: View?) {
        gotoUrl("https://www.youtube.com/channel/UCKNu3KilMKInLzEJqSZnfuQ")
    }

    fun xizrabdulkarim(view: View?) {
        gotoUrl("https://www.youtube.com/c/XizrAbdulkarim")
    }

    fun shareApp(view: View?) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "English for IT")
            var shareMessage = " - Assalomu alaykum!\n - Sizga ushbu dasturni tavsiya qilaman\n - Dastur nomi: Masjid in Uzbekistan\n\n"
            shareMessage = """$shareMessage - Link -> https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"""
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}