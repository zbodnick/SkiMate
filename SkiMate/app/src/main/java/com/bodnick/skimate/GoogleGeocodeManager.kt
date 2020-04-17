package com.bodnick.skimate

import android.widget.Toast
import okhttp3.Address
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class GoogleGeocodeManager {

    private val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        // Set up our OkHttpClient instance to log all network traffic to Logcat
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        okHttpClient = builder.build()
    }

    fun geocode(apiKey: String, address: String): Location {

            val request = Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?address=$address&key=$apiKey")
                .header("Authorization", "Bearer $apiKey")
                .build()

            // "Execute" the request (.execute will block the current thread until the server replies with a response)
            val response = okHttpClient.newCall(request).execute()

            // Get the JSON body from the response (if it exists)
            val responseString: String? = response.body?.string()

            if (!responseString.isNullOrEmpty() && response.isSuccessful) {
                val geocodeJSON: JSONObject = JSONObject(responseString)

                val resultsArray: JSONArray = geocodeJSON.getJSONArray("results")

                var data: JSONObject = resultsArray.getJSONObject(0)
                val address = data.optString("formatted_address")

                var locationInfo: JSONObject = resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")

                val lat = locationInfo.optString("lat")
                val lng = locationInfo.optString("lng")

                println("Address: $address \nLat: $lat \nLng: $lng ")

                return Location(address, lat, lng)

            }
        return Location("", "", "")
    }
}