package com.bodnick.skimate

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView


class CourseAdapter(val courses: List<Course>) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    // The adapter needs to render a new row and needs to know what XML file to use
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Layout inflation (read & parse XML file and return a reference to the root layout)
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_course_manager_row, parent, false)
        return ViewHolder(view)
    }

    // The adapter has a row that's ready to be rendered and needs the content filled in
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCourse = courses[position]

        holder.name.text = currentCourse.name
        holder.location.text = currentCourse.location
        holder.temp.text = currentCourse.temp
        holder.precipitation.text = currentCourse.precipitation
        holder.wind.text = currentCourse.wind

//        holder.name.text = currentCourse.lat
//        holder.name.text = currentCourse.lng
//        holder.name.text = currentCourse.thumbnail
//        holder.name.text = currentCourse.weatherIcon

//        if (currentCourse. .isEmpty()) {
//            holder. .isVisible = false
//        }

//    val webIntent: Intent = Intent(Intent.ACTION_CALL, Uri.parse(currentBusiness.url))
//    val phoneIntent: Intent = Intent(Intent.ACTION_CALL, Uri.parse(currentBusiness.url))
//
//    holder.url.setOnClickListener(View.OnClickListener {
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.data = Uri.parse(businesses[holder.adapterPosition].url)
//        holder.startActivity(intent)
//    })

    }


    // Return the total number of rows you expect your list to have
    override fun getItemCount(): Int {
        return courses.size
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
        val precipitation: TextView = itemView.findViewById(R.id.course_rain_percent)
        val wind: TextView = itemView.findViewById(R.id.course_wind_speed)
        val name: TextView = itemView.findViewById(R.id.course_name)

        //        val thumbnail: ImageView = itemView.findViewById(R.id.url)

    }
}