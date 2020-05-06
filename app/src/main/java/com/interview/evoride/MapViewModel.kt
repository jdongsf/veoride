package com.interview.evoride

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil

@Suppress("DEPRECATION")
class MapViewModel : ViewModel() {

    private var startTime: Long = 0
    private var dst: LatLng? = null
    private var totalDistance: Double = 0.0

    // set 2 destination markers as demo purpose, click one marker, the other disappeared
    private lateinit var marker1: Marker
    private lateinit var marker2: Marker
    private val route = mutableListOf<LatLng>()

    lateinit var map: GoogleMap
    val arrived = MutableLiveData<Boolean>()

    fun initMap(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true

        map.setOnMarkerClickListener { marker ->
            if (marker1.equals(marker)) {
                marker2.remove()
                dst = LatLng(marker1.position.latitude, marker1.position.longitude)
            } else if (marker2.equals(marker)) {
                marker1.remove()
                dst = LatLng(marker2.position.latitude, marker2.position.longitude)
            }
            false
        }

        map.setOnMyLocationChangeListener { arg ->
            if (startTime == 0L) return@setOnMyLocationChangeListener
            val loc = LatLng(arg.latitude, arg.longitude)
            map.addMarker(MarkerOptions().position(loc).title("Start"))
            if (route.isNotEmpty()) {
                val pre = route[route.size - 1]
                if (pre.equals(loc)) return@setOnMyLocationChangeListener
                map.addPolyline(PolylineOptions().clickable(true).add(pre, loc))
                totalDistance += SphericalUtil.computeDistanceBetween(pre, loc)
                // reach the destination
                if (dst != null && SphericalUtil.computeDistanceBetween(pre, dst) < RADIUS) {
                    arrived.value = true
                }
            } else {
                val dest1 = SphericalUtil.computeOffset(loc, 500.0, 0.0)
                marker1 = map.addMarker(MarkerOptions().position(dest1).title("End1"))
                val dest2 = SphericalUtil.computeOffset(loc, 500.0, 90.0)
                marker2 = map.addMarker(MarkerOptions().position(dest2).title("End2"))
            }
            route.add(loc)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
        }
    }

    fun travel() {
        startTime = System.currentTimeMillis()
    }

    fun totalDistance(): String {
        return String.format("Total distance is %.2f meters", totalDistance)
    }

    fun totalTime(): String {
        val seconds = (System.currentTimeMillis() - startTime) / 1000
        val s: Long = seconds % 60
        val m: Long = seconds / 60 % 60
        val h: Long = seconds / (60 * 60) % 24
        return String.format("Total time is %d:%02d:%02d", h, m, s)
    }

    companion object {
        private const val RADIUS: Double = 5.0
    }

}
