package com.bodnick.skimate

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
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

    fun retrieve5DayForecast(apiKey: String, lat: String, lng: String): MutableList<Forecast> {

            var fiveDayForecast: MutableList<Forecast> = mutableListOf()

            var numDays = 5

            val request = Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lng&units=imperial&appid=$apiKey")
                .header("Authorization", "Bearer $apiKey")
                .build()



            val response = okHttpClient.newCall(request).execute()

            // Get the JSON body from the response (if it exists)
            val responseString: String? = response.body?.string()

            if (!responseString.isNullOrEmpty() && response.isSuccessful) {

                for (i in 0 until numDays) {
                    val forecastJSON: JSONObject = JSONObject(responseString)

                    val forecastList: JSONArray? = forecastJSON.getJSONArray("daily")
                    val forecast: JSONObject? = forecastList?.getJSONObject(i)

                    val tempObject: JSONObject? =
                        forecastList?.getJSONObject(i)?.getJSONObject("temp")

                    val weatherArray: JSONObject? =
                        forecastList?.getJSONObject(i)?.getJSONArray("weather")?.getJSONObject(0)

                    // UNIX timestamp
                    val timestamp: String? = forecast?.optString("dt")
                    var temp: String? = tempObject?.optString("day")
                    val iconID: String? = weatherArray?.optString("icon")

                    val sdf = SimpleDateFormat("EEEE")
                    val dateFormat = Date((timestamp?.toLong() as Long) * 1000)
                    var day: String = sdf.format(dateFormat)

                    temp = "${temp?.toDouble()?.roundToInt().toString()}°F"

                    when (day) {
                        "Monday" -> day = "MON"
                        "Tuesday" -> day = "TUE"
                        "Wednesday" -> day = "WED"
                        "Thursday" -> day = "THU"
                        "Friday" -> day = "FRI"
                        "Saturday" -> day = "SAT"
                        "Sunday" -> day = "SUN"
                    }

                    fiveDayForecast.add(
                        Forecast(
                            day,
                            iconID as String,
                            temp
                        )
                    )
                }

            }
        return fiveDayForecast
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
            var windSpeed: String? = wind?.optString("speed")

            temperature = temperature?.toDouble()?.roundToInt().toString()
            windSpeed = windSpeed?.toDouble()?.roundToInt().toString()

            it.temp = "${temperature}°F"
            it.wind = "${windSpeed}mph"
//                it.precipitation = rainVolumePastHour
            it.weatherIcon = iconID
        }
    }
    return courses
    }
}