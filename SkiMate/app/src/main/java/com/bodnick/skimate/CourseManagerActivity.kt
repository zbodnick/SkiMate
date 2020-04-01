package com.bodnick.skimate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync

class CourseManagerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CourseAdapter

    private lateinit var updatedCourses: List<Course>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_manager)

        setTitle(R.string.manager_title);

        recyclerView = findViewById(R.id.recyclerView)

        // Set the RecyclerView direction to vertical (the default)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val courses = getCourses()

        adapter = CourseAdapter(courses)
        recyclerView.adapter = adapter

        updateCourses(courses)
        adapter.notifyDataSetChanged()

    }

    private fun updateCourses(courses: List<Course>) {
        doAsync {
            val weatherManager = OpenWeatherManager()

            try {
                // Read OpenWeather API key from XML file
                val apiKey = getString(R.string.openWeatherAPI)

                updatedCourses = weatherManager.retrieveWeatherData (
                    apiKey = apiKey,
                    courses = courses
                )

                runOnUiThread {
                    adapter = CourseAdapter(updatedCourses)
                    recyclerView.adapter = adapter
                }

            } catch (exception: Exception) {
                exception.printStackTrace()
                // Switch back to the UI Thread
                runOnUiThread {
                    Toast.makeText(
                        this@CourseManagerActivity,
                        "Error: Cannot retrieve data from Open Weather API: $exception",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun getCourses(): List<Course> {
        return listOf(
            Course (
                name = "PB Water Sports",
                location = "Stub Canal",
                lat = "26.685553",
                lng = "-80.073359",
//                thumbnail = " ",
                weatherIcon = " ",
                temp = "77°F",
                precipitation = "32%",
                wind = "10mph"
            ),
            Course (
                name = "PB Training Center",
                location = "Lake 38",
                lat = "26.382224",
                lng = "-80.223308",
//                thumbnail = " ",
                weatherIcon = " ",
                temp = "84°F",
                precipitation = "0%",
                wind = "6mph"
            ),
            Course (
                name = "Camp Ramah",
                location = "Skeleton Lake",
                lat = "45.226577",
                lng = "-79.497161",
//                thumbnail = " ",
                weatherIcon = " ",
                temp = "54°F",
                precipitation = "8%",
                wind = "12mph"
            )
        )
    }
}