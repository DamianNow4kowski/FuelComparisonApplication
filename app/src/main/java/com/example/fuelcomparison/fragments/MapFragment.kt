package com.example.fuelcomparison.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fuelcomparison.R
import com.example.fuelcomparison.controllers.MapFragmentController
import com.example.fuelcomparison.data.GasStation
import com.example.fuelcomparison.model.MapFragmentModel
import com.example.fuelcomparison.source.AsyncConnectionTaskFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback,
    OnMapLongClickListener, OnMarkerClickListener, OnCameraIdleListener {
    private var controller: MapFragmentController? = null
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = MapFragmentController(
            this, MapFragmentModel(),
            Geocoder(activity, Locale.getDefault()), AsyncConnectionTaskFactory()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap!!.setOnMapLongClickListener(this)
        this.googleMap!!.setOnMarkerClickListener(this)
        this.googleMap!!.setOnCameraIdleListener(this)
        zoomMapToPosition(LatLng(50.06222046451725, 19.938685186207294))
    }

    override fun onMapLongClick(latLng: LatLng) {}
    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }

    override fun onCameraIdle() {
        val cameraBounds = googleMap!!.projection.visibleRegion.latLngBounds
        controller!!.processRetrieveGasStations(cameraBounds)
    }

    @Deprecated("")
    fun setController(controller: MapFragmentController?) {
        this.controller = controller
    }

    fun zoomMapToPosition(position: LatLng?) {
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f), null)
    }

    fun clearCurrentStations() {
        controller!!.clearGasStations(googleMap!!)
    }

    fun createGasStationMarker(gasStation: GasStation): Marker {
        return googleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(gasStation.latitude, gasStation.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
        )
    }
}