package com.interview.evoride

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        start.setOnClickListener {
            it.visibility = GONE
            viewModel.travel()
        }
        viewModel.arrived.observe(this, Observer {
            if (it) {
                viewModel.map.snapshot { bitmap -> thumbnail.setImageBitmap(bitmap) }
                timeSummary.text = viewModel.totalTime()
                distanceSummary.text = viewModel.totalDistance()

                mapFragment.view?.visibility = GONE
                summaryContainer.visibility = VISIBLE
            }
        })

        requestPermissionIfNeeded()
    }

    private fun requestPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        viewModel.initMap(googleMap)
    }

}
