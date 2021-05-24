package site.antontikhonov.android.presentation.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import site.antontikhonov.android.domain.contactlocation.ContactLocationEntity
import site.antontikhonov.android.presentation.R
import site.antontikhonov.android.presentation.di.HasComponent
import site.antontikhonov.android.presentation.extensions.injectViewModel
import site.antontikhonov.android.presentation.viewmodels.ContactMapsViewModel
import javax.inject.Inject

private const val MAP_ZOOM = 15F
private const val PADDING = 150

class ContactMapsFragment : Fragment(R.layout.fragment_maps) {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: ContactMapsViewModel
    private var buttonApprove: Button? = null
    private var buttonShowLocations: Button? = null
    private lateinit var map: GoogleMap
    private var currentMarker: Marker? = null
    private var markers = ArrayList<Marker?>()
    private lateinit var contactId: String
    private var isLocationExist: Boolean = false
    private var progressBar: ProgressBar? = null

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        viewModel.getLocationById(contactId)
        viewModel.location.observe(viewLifecycleOwner, { location ->
            isLocationExist = true
            val point = LatLng(location.latitude, location.longitude)
            currentMarker = map.addMarker(MarkerOptions().position(point))
            val cameraPosition = CameraPosition.Builder()
                .target(point)
                .zoom(MAP_ZOOM)
                .build()
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        })
        map.setOnMapClickListener(onMapClickListener)
    }

    private val onMapClickListener = GoogleMap.OnMapClickListener { point ->
        if(buttonShowLocations?.text == getString(R.string.hideAll)) {
            hideAllMarkers()
        }
        currentMarker?.remove()
        currentMarker = map.addMarker(MarkerOptions().position(point))
        buttonApprove?.visibility = View.VISIBLE
        buttonApprove?.setOnClickListener { saveDataLocation(point) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as HasComponent)
            .getAppComponent()
            .plusContactMapsContainer()
            .inject(this)
        super.onCreate(savedInstanceState)
        viewModel = injectViewModel(factory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.contact_map_title)
        progressBar = view.findViewById(R.id.progress_bar_contact_maps)
        contactId = requireNotNull(arguments?.getString(EXTRA_CONTACT_ID))
        buttonApprove = view.findViewById(R.id.button_approve)
        buttonShowLocations = view.findViewById(R.id.button_show_locations)
        buttonShowLocations?.setOnClickListener {
            if(buttonShowLocations?.text == getString(R.string.showAll)) {
                showAllMarkers()
            } else {
                hideAllMarkers()
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            when(isLoading) {
                true -> progressBar?.visibility = View.VISIBLE
                false -> progressBar?.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        buttonApprove = null
        buttonShowLocations = null
        super.onDestroyView()
    }

    private fun saveDataLocation(latLng: LatLng) {
        val location = ContactLocationEntity(
            contactId,
            reverseGeocoding(latLng),
            latLng.latitude,
            latLng.longitude
        )
        if(isLocationExist) {
            viewModel.updateLocation(location)
        } else {
            viewModel.addLocation(location)
        }
        buttonApprove?.visibility = View.GONE
    }

    private fun showAllMarkers() {
        viewModel.getLocations()
        viewModel.allLocations.observe(viewLifecycleOwner, { locations ->
            currentMarker?.remove()
            if(locations.isEmpty()) {
                buttonShowLocations?.isEnabled = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_location_data),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                buttonShowLocations?.isEnabled = true
                val builder = LatLngBounds.builder()
                locations.forEach { location ->
                    builder.include(LatLng(location.latitude, location.longitude))
                    markers.add(
                        map.addMarker(
                            MarkerOptions().position(LatLng(location.latitude, location.longitude))
                        )
                    )
                }
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), PADDING))
                buttonShowLocations?.text = getString(R.string.hideAll)
            }
        })
    }

    private fun hideAllMarkers() {
        markers.forEach { marker ->
            marker?.remove()
        }
        markers.clear()
        buttonShowLocations?.text = getString(R.string.showAll)
    }

    private fun reverseGeocoding(latLng: LatLng): String {
        // TODO добавить обратный геокодинг
        return "ул. Максима Горького, 150, Ижевск, республика Удмуртия"
    }

    companion object {
        fun newInstance(id: String): ContactMapsFragment {
            val args = Bundle()
            args.putString(EXTRA_CONTACT_ID, id)
            val fragment = ContactMapsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}