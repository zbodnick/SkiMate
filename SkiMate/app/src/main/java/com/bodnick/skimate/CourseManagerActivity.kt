package com.bodnick.skimate

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.doAsync

class CourseManagerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CourseAdapter

    private lateinit var addCourseButton: FloatingActionButton

    private lateinit var updatedCourses: List<Course>

    private var newCourseName: String = ""
    private var newCourseAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_manager)

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        setTitle(R.string.manager_title);

        recyclerView = findViewById(R.id.recyclerView)

        addCourseButton = findViewById(R.id.add_course_button)

        // Set the RecyclerView direction to vertical (the default)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val courses = getCourses()

        adapter = CourseAdapter(courses, this, this)
        recyclerView.adapter = adapter

        updateCourses(courses)
        adapter.notifyDataSetChanged()

        addCourseButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Course")

//            // Set up input
//            val name = EditText(this)
//            val address = EditText(this)
//
//            // Specify the type of input expected
//            name.inputType = InputType.TYPE_CLASS_TEXT
//            address.inputType = InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS

//            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.dialog_create_course, parent, false)
//            builder.setView(View.inflate((R.layout., null))

            // Set up the buttons
            builder.setPositiveButton("Add",
                DialogInterface.OnClickListener { dialog, which -> newCourseName = name.text.toString() })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }

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
                    adapter = CourseAdapter(updatedCourses, this@CourseManagerActivity, this@CourseManagerActivity)
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
                lat = "26.682656",
                lng = "-80.071766",
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
                weatherIcon = " ",
                temp = "54°F",
                precipitation = "8%",
                wind = "12mph"
            )
        )
    }
}