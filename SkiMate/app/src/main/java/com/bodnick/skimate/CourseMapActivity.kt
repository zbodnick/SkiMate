package com.bodnick.skimate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class CourseMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var resizeSeekbar: SeekBar
    private lateinit var rotateSeekbar: SeekBar
    private var resizeProgress: Int = 0
    private var rotateProgress: Int = 0
    private var rotatingCourse: Boolean = true
    private lateinit var courseOverlay: GroundOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_map)

        resizeSeekbar = findViewById(R.id.resizeSeekBar)
        rotateSeekbar = findViewById(R.id.rotateSeekBar)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        rotateSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
//                if (!preferencesSaved) {
//                    restuarantCount.text = "Results (${restaurantSeekbar.progress}):"
//                } else {
//                    restuarantCount.text = "Results (${preferences.getInt("numRestaurants",0)}):"
//                }

                courseOverlay.bearing = progress.toFloat()

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
            }
        })

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

//        mMap.setMinZoomPreference(25.0f)
//        mMap.setMaxZoomPreference(30.0f)

        val course = LatLng(lat.toDouble(), lng.toDouble())

//        courseMarker = mMap.addMarker(MarkerOptions()
//            .flat(rotatingCourse)
//            .position(course)
//            .anchor(0.5f, 0.5f)
//            .title(name)
//            .icon(BitmapDescriptorFactory.fromBitmap(R.drawable.ic_course_overlay.toBitmap(this, resizeProgress))))

        courseOverlay = mMap.addGroundOverlay (
            GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(R.drawable.ic_course_overlay_proper_scale.toBitmap(this, resizeProgress)))
                .position(course, 25f)
        )

        val zoomLevel = 16.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(course, zoomLevel))
    }

    private fun Int.toBitmap(context: Context, resizeAmount: Int): Bitmap? {

        // Retrieve vector asset drawable
        val drawableAsset = ContextCompat.getDrawable(context, this) ?: return null
        drawableAsset.setBounds(0, 0, drawableAsset.intrinsicWidth, drawableAsset.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(drawableAsset.intrinsicWidth+resizeAmount, drawableAsset.intrinsicHeight+resizeAmount, Bitmap.Config.ARGB_8888)

        // Draw asset to bitmap
        val canvas = Canvas(bitmap)
        drawableAsset.draw(canvas)

        return bitmap
    }


}
