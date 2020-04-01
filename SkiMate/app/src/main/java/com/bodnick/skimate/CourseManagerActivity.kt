package com.bodnick.skimate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CourseManagerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_manager)

        setTitle(R.string.manager_title);

        recyclerView = findViewById(R.id.recyclerView)

        // Set the RecyclerView direction to vertical (the default)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val courses = getCourses()
        val adapter = CourseAdapter(courses)
        recyclerView.adapter = adapter

    }

    private fun getCourses(): List<Course> {
        return listOf(
            Course (
                name = "PB Water Sports",
                location = "Stub Canal",
                lat = " ",
                lng = " ",
//                thumbnail = " ",
                weatherIcon = " ",
                temp = "77°F",
                precipitation = "32%",
                wind = "10mph"
            ),
            Course (
                name = "PB Training Center",
                location = "Lake 38",
                lat = " ",
                lng = " ",
//                thumbnail = " ",
                weatherIcon = " ",
                temp = "84°F",
                precipitation = "0%",
                wind = "6mph"
            ),
            Course (
                name = "Camp Ramah",
                location = "Skeleton Lake",
                lat = " ",
                lng = " ",
//                thumbnail = " ",
                weatherIcon = " ",
                temp = "54°F",
                precipitation = "8%",
                wind = "12mph"
            )
        )
    }
}