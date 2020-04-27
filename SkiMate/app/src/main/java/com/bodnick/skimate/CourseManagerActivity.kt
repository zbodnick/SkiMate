package com.bodnick.skimate

import android.content.Intent
import android.content.SharedPreferences
import android.database.DataSetObserver
import android.location.Address
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.doAsync

class CourseManagerActivity : AppCompatActivity() {

    private lateinit var fbDatabase: FirebaseDatabase

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: CourseAdapter

    private lateinit var addCourseButton: Button

    private lateinit var updatedCourses: MutableList<Course>

    private var geocodedLocation: Location = Location("", "", "")

    private var courseAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_manager)

        // Initialize db
        fbDatabase = FirebaseDatabase.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email as String
        val filteredEmail = email.filter{ it.isLetterOrDigit() || it.isWhitespace() }
        val reference = fbDatabase.getReference("$filteredEmail/courses/")

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        setTitle(R.string.manager_title);

        recyclerView = findViewById(R.id.recyclerView)

        addCourseButton = findViewById(R.id.add_course_button)

        // Set the RecyclerView direction to vertical (the default)
        recyclerView.layoutManager = LinearLayoutManager(this)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@CourseManagerActivity,
                    "Failed to retrieve Courses Error: ${databaseError.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val courses = mutableListOf<Course>()
                dataSnapshot.children.forEach { data ->
                    val course = data.getValue(Course::class.java)
                    if (course != null) {
                        courses.add(course)
                    }
                }
                adapter = CourseAdapter(courses, this@CourseManagerActivity, this@CourseManagerActivity)
                recyclerView.adapter = adapter
                updateCourses(courses)
                adapter.notifyDataSetChanged()
            }

        })

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
                getGeocode(address?.text.toString())
                if (geocodedLocation.address.isEmpty() || geocodedLocation.lat.isEmpty() || geocodedLocation.lng.isEmpty()) {
                    // Geocode inputted address
                    getGeocode(address?.text.toString())
                } else {

                val arrayAdapter =
                    ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice)
                arrayAdapter.add(geocodedLocation.address)

                if (geocodedLocation.address.isNotEmpty()) {

                    android.app.AlertDialog.Builder(this)
                        .setTitle("Search Results")
                        .setAdapter(arrayAdapter) { dialog, which ->

                            val intent = Intent(this@CourseManagerActivity, CourseMapEditActivity::class.java)

                            intent.putExtra("name", name?.text.toString())
                            intent.putExtra("address", geocodedLocation.address)
                            intent.putExtra("lat", geocodedLocation.lat)
                            intent.putExtra("lng", geocodedLocation.lng)

                            this.startActivity(intent)
                            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            Toast.makeText(
                                this,
                                "Locating course at ${geocodedLocation.lat} | ${geocodedLocation.lng}...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("CANCEL") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    throwError(0)
//                    Toast.makeText(this, "${name?.text.toString()}", Toast.LENGTH_SHORT).show()

                }
                }
            })

            cancelButton?.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })
        }

    }

    private fun throwError(errorCode: Int) {
        val errorArr = resources.getStringArray(R.array.errors_array)
        Toast.makeText(this, "${errorArr[errorCode]}", Toast.LENGTH_SHORT).show()
    }

    private fun getGeocode(address: String) {

        doAsync {
            val geocodeManager = GoogleGeocodeManager()

            try {
                val apiKey = "AIzaSyAW7C5nCNKRjEV04ByKBVk0GPEZTgeSugA"

                geocodedLocation = geocodeManager.geocode (
                    apiKey = apiKey,
                    address = address
                )

            } catch (exception: Exception) {
                exception.printStackTrace()
                // Switch back to the UI Thread
                runOnUiThread {
                    Toast.makeText(
                        this@CourseManagerActivity,
                        "Error: Cannot retrieve data from Google Geocode API: $exception",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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

    private fun updateCourses(courses: MutableList<Course>) {
        doAsync {
            val weatherManager = OpenWeatherManager()

            try {
                // Read OpenWeather API key from XML file
                val apiKey = getString(R.string.openWeatherAPI)

                updatedCourses = weatherManager.retrieveWeatherData (
                    apiKey = apiKey,
                    courses = courses
                ) as MutableList<Course>

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
}