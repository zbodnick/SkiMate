package com.bodnick.skimate

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class OpenWeatherManager {

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

    fun retrieveWeatherData(apiKey: String, courses: List<Course>): List<Course> {

        // Build the request
        // The OAuth token becomes a header on the request

        courses.forEach {

            val lat = it.lat?.toFloat()
            val lng = it.lng?.toFloat()

            val request = Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&units=imperial&appid=$apiKey")
                .header("Authorization", "Bearer $apiKey")
                .build()


            // "Execute" the request (.execute will block the current thread until the server replies with a response)
            val response = okHttpClient.newCall(request).execute()

            // Create an empty, mutable list to hold up the Tweets we will parse from the JSON
            val weatherData: MutableList<String> = mutableListOf()

            // Get the JSON body from the response (if it exists)
            val responseString: String? = response.body?.string()

            if (!responseString.isNullOrEmpty() && response.isSuccessful) {
                val weatherJSON: JSONObject = JSONObject(responseString)

                val main: JSONObject? = weatherJSON.getJSONObject("main")

                val array: JSONArray? = weatherJSON.getJSONArray("weather")
                val main_info: JSONObject? = array?.getJSONObject(0)

                var temperature: String? = main?.optString("temp")
                val iconID: String? = main_info?.optString("icon")


//                val rain: JSONObject? = main?.getJSONObject("rain")
//                val rainVolumePastHour: String? = rain?.optString("1h")

                val wind: JSONObject? = weatherJSON.getJSONObject("wind")
                val windSpeed: String? = wind?.optString("speed")

                temperature = temperature?.toDouble()?.roundToInt().toString()

                it.temp = "${temperature}°F"
                it.wind = "${windSpeed}mph"
//                it.precipitation = rainVolumePastHour
                it.weatherIcon = iconID
            }
        }
        return courses
    }
}