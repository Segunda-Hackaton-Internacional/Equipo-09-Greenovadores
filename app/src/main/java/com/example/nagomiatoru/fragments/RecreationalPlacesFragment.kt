package com.example.nagomiatoru.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nagomiatoru.R
import com.example.nagomiatoru.adapters.PlacesAdapter
import com.example.nagomiatoru.network.GeoapifyService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecreationalPlacesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apiKey = "79c2cb54cc564c8595675040bcd35508"
    private val LOCATION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recreational_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PlacesAdapter(emptyList()) // Evita error de RecyclerView sin adaptador

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (hasLocationPermissions()) {
            requestUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val context = requireContext()
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestUserLocation() {
        if (!hasLocationPermissions()) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getPlaces(location.latitude, location.longitude)
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener tu ubicación", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error obteniendo ubicación: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getPlaces(latitude: Double, longitude: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.geoapify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GeoapifyService::class.java)
        val filter = "circle:$longitude,$latitude,3000"

        lifecycleScope.launch {
            try {
                val response = api.getRecreationalPlaces(
                    filter = filter,
                    apiKey = apiKey
                )

                if (response.isSuccessful) {
                    val placesList = response.body()?.features?.map { it.properties } ?: emptyList()

                    if (placesList.isEmpty()) {
                        Toast.makeText(requireContext(), "No se encontraron lugares recreativos cercanos", Toast.LENGTH_SHORT).show()
                    }

                    recyclerView.adapter = PlacesAdapter(placesList)
                } else {
                    Toast.makeText(requireContext(), "Error ${response.code()}: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestUserLocation()
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado. Actívalo para ver lugares cercanos.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
