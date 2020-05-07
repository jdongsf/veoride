package com.interview.evoride

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil

@Suppress("DEPRECATION")
class MapViewModel : ViewModel() {

    private var startTime: Long = 0
    private var minLat: Double? = null
    private var maxLat: Double? = null
    private var minLng: Double? = null
    private var maxLng: Double? = null
    private var totalDistance: Double = 0.0

    private var dst: LatLng? = null
    private var endMarker: Marker? = null
    private var startMarker: Marker? = null
    private val route = mutableListOf<LatLng>()

    internal lateinit var map: GoogleMap
    internal val arrived = MutableLiveData<LatLngBounds>()

    fun initMap(googleMap: GoogleMap) {
        map = googleMap
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        map.isMyLocationEnabled = true
        map.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng?) {
                if (isStarted()) return
                dst = p0
                endMarker?.remove()
                endMarker = map.addMarker(MarkerOptions().position(dst!!).title("End"))
            }
        })
        map.setOnMyLocationChangeListener { arg ->
            val loc = LatLng(arg.latitude, arg.longitude)
            if (isStarted() && route.isNotEmpty()) {
                val pre = route[route.size - 1]
                if (pre == loc) return@setOnMyLocationChangeListener
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM))
                map.addPolyline(PolylineOptions().clickable(true).add(pre, loc))
                totalDistance += SphericalUtil.computeDistanceBetween(pre, loc)
                // reach the destination
//                if (dst != null)
//                    println("distance is :" + SphericalUtil.computeDistanceBetween(loc, dst))
                if (isArrived(loc)) {
                    arrived.value = LatLngBounds(LatLng(minLat!!, minLng!!), LatLng(maxLat!!, maxLng!!))
                }
            } else {
                if (startMarker == null)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM))
                else
                    startMarker?.remove()
                startMarker = map.addMarker(MarkerOptions().position(loc).title("Start"))
            }

            if (route.isNotEmpty() && !isStarted()) route[0] = loc
            else route.add(loc)

            minLat = if(minLat == null) loc.latitude else  Math.min(minLat!!, loc.latitude)
            maxLat = if (maxLat == null) loc.latitude else  Math.max(maxLat!!, loc.latitude)
            minLng = if (minLng == null) loc.longitude else  Math.max(minLng!!, loc.longitude)
            maxLng = if (maxLng == null) loc.longitude else  Math.max(maxLng!!, loc.longitude)
        }
    }

    private fun isArrived(loc: LatLng): Boolean {
        return dst != null &&
                SphericalUtil.computeDistanceBetween(loc, dst) < RADIUS &&
                arrived.value == null
    }

    private fun isStarted(): Boolean {
        return startTime != 0L
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

    fun isDstChoosed(): Boolean {
        return dst != null
    }

    companion object {
        private const val ZOOM = 15.0f
        private const val RADIUS = 50.0
    }

}
