package com.azizbek.masjidinuzbekistan.fetchGeoAddress

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.text.TextUtils
import android.os.Bundle
import android.os.ResultReceiver
import java.lang.Exception
import java.util.*

class FetchAddressIntentService : IntentService("FetchAddressIntentService") {
    private var resultReceiver: ResultReceiver? = null
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            var errorMessage = ""
            resultReceiver = intent.getParcelableExtra(Constants.RECEIVER)
            val location = intent.getParcelableExtra<Location>(Constants.LOCATION_DATA_EXTRA)
                    ?: return
            val geoCoder = Geocoder(this, Locale.ENGLISH)
            var addresses: List<Address>? = null
            try {

                    addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

            } catch (exception: Exception) {
                errorMessage = exception.message.toString()
            }
            if (addresses == null || addresses.isEmpty()) {
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
            } else {
                val address = addresses[0]
                val addressFragments = ArrayList<String?>()
                for (i in 0..address.maxAddressLineIndex) {
                    addressFragments.add(address.getAddressLine(i))
                }
                deliverResultToReceiver(
                        Constants.SUCCESS_RESULT,
                        TextUtils.join(
                                Objects.requireNonNull(System.getProperty("line.separator")),
                                addressFragments
                        )
                )
            }
        }
    }

    private fun deliverResultToReceiver(resultCode: Int, addressMessage: String?) {
        val bundle = Bundle()
        bundle.putString(Constants.RESULT_DATA_KEY, addressMessage)
        resultReceiver!!.send(resultCode, bundle)
    }
}