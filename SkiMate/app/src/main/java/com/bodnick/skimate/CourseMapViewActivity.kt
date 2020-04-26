package com.bodnick.skimate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.doAsync

class CourseMapViewActivity : AppCompatActivity(), OnMapReadyCallback {

    var forecasts: MutableList<Forecast> = mutableListOf()
    private lateinit var mMap: GoogleMap
    var lat: String = ""
    var lng: String = ""
    var name: String = ""

    private lateinit var dayText0: TextView
    private lateinit var dayText1: TextView
    private lateinit var dayText2: TextView
    private lateinit var dayText3: TextView
    private lateinit var dayText4: TextView

    private lateinit var weatherIcon0: ImageView
    private lateinit var weatherIcon1: ImageView
    private lateinit var weatherIcon2: ImageView
    private lateinit var weatherIcon3: ImageView
    private lateinit var weatherIcon4: ImageView

    private lateinit var tempText0: TextView
    private lateinit var tempText1: TextView
    private lateinit var tempText2: TextView
    private lateinit var tempText3: TextView
    private lateinit var tempText4: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_map_view)

        lat = intent.getStringExtra("lat").toString()
        lng = intent.getStringExtra("lng").toString()
        name = intent.getStringExtra("name").toString()

        dayText0 = findViewById(R.id.daytext)
        dayText1 = findViewById(R.id.daytext1)
        dayText2 = findViewById(R.id.daytext2)
        dayText3 = findViewById(R.id.daytext3)
        dayText4 = findViewById(R.id.daytext4)

        weatherIcon0 = findViewById(R.id.weathericon)
        weatherIcon1 = findViewById(R.id.weathericon1)
        weatherIcon2 = findViewById(R.id.weathericon2)
        weatherIcon3 = findViewById(R.id.weathericon3)
        weatherIcon4 = findViewById(R.id.weathericon4)

        tempText0 = findViewById(R.id.temptext)
        tempText1 = findViewById(R.id.temptext1)
        tempText2 = findViewById(R.id.temptext2)
        tempText3 = findViewById(R.id.temptext3)
        tempText4 = findViewById(R.id.temptext4)

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        title = name;
//        actionBar.setIcon(R.drawable.icon)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getForecast()
        inflateForecast()
    }

    private fun inflateForecast() {
        forecasts.forEachIndexed { index, forecast ->

        }
    }

    private fun setIcon(iconID: String, iconView: ImageView) {
        when (iconID) {
            "01d" -> iconView.setImageResource(R.drawable.ic_01d);
            "01n" -> iconView.setImageResource(R.drawable.ic_01n);
            "02d" -> iconView.setImageResource(R.drawable.ic_02d);
            "02n" -> iconView.setImageResource(R.drawable.ic_02n);
            "03d" -> iconView.setImageResource(R.drawable.ic_03d);
            "03n" -> iconView.setImageResource(R.drawable.ic_03n);
            "04d" -> iconView.setImageResource(R.drawable.ic_04d);
            "04n" -> iconView.setImageResource(R.drawable.ic_04n);
            "09d" -> iconView.setImageResource(R.drawable.ic_09d);
            "09n" -> iconView.setImageResource(R.drawable.ic_09n);
            "10d" -> iconView.setImageResource(R.drawable.ic_10d);
            "10n" -> iconView.setImageResource(R.drawable.ic_10n);
            "11d" -> iconView.setImageResource(R.drawable.ic_11d);
            "11n" -> iconView.setImageResource(R.drawable.ic_11n);
            "13d" -> iconView.setImageResource(R.drawable.ic_13d);
            "13n" -> iconView.setImageResource(R.drawable.ic_13n);
            "50d" -> iconView.setImageResource(R.drawable.ic_50d);
            "50n" -> iconView.setImageResource(R.drawable.ic_50n);
        }
    }

    private fun getForecast() {

        doAsync {
            val weatherManager = OpenWeatherManager()

            try {
                // Read OpenWeather API key from XML file
                val apiKey = getString(R.string.openWeatherAPI)

                forecasts = weatherManager.retrieve5DayForecast (
                    apiKey = apiKey,
                    lat = lat,
                    lng = lng
                )

                runOnUiThread {
                    // UI THREAD
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
                // Switch back to the UI Thread
                runOnUiThread {
                    Toast.makeText(
                        this@CourseMapViewActivity,
                        "Error: Cannot retrieve forecast data from Open Weather API: $exception",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val course = LatLng(lat.toDouble(), lng.toDouble())

        val zoomLevel = 18.0f
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(course, zoomLevel))
    }
}