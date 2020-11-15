package com.azizbek.masjidinuzbekistan.activities

import android.hardware.Sensor
import androidx.appcompat.app.AppCompatActivity
import android.hardware.SensorEventListener
import android.widget.TextView
import android.hardware.SensorManager
import android.os.Bundle
import com.azizbek.masjidinuzbekistan.R
import android.hardware.SensorEvent
import android.view.View
import android.view.animation.RotateAnimation
import android.view.animation.Animation
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_qibla.*
import kotlin.math.roundToInt

class QiblaActivity : AppCompatActivity(), SensorEventListener {
    private var currentDegree = 0f
    private var sensorManager: SensorManager? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qibla)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val degree = sensorEvent.values[0].roundToInt().toFloat()
        val a = degree.toInt()
        txtDegrees!!.text = "$a gradus"
        val animation = RotateAnimation(
            currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        animation.duration = 120
        animation.fillAfter = true
        imgCompass!!.startAnimation(animation)
        currentDegree = -degree
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    fun dicbacpressed(view: View?) {
        finish()
    }
}