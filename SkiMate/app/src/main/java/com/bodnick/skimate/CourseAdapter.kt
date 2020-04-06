package com.bodnick.skimate

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso


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

        val lat = currentCourse.lat.toDouble()
        val lng = currentCourse.lng.toDouble()
        val location = LatLng(lat, lng)

        Picasso.get().setIndicatorsEnabled(true)
        Picasso.get()
            .load("https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lng&scale=2&zoom=20&size=400x400&maptype=hybrid&key=$apiKey")
            .into(holder.mapView)



        // Setting  weather dynamic weather icon
        when (currentCourse.weatherIcon) {
            "01d" -> holder.weatherIcon.setImageResource(R.drawable.ic_01d);
            "01n" -> holder.weatherIcon.setImageResource(R.drawable.ic_01n);
            "02d" -> holder.weatherIcon.setImageResource(R.drawable.ic_02d);
            "02n" -> holder.weatherIcon.setImageResource(R.drawable.ic_02n);
            "03d" -> holder.weatherIcon.setImageResource(R.drawable.ic_03d);
            "03n" -> holder.weatherIcon.setImageResource(R.drawable.ic_03n);
            "04d" -> holder.weatherIcon.setImageResource(R.drawable.ic_04d);
            "04n" -> holder.weatherIcon.setImageResource(R.drawable.ic_04n);
            "09d" -> holder.weatherIcon.setImageResource(R.drawable.ic_09d);
            "09n" -> holder.weatherIcon.setImageResource(R.drawable.ic_09n);
            "10d" -> holder.weatherIcon.setImageResource(R.drawable.ic_10d);
            "10n" -> holder.weatherIcon.setImageResource(R.drawable.ic_10n);
            "11d" -> holder.weatherIcon.setImageResource(R.drawable.ic_11d);
            "11n" -> holder.weatherIcon.setImageResource(R.drawable.ic_11n);
            "13d" -> holder.weatherIcon.setImageResource(R.drawable.ic_13d);
            "13n" -> holder.weatherIcon.setImageResource(R.drawable.ic_13n);
            "50d" -> holder.weatherIcon.setImageResource(R.drawable.ic_50d);
            "50n" -> holder.weatherIcon.setImageResource(R.drawable.ic_50n);
        }



//
//        mMap.addMarker(MarkerOptions().position(location).title(currentCourse.name))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
//        holder.map.onResume();// needed to get the map to display immediately


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

        val weatherIcon: ImageView = itemView.findViewById(R.id.course_current_weather_icon)

        val mapView: ImageView = itemView.findViewById(R.id.course_thumbnail)

    }

}