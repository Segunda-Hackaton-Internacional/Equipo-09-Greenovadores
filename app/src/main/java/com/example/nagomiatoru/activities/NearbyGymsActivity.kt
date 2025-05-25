package com.example.nagomiatoru.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nagomiatoru.R
import com.example.nagomiatoru.models.GymDetails
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class NearbyGymsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var rotationVectorSensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var lastUpdateTime: Long = 0
    private val updateInterval: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_gyms)

        supportActionBar?.hide()

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home, R.id.nav_profile, R.id.nav_shopping -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra("fragment", item.itemId)
                    })
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        googleMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
                loadGymLocationsFromOpenStreetMap(userLocation)
            }
        }

        googleMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }

        googleMap.setOnInfoWindowClickListener { marker ->
            val gymDetails = marker.tag as? GymDetails
            if (gymDetails != null) {
                val intent = Intent(this, GymDetailActivity::class.java).apply {
                    putExtra("gymLat", marker.position.latitude)
                    putExtra("gymLng", marker.position.longitude)
                    putExtra("gymName", gymDetails.name)
                    putExtra("gymAddress", gymDetails.address)
                    putExtra("gymPhone", gymDetails.phone)
                    putExtra("gymOpeningHours", gymDetails.openingHours)
                    putExtra("gymWebsite", gymDetails.website)
                    putExtra("gymDescription", gymDetails.description)
                    putExtra("gymEmail", gymDetails.email)
                }
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapView.getMapAsync(this)
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGymLocationsFromOpenStreetMap(userLocation: LatLng) {
        val query = """
           [out:json];
           node
             ["leisure"="fitness_centre"]
             (around:5000,${userLocation.latitude},${userLocation.longitude});
           out;
        """.trimIndent()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://overpass-api.de")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OpenStreetMapService::class.java)
        val call = service.getNearbyGyms(query)

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val gymList = response.body()?.getAsJsonArray("elements")
                    gymList?.forEach { gym ->
                        val obj = gym.asJsonObject
                        val lat = obj["lat"]?.asDouble ?: return@forEach
                        val lon = obj["lon"]?.asDouble ?: return@forEach
                        val tags = obj.getAsJsonObject("tags")

                        val name = tags?.get("name")?.asString ?: "Gimnasio sin nombre"
                        val street = tags?.get("addr:street")?.asString ?: "Calle no disponible"
                        val number = tags?.get("addr:housenumber")?.asString ?: ""
                        val postal = tags?.get("addr:postcode")?.asString ?: ""
                        val city = tags?.get("addr:city")?.asString ?: "Ciudad no disponible"
                        val hours = tags?.get("opening_hours")?.asString ?: "Horario no disponible"
                        val phone = tags?.get("phone")?.asString ?: "Teléfono no disponible"
                        val web = tags?.get("website")?.asString ?: "Website no disponible"
                        val desc = tags?.get("description")?.asString ?: "Descripción no disponible"
                        val email = tags?.get("email")?.asString ?: "Email no disponible"

                        val marker = googleMap.addMarker(
                            MarkerOptions().position(LatLng(lat, lon)).title(name)
                        )

                        marker?.tag = GymDetails(
                            name, "$street $number, $postal, $city", phone, hours, web, desc, email
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@NearbyGymsActivity, "Error cargando gimnasios", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface OpenStreetMapService {
        @GET("/api/interpreter")
        fun getNearbyGyms(@Query("data") query: String): Call<JsonObject>
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (::googleMap.isInitialized && it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                val now = System.currentTimeMillis()
                if (now - lastUpdateTime >= updateInterval) {
                    lastUpdateTime = now
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val bearing = Math.toDegrees(orientation[0].toDouble()).toFloat()

                    val cameraPos = CameraPosition.Builder()
                        .target(googleMap.cameraPosition.target)
                        .zoom(googleMap.cameraPosition.zoom)
                        .bearing(bearing)
                        .tilt(googleMap.cameraPosition.tilt)
                        .build()

                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos))
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        mapView.onPause()
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }
}
