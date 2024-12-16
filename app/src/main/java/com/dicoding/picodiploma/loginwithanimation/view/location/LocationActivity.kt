package com.dicoding.picodiploma.loginwithanimation.view.location

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLocationBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        private const val TAG = "LocationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fetchMaps()
    }


    private fun fetchMaps() {
        lifecycleScope.launch {
            viewModel.getMapsStories()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        styleMyMaps()
        manyMarkerMaps()

        //control
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }


    //set style maps
    private fun styleMyMaps() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_maps))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }


    //marker maps
    private fun manyMarkerMaps() {
        viewModel.resultMaps.observe(this) { listStories ->
            Log.d("MapsActivityTest", "List Stories: $listStories")
            //get data to maps
            if (listStories != null && listStories.isNotEmpty()) {
                listStories.forEach { story ->
                    val latLng = LatLng(story.lat!!.toString().toDouble(), story.lon!!.toString().toDouble())
                    Log.d("MapsActivityTest", "Lat: ${story.lat.toString().toDouble()}, Lon: ${story.lon}")

                    val markerOptions = MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)

                    mMap.addMarker(markerOptions)
                }
            } else {
                Toast.makeText(this, "Tidak ada data lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }


}