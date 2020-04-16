package com.bodnick.skimate

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CourseMapEditActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fbDatabase: FirebaseDatabase

    private lateinit var mMap: GoogleMap
    private lateinit var rotateSeekbar: SeekBar
    private lateinit var undoButton: ImageButton
    private lateinit var infoButton: Button
    private var coursePlaced: Boolean = false
    private lateinit var courseOverlay: GroundOverlay

    var lat: String = ""
    var lng: String = ""
    var name: String = ""
    var location: String = ""

    var course_lat: String = ""
    var course_lng: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_map)

        fbDatabase = FirebaseDatabase.getInstance()

        rotateSeekbar = findViewById(R.id.rotateSeekBar)
        undoButton = findViewById(R.id.undoButton)
        infoButton = findViewById(R.id.place_course_button)

        rotateSeekbar.isEnabled = false
        undoButton.isEnabled = false

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        rotateSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                courseOverlay.bearing = progress.toFloat()

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
            }
        })

        infoButton.setOnClickListener {
            if (coursePlaced) {

                val reference = fbDatabase.getReference("courses/")
                val currentUser = FirebaseAuth.getInstance().currentUser

                val bearing = courseOverlay.bearing.toString()

                if (name.isNotEmpty() && location.isNotEmpty()) {
                    val course = Course(name, location.substringAfter(","," "), course_lat, course_lng, "", "", "", "", bearing)
                    reference.push().setValue(course)

                    val intent = Intent(this@CourseMapEditActivity, CourseManagerActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
        }

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
        lat = intent.getStringExtra("lat").toString()
        lng = intent.getStringExtra("lng").toString()

        location = intent.getStringExtra("address").toString()

        name = intent.getStringExtra("name")

        val course = LatLng(lat.toDouble(), lng.toDouble())

        mMap.setOnMapLongClickListener { latLng: LatLng ->
            Log.d("CourseMapActivity", "Course placed at ${latLng.latitude}, ${latLng.longitude}")

            if ( !undoButton.isEnabled ) {
                courseOverlay = mMap.addGroundOverlay(
                    GroundOverlayOptions()
                        .image(
                            BitmapDescriptorFactory.fromBitmap(
                                R.drawable.ic_course_overlay_proper_scale.toBitmap(
                                    this
                                )
                            )
                        )
                        .position(latLng, 26f)
                )


                course_lat = latLng.latitude.toString()
                course_lng = latLng.longitude.toString()

                undoButton.isEnabled = true
            }


            if (undoButton.isEnabled) {
                rotateSeekbar.isEnabled = true
                coursePlaced = true
                infoButton.text = getString(R.string.done)
                infoButton.setBackgroundResource(R.color.colorPrimaryDark)
            }
        }

        undoButton.setOnClickListener {
            courseOverlay.remove()

            // Change info button back to default instructions b/c course has been removed from the map
            infoButton.text = getString(R.string.course_placement_instructions)
            infoButton.setBackgroundResource(R.color.colorAccent)

            rotateSeekbar.isEnabled = false
            coursePlaced = false
            undoButton.isEnabled = false
        }

        val zoomLevel = 18.0f
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(course, zoomLevel))
    }

    private fun Int.toBitmap(context: Context): Bitmap? {

        // Retrieve vector asset drawable
        val drawableAsset = ContextCompat.getDrawable(context, this) ?: return null
        drawableAsset.setBounds(0, 0, drawableAsset.intrinsicWidth, drawableAsset.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(drawableAsset.intrinsicWidth, drawableAsset.intrinsicHeight, Bitmap.Config.ARGB_8888)

        // Draw asset to bitmap
        val canvas = Canvas(bitmap)
        drawableAsset.draw(canvas)

        return bitmap
    }


}
