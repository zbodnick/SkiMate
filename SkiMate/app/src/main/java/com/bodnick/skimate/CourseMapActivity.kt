package com.bodnick.skimate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import sun.jvm.hotspot.utilities.IntArray


class CourseMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val lat = intent.getStringExtra("lat")
        val lng = intent.getStringExtra("lng")
        val name = intent.getStringExtra("name")

        val course = LatLng(lat.toDouble(), lng.toDouble())

        val courseOverlay = GroundOverlayOptions()
            .image(bitmapDescriptorFromVector(this, R.drawable.ic_course_overlay))
            .position(course, 8600f, 6500f)
        mMap.addGroundOverlay(courseOverlay)

        // Add a marker at the course and move the camera
        mMap.addMarker(MarkerOptions().position(course).title(name))

        val zoomLevel = 16.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(course, zoomLevel))
    }

    private fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {
        val background =
            ContextCompat.getDrawable(context, vectorDrawableResourceId)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable =
            ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(
            40,
            20,
            vectorDrawable.intrinsicWidth + 40,
            vectorDrawable.intrinsicHeight + 20
        )
        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}
