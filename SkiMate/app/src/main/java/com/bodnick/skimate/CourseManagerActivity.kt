package com.bodnick.skimate

import android.os.Bundle
import android.view.View
import android.widget.Button
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

            val dialog = createCourseDialog()

            dialog.show()

            val name = dialog.findViewById<EditText>(R.id.new_name)
            val address = dialog.findViewById<EditText>(R.id.new_address)

            val addButton = dialog.findViewById<Button>(R.id.addButton)
            val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

            dialog.findViewById<Button>(R.id.addButton)?.setText(R.string.add)
            dialog.findViewById<Button>(R.id.cancelButton)?.setText(R.string.cancel)

            addButton?.setOnClickListener(View.OnClickListener {
                // GO TO CourseMapEditActivity
            })

            cancelButton?.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })
        }

    }


    private fun createCourseDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        // Get the layout inflater
        val inflater = this.layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the manager layout
        builder.setView(inflater.inflate(R.layout.dialog_create_course, null))

        val dialog = builder.create()

        return dialog

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