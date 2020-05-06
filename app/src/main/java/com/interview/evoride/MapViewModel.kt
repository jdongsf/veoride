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
    private var totalDistance: Double = 0.0

    private var dst: LatLng? = null
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    private val route = mutableListOf<LatLng>()

    internal lateinit var map: GoogleMap
    internal val arrived = MutableLiveData<Boolean>()

    fun initMap(googleMap: GoogleMap) {
        map = googleMap
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun init() {
        map.isMyLocationEnabled = true
        map.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng?) {
                if (startTime != 0L) return
                dst = p0
                endMarker?.remove()
                endMarker = map.addMarker(MarkerOptions().position(dst!!).title("End"))
            }
        })
        map.setOnMyLocationChangeListener { arg ->
            val loc = LatLng(arg.latitude, arg.longitude)
            if (route.isNotEmpty() && startTime != 0L) {
                val pre = route[route.size - 1]
                if (pre == loc) return@setOnMyLocationChangeListener
                map.addPolyline(PolylineOptions().clickable(true).add(pre, loc))
                totalDistance += SphericalUtil.computeDistanceBetween(pre, loc)
                // reach the destination
                if (dst != null)
                    println("distance is :" + SphericalUtil.computeDistanceBetween(loc, dst))
                if (dst != null && SphericalUtil.computeDistanceBetween(
                        loc,
                        dst
                    ) < RADIUS && arrived.value != true
                ) {
                    arrived.value = true
                }
            } else {
                if (startMarker == null)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM))
                else
                    startMarker!!.remove()
                startMarker = map.addMarker(MarkerOptions().position(loc).title("Start"))
            }

            if (startTime == 0L && route.isNotEmpty()) route[0] = loc
            else route.add(loc)
        }
    }

    fun travel() {
        startTime = System.currentTimeMillis()
        if (!map.isMyLocationEnabled)
            init()
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
        private const val ZOOM = 15.0f
        private const val RADIUS = 50.0
    }

}
