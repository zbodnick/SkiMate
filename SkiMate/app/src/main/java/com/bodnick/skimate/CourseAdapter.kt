package com.bodnick.skimate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CourseAdapter(val courses: MutableList<Course>, val context: Context, val activity: Activity) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    // The adapter needs to render a new row and needs to know what XML file to use
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Layout inflation (read & parse XML file and return a reference to the root layout)
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_course_manager_row_2, parent, false)

        return ViewHolder(view)

    }

    // The adapter has a row that's ready to be rendered and needs the content filled in
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCourse = courses[position]

        holder.name.text = currentCourse.name
        holder.location.text = currentCourse.location
        holder.temp.text = currentCourse.temp
        holder.wind.text = currentCourse.wind

        val lat = currentCourse.lat
        val lng = currentCourse.lng

        holder.viewCourseButton.setOnClickListener {
            // Click listener for view course button
            val intent = Intent(context, CourseMapViewActivity::class.java)

            intent.putExtra("lat", courses[position].lat)
            intent.putExtra("lng", courses[position].lng)
            intent.putExtra("name", courses[position].name)
            intent.putExtra("bearing", courses[position].bearing)

            intent.putExtra("windSpeed", courses[position].wind)
            intent.putExtra("windDegree", courses[position].windDeg)

            context.startActivity(intent)

            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val apiKey = "AIzaSyAW7C5nCNKRjEV04ByKBVk0GPEZTgeSugA"

        Picasso.get().setIndicatorsEnabled(true)
        Picasso.get()
            .load("https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lng&scale=2&zoom=18&size=400x400&maptype=terrain&key=$apiKey")
            .into(holder.mapView)

        // Setting  weather dynamic weather icon
        when (currentCourse.weatherIcon) {
            "01d" -> holder.weatherIcon.setImageResource(R.drawable.ic_01d)
            "01n" -> holder.weatherIcon.setImageResource(R.drawable.ic_01n)
            "02d" -> holder.weatherIcon.setImageResource(R.drawable.ic_02d)
            "02n" -> holder.weatherIcon.setImageResource(R.drawable.ic_02n)
            "03d" -> holder.weatherIcon.setImageResource(R.drawable.ic_03d)
            "03n" -> holder.weatherIcon.setImageResource(R.drawable.ic_03n)
            "04d" -> holder.weatherIcon.setImageResource(R.drawable.ic_04d)
            "04n" -> holder.weatherIcon.setImageResource(R.drawable.ic_04n)
            "09d" -> holder.weatherIcon.setImageResource(R.drawable.ic_09d)
            "09n" -> holder.weatherIcon.setImageResource(R.drawable.ic_09n)
            "10d" -> holder.weatherIcon.setImageResource(R.drawable.ic_10d)
            "10n" -> holder.weatherIcon.setImageResource(R.drawable.ic_10n)
            "11d" -> holder.weatherIcon.setImageResource(R.drawable.ic_11d)
            "11n" -> holder.weatherIcon.setImageResource(R.drawable.ic_11n)
            "13d" -> holder.weatherIcon.setImageResource(R.drawable.ic_13d)
            "13n" -> holder.weatherIcon.setImageResource(R.drawable.ic_13n)
            "50d" -> holder.weatherIcon.setImageResource(R.drawable.ic_50d)
            "50n" -> holder.weatherIcon.setImageResource(R.drawable.ic_50n)
        }

        // Handle course menu listener - edit and delete
        holder.editCourseButton.setOnClickListener(View.OnClickListener {

            // Initialize popup menu
            val popup = PopupMenu(context, holder.editCourseButton)

            // Inflate menu from xml resource
            popup.inflate(R.menu.course_menu)

            // Add click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_course_menu_item ->  {
                        deleteCourse(courses[position])
                        courses.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, courses.size)
                        true
                    }
                    R.id.edit_course_menu_item ->  {
                        true
                    }
                    else -> false
                }
            }

            // Display the popup
            popup.show()
        })

    }

    private fun deleteCourse(course: Course) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email as String
        val filteredEmail = email.filter{ it.isLetterOrDigit() || it.isWhitespace() }
        val dbReference = FirebaseDatabase.getInstance().getReference("$filteredEmail/courses/")

        dbReference.child(course.id).removeValue()

        Toast.makeText(
            context,
            "Removing course with ID: ${course.id}",
            Toast.LENGTH_SHORT
        ).show()

    }

    // A ViewHolder represents the Views that comprise a single row in our list (e.g.
    // our row to display a Business contains three TextViews and one ImageView).
    //
    // The "itemView" passed into the constructor comes from onCreateViewHolder because our LayoutInflater
    // ultimately returns a reference to the root View in the row's inflated layout. From there, we can
    // call findViewById to search from that root View downwards to find the Views we card about.
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val location: TextView = itemView.findViewById(R.id.course_location)
        val temp: TextView = itemView.findViewById(R.id.course_temperature)
        val wind: TextView = itemView.findViewById(R.id.course_wind_speed)
        val name: TextView = itemView.findViewById(R.id.course_name)

        val weatherIcon: ImageView = itemView.findViewById(R.id.course_current_weather_icon)

        val mapView: ImageView = itemView.findViewById(R.id.course_thumbnail)

        val viewCourseButton: ImageButton = itemView.findViewById(R.id.view_course_button)

        val editCourseButton: ImageButton = itemView.findViewById(R.id.edit_course_button)

    }

    override fun getItemCount(): Int {
        return courses.size
    }

}