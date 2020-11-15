package com.azizbek.masjidinuzbekistan.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.azizbek.masjidinuzbekistan.R
import kotlinx.android.synthetic.main.activity_masjid.*

class MasjidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masjid)
    }

    fun ortga(view: View) {finish()}

    fun searchLocation(view: View)  {
        var gmmIntentUri = Uri.parse("geo:0,0?z=10&q=mosque")

        when(view.id){
            toshkentShahar.id->gmmIntentUri = Uri.parse("geo:41.275891,69.293713?z=10&q=mosque")
            toshkentViloyati.id->gmmIntentUri = Uri.parse("geo:41.221323,69.859741?z=10&q=mosque")
            andijonViloyati.id->gmmIntentUri = Uri.parse("geo:40.768594,72.236379?z=10&q=mosque")
            buxoroViloyati.id->gmmIntentUri = Uri.parse("geo:40.250416,63.203215?z=10&q=mosque")
            samarqandViloyati.id->gmmIntentUri = Uri.parse("geo:39.9200791,66.427150?z=10&q=mosque")
            sirdaryoViloyati.id->gmmIntentUri = Uri.parse("geo:40.386381,68.715497?z=10&q=mosque")
            fargonaViloyati.id->gmmIntentUri = Uri.parse("geo:40.456808,71.287421?z=10&q=mosque")
            xorazmViloyati.id->gmmIntentUri = Uri.parse("geo:41.356534,60.856669?z=10&q=mosque")
            navoiyViloyati.id->gmmIntentUri = Uri.parse("geo:42.698858,64.633769?z=10&q=mosque")
            jizzaxViloyati.id->gmmIntentUri = Uri.parse("geo:40.470641,67.570854?z=10&q=mosque")
            qashqadaryoViloyati.id->gmmIntentUri = Uri.parse("geo:43.33333,67.66667?z=10&q=mosque")
            namanganViloyati.id->gmmIntentUri = Uri.parse("geo:38.898623,66.046353?z=10&q=mosque")
            surxandaryoViloyati.id->gmmIntentUri = Uri.parse("geo:37.940900,67.570854?z=10&q=mosque")
            qoraqalpogiston.id->gmmIntentUri = Uri.parse("geo:43.804133,59.445799?z=10&q=mosque")
        }

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
    }
}