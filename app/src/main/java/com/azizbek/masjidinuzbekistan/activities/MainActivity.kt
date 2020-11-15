package com.azizbek.masjidinuzbekistan.activities

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.SimpleDate
import com.azizbek.masjidinuzbekistan.*
import com.azizbek.masjidinuzbekistan.R
import com.azizbek.masjidinuzbekistan.fetchGeoAddress.Constants
import com.azizbek.masjidinuzbekistan.fetchGeoAddress.FetchAddressIntentService
import com.azizbek.masjidinuzbekistan.model.GlobalsSHaredpreference
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var resultReceiver:ResultReceiver
    private lateinit var timer: CountDownTimer
    private var permissionId = 52
    private var morning: Calendar? = null
    private var afternoon: Calendar? = null
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    private var hourForTimer=0
    private var minuteForTimer=0
    private var secondForTimer=60
    private var myLatitude:Double=41.275891
    private var myLongitude:Double=69.293713
    private var myPlaceName:String="Uzbekistan, Tashkent"
    private val myLocation: Location = Location("providerNa")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        search_btn.animate().rotation(360f).alpha(1f).duration = 1000
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        resultReceiver=AddressResultReceiver(Looper.myLooper()?.let { Handler(it) })
//        getLastLocation()
        getFirstDate()
    }

    private fun getFirstDate(){
        when {
            checkPermission() -> {
            }
            else -> {
                requestPermission()
            }
        }
        myLatitude= GlobalsSHaredpreference.getLocation(this@MainActivity, "latitude").toDouble()
        myLongitude= GlobalsSHaredpreference.getLocation(this@MainActivity, "longitude").toDouble()
        myPlaceName = GlobalsSHaredpreference.getLocationAddress(this@MainActivity, "locationAddress")

        if (myLatitude==0.0&&myLongitude==0.0){
            getToday(41.275891,69.293713)
        }else{
            getToday(myLatitude,myLongitude)
        }
        locationName!!.text=myPlaceName

    }

    private fun getLastLocation() {

        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {

                        myLocation.latitude = location.latitude
                        myLocation.longitude = location.longitude
                        fetchAddress(myLocation)
                        getToday(location.latitude,
                                location.longitude)
                        GlobalsSHaredpreference.saveLocation(this@MainActivity, "latitude", location.latitude)
                        GlobalsSHaredpreference.saveLocation(this@MainActivity, "longitude", location.longitude)
                    }
                }
            } else {
                Toast.makeText(this, "Please enable your location services", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            requestPermission()
        }
    }

    private fun getPrayerTime(hour: Int, minute: Int){
        if (hour < 10 && minute < 10) {
            namozVaqti!!.text= "0$hour:0$minute"
        }else if (hour < 10 && minute >= 10) {
            namozVaqti!!.text= "0$hour:$minute"
        }else if (hour >= 10 && minute >= 10) {
            namozVaqti!!.text = "$hour:$minute"
        } else if (hour >= 10 && minute < 10) {
            namozVaqti!!.text = "$hour:0$minute"
        }
    }
    
    private fun getToday(latitude: Double, longitude: Double) {

        val myToday = SimpleDate(GregorianCalendar())

        val d=System.currentTimeMillis()
        val second:Int= ((d/1000)%60).toInt()
        val minute:Int=(((d/60000)%60).toInt())
        var hours:Int=(((d/(60000*60))%24).toInt())+5 //current time

        when(hours){
            24->hours=0
            25->hours=1
            26->hours=2
            27->hours=3
            28->hours=4
            29->hours=5
        }


        val myLocation = com.azan.astrologicalCalc.Location(latitude, longitude, 5.0, 0)
        val azan = Azan(myLocation, Method.MUSLIM_LEAGUE)
        val prayerTimes = azan.getPrayerTimes(myToday)
        val imsaak=prayerTimes.times
        val totalMinute=hours*60+minute

        val isFajr=prayerTimes.fajr().hour*60+prayerTimes.fajr().minute
        val isShuruq=prayerTimes.shuruq().hour*60+prayerTimes.shuruq().minute
        val isThuhr=prayerTimes.thuhr().hour*60+prayerTimes.thuhr().minute
        val isAssr=prayerTimes.assr().hour*60+prayerTimes.assr().minute
        val isMaghrib=prayerTimes.maghrib().hour*60+prayerTimes.maghrib().minute
        val isIshaa=prayerTimes.ishaa().hour*60+prayerTimes.ishaa().minute
        val isNextFajr=azan.getNextDayFajr(myToday).hour*60+azan.getNextDayFajr(myToday).minute

            setNotifTime(1, prayerTimes.fajr().hour, prayerTimes.fajr().minute, 0)
            setNotifTime(2, prayerTimes.shuruq().hour, prayerTimes.shuruq().minute, 0)
            setNotifTime(3, prayerTimes.thuhr().hour, prayerTimes.thuhr().minute, 0)
            setNotifTime(4, prayerTimes.assr().hour, prayerTimes.assr().minute, 0)
            setNotifTime(5, prayerTimes.maghrib().hour, prayerTimes.maghrib().minute, 0)
            setNotifTime(6, prayerTimes.ishaa().hour, prayerTimes.ishaa().minute, 0)

        when {
            totalMinute<=isFajr -> {
                getPrayerTime(prayerTimes.fajr().hour, prayerTimes.fajr().minute)
                namozNomi!!.text=getString(R.string.bomdod)

                hourForTimer=prayerTimes.fajr().hour-hours
                minuteForTimer=hourForTimer*60-(minute-prayerTimes.fajr().minute)

            }
            totalMinute in (isFajr + 1) until isShuruq -> {
                getPrayerTime(prayerTimes.shuruq().hour, prayerTimes.shuruq().minute)
                namozNomi!!.text=getString(R.string.sunrise)

                hourForTimer=prayerTimes.shuruq().hour-hours
                minuteForTimer=hourForTimer*60-(minute-prayerTimes.shuruq().minute)

            }
            totalMinute in (isShuruq + 1)..isThuhr -> {
                getPrayerTime(prayerTimes.thuhr().hour, prayerTimes.thuhr().minute)
                namozNomi!!.text=getString(R.string.peshin)

                hourForTimer=prayerTimes.thuhr().hour-hours
                minuteForTimer=hourForTimer*60-(minute-prayerTimes.thuhr().minute)
            }
            totalMinute in (isThuhr + 1)..isAssr -> {
                getPrayerTime(prayerTimes.assr().hour, prayerTimes.assr().minute)
                namozNomi!!.text=getString(R.string.asr)

                hourForTimer=prayerTimes.assr().hour-hours
                minuteForTimer=hourForTimer*60-(minute-prayerTimes.assr().minute)
            }
            totalMinute in (isAssr + 1) ..isMaghrib -> {
                getPrayerTime(prayerTimes.maghrib().hour, prayerTimes.maghrib().minute)
                namozNomi!!.text=getString(R.string.shom)

                hourForTimer=prayerTimes.maghrib().hour-hours
                minuteForTimer=hourForTimer*60-(minute-prayerTimes.maghrib().minute)
            }
            totalMinute in (isMaghrib + 1) until isIshaa -> {
                getPrayerTime(prayerTimes.ishaa().hour, prayerTimes.ishaa().minute)
                namozNomi!!.text=getString(R.string.xufton)

                hourForTimer=prayerTimes.ishaa().hour-hours
                minuteForTimer=hourForTimer*60-(minute-prayerTimes.ishaa().minute)
            }
            else -> {
                getPrayerTime(azan.getNextDayFajr(myToday).hour, azan.getNextDayFajr(myToday).minute)
                namozNomi!!.text=getString(R.string.bomdod_ertangi)

                    hourForTimer=(24-hours)+azan.getNextDayFajr(myToday).hour
                    minuteForTimer=hourForTimer*60-(minute-azan.getNextDayFajr(myToday).minute)
            }
        }
        hourForTimer=minuteForTimer/60
        minuteForTimer %= 60
        var hourForTimeString=""
        var minuteForTimeString=""
        var secondForTimeString=""
        secondForTimer=60

//       Toast.makeText(this@MainActivity,"$hourForTimer:$minuteForTimer",Toast.LENGTH_SHORT).show()
        timer = object:CountDownTimer((hourForTimer*60*60*1000+minuteForTimer*1000*60+60000).toLong(),1000){

            override fun onTick(p0: Long) {

                secondForTimer--

                secondForTimeString = if (secondForTimer in 0..9) {
                    "0$secondForTimer"
                }else{"$secondForTimer"}

                minuteForTimeString = if(minuteForTimer in 0..9){
                    "0$minuteForTimer"
                }else{"$minuteForTimer"}

                hourForTimeString = if (hourForTimer in 1..9) {
                    "0$hourForTimer"
                }else{"$hourForTimer"}

                if (hourForTimer == 0) { nextNamozVaqti!!.text="(-$minuteForTimeString:$secondForTimeString)"}

                if (hourForTimer == 0&&minuteForTimer == 0) { nextNamozVaqti!!.text="(-$secondForTimeString)"}
                if (secondForTimer == 0&&minuteForTimer!=0&&hourForTimer==0) {

                    secondForTimer=60
                    minuteForTimer--
                    nextNamozVaqti!!.text="(-$minuteForTimeString:$secondForTimeString)"
                }

                if (secondForTimer == 0&&minuteForTimer!=0&&hourForTimer!=0) {
                    secondForTimer=60
                    minuteForTimer--
                    nextNamozVaqti!!.text="(-$hourForTimeString:$minuteForTimeString:$secondForTimeString)"
                }else if (secondForTimer == 0 && minuteForTimer == 0) {
                    if (hourForTimer != 0) {
                        hourForTimer--
                        secondForTimer=60
                        minuteForTimer=59
                        nextNamozVaqti!!.text="(-$hourForTimeString:$minuteForTimeString:$secondForTimeString)"
                    }else{
                        timer.cancel()
                        getLastLocation()
                    }
                }else{
                    nextNamozVaqti!!.text="(-$hourForTimeString:$minuteForTimeString:$secondForTimeString)"
                }

            }

            override fun onFinish() {
                getLastLocation()
            }

        }
        timer.start()

        var myMonthName=""

        when (myToday.month) {
            1 -> myMonthName = "Yanvar"
            2 -> myMonthName = "Fevral"
            3 -> myMonthName = "Mart"
            4 -> myMonthName = "Aprel"
            5 -> myMonthName = "May"
            6 -> myMonthName = "Iyun"
            7 -> myMonthName = "Iyul"
            8 -> myMonthName = "Avgust"
            9 -> myMonthName = "Sentabr"
            10 -> myMonthName = "Oktabr"
            11 -> myMonthName = "Noyabr"
            12 -> myMonthName = "Dekabr"
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
           val calendar=android.icu.util.Calendar.getInstance()
           val day=calendar.get(Calendar.DAY_OF_WEEK)
           var dayOfName=""
           when (day) {
               1-> dayOfName="Yakshanba"
               2-> dayOfName="Dushanba"
               3-> dayOfName="Seshanba"
               4-> dayOfName="Chorshanba"
               5-> dayOfName="Payshanba"
               6-> dayOfName="Juma"
               7-> dayOfName="Shanba"
           }
           today!!.text = """${myToday.day}-$myMonthName, $dayOfName, ${myToday.year}"""
       } else {
           today!!.text = """${myToday.day}-$myMonthName, ${myToday.year}"""
         }
    }

    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
            fetchAddress(lastLocation)
            GlobalsSHaredpreference.saveLocation(this@MainActivity, "latitude", lastLocation.latitude)
            GlobalsSHaredpreference.saveLocation(this@MainActivity, "longitude", lastLocation.longitude)
            getToday(lastLocation.latitude,
                    lastLocation.longitude)
        }
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
            PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
                this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ), permissionId
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug: ", "You have the permission")
            }
        }
    }

    private fun gotoUrl(s: String) {
        val uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun manbalar(view: View?) {
        view as CardView
        startActivity(Intent(this@MainActivity, SourcesActivity::class.java))
    }

    fun shareapp(view: View?) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "English for IT")
            var shareMessage =
                " - Assalomu alaykum!\n - Sizga ushbu dasturni tavsiya qilaman\n - Dastur nomi: Masjid in Uzbekistan\n\n"
            shareMessage =
                """$shareMessage - Link -> https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"""
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun myphoto(view: View) {
        view as CardView
        gotoUrl("https://t.me/masjidlarrasmi")
    }

    fun qiblacompass(view: View) {
        view as CardView
        startActivity(Intent(this@MainActivity, QiblaActivity::class.java))
    }

    fun searchLocation(view: View) {
        val myAlertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        myAlertDialogBuilder.setTitle("Joylashuvni aniqlash")
        myAlertDialogBuilder.setIcon(R.drawable.ic_baseline_location_on_24)
        myAlertDialogBuilder.setMessage("Hozirgi joylashuvingizni aniqlamoqchimisiz?")
        myAlertDialogBuilder.setCancelable(false)
        myAlertDialogBuilder.setPositiveButton("Ha") { _, _ ->
            timer.cancel()
            getLastLocation()
        }
        myAlertDialogBuilder.setNeutralButton("Bekor qilish") { _, _ ->
            Toast.makeText(this, "Bekor qilindi", Toast.LENGTH_SHORT).show()
        }

        val myAlertDialog = myAlertDialogBuilder.create()
        myAlertDialog.show()
    }

    private fun setNotifTime(ID: Int, hh: Int, mm: Int, ss: Int) {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, NotificationReceiever::class.java)
        intent.putExtra("ID", ID)
        pendingIntent = PendingIntent.getBroadcast(this@MainActivity, ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        morning = Calendar.getInstance()
        afternoon = Calendar.getInstance()
        morning!!.set(Calendar.HOUR_OF_DAY, hh)
        morning!!.set(Calendar.MINUTE, mm)
        morning!!.set(Calendar.SECOND, ss)
        if (morning!!.before(afternoon)) morning!!.add(Calendar.DATE, 1)
        alarmManager!!.setRepeating(AlarmManager.RTC_WAKEUP, morning!!.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun searchMasjidNearby(view: View) {
        val gmmIntentUri = Uri.parse("geo:0,0?z=10&q=mosque")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
    }

    fun masjidlar(view: View) {
        view as CardView
        startActivity(Intent(this@MainActivity, MasjidActivity::class.java))
    }

    fun rateApp(view: View) {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try{
            startActivity(myAppLinkToMarket)
        }catch(e:ActivityNotFoundException){
            Toast.makeText(this@MainActivity,"Dastur topilmadi!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAddress(location: Location) {
        val intent=Intent(this@MainActivity, FetchAddressIntentService::class.java)
        intent.putExtra(Constants.RECEIVER, resultReceiver)
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location)
        startService(intent)
    }

    private inner class AddressResultReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == Constants.SUCCESS_RESULT) {
                locationName!!.text=resultData.getString(Constants.RESULT_DATA_KEY)
                Toast.makeText(this@MainActivity,"Sizning manzilingiz: "+locationName.text,Toast.LENGTH_SHORT).show()
                GlobalsSHaredpreference.saveLocationAddress(this@MainActivity,
                        "locationAddress",locationName.text.toString())
            } else {
                Toast.makeText(this@MainActivity, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show()
            }
        }
    }
}