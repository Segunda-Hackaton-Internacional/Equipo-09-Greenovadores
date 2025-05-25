package com.example.nagomiatoru.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nagomiatoru.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import java.lang.Exception

class GymDetailActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var gymLocation: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation: LatLng

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null
    private var lightSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_detail)

        // Obtener datos del intent
        val gymName = intent.getStringExtra("gymName")
        val gymAddress = intent.getStringExtra("gymAddress")
        val gymPhone = intent.getStringExtra("gymPhone")
        val gymOpeningHours = intent.getStringExtra("gymOpeningHours")
        val gymWebsite = intent.getStringExtra("gymWebsite")
        val gymDescription = intent.getStringExtra("gymDescription")
        val gymEmail = intent.getStringExtra("gymEmail")

        findViewById<TextView>(R.id.gymNameTextView).text = gymName ?: "Nombre no disponible"
        findViewById<TextView>(R.id.gymAddressTextView).text = gymAddress ?: "Dirección no disponible"
        findViewById<TextView>(R.id.gymPhoneTextView).text = gymPhone ?: "Teléfono no disponible"
        findViewById<TextView>(R.id.gymOpeningHoursTextView).text = gymOpeningHours ?: "Horario no disponible"
        findViewById<TextView>(R.id.gymWebsiteTextView).text = gymWebsite ?: "Website no disponible"
        findViewById<TextView>(R.id.gymDescriptionTextView).text = gymDescription ?: "Descripción no disponible"
        findViewById<TextView>(R.id.gymEmailTextView).text = gymEmail ?: "Email no disponible"

        val gymLat = intent.getDoubleExtra("gymLat", 0.0)
        val gymLng = intent.getDoubleExtra("gymLng", 0.0)
        gymLocation = LatLng(gymLat, gymLng)

        // Mapa
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        supportActionBar?.hide()

        // Sensores
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        rotationSensor?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        lightSensor?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }

        // BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home // o el que quieras resaltar por defecto

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home,
                R.id.nav_profile,
                R.id.nav_shopping -> {

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

        // Mostrar un marcador en la ubicación del gimnasio
        googleMap.addMarker(MarkerOptions().position(gymLocation).title("Ubicación del Gimnasio"))

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        // Obtener la ubicación actual del usuario
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLocation = LatLng(location.latitude, location.longitude)

                googleMap.addMarker(
                    MarkerOptions()
                        .position(userLocation)
                        .title("Mi Ubicación")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))

                // Cargar la ruta desde la ubicación actual hasta el gimnasio usando OpenRouteService
                loadRouteFromOpenRouteService(userLocation, gymLocation)
            }
        }
    }

    private fun loadRouteFromOpenRouteService(origin: LatLng, destination: LatLng) {
        val apiKey = getString(R.string.openroute_api_key)
        val url = "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=$apiKey&start=${origin.longitude},${origin.latitude}&end=${destination.longitude},${destination.latitude}"

        Log.d("GymDetail", "Solicitando ruta a OpenRouteService: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                Log.d("GymDetail", "Respuesta de OpenRouteService: $responseBody")

                if (responseBody != null) {
                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val routes = jsonResponse.getJSONArray("features")
                        if (routes.length() > 0) {
                            val geometry = routes.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates")
                            val polylineOptions = PolylineOptions()
                                .color(android.graphics.Color.GREEN)
                            for (i in 0 until geometry.length()) {
                                val point = geometry.getJSONArray(i)
                                val lng = point.getDouble(0)
                                val lat = point.getDouble(1)
                                polylineOptions.add(LatLng(lat, lng))
                            }
                            runOnUiThread {
                                googleMap.addPolyline(polylineOptions) // Dibujar la ruta en el mapa
                            }
                        } else {
                            Log.e("GymDetail", "No se encontraron rutas.")
                        }
                    } catch (e: Exception) {
                        Log.e("GymDetail", "Error procesando la respuesta de OpenRouteService: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("GymDetail", "Error en la solicitud a OpenRouteService: ${e.message}")
                e.printStackTrace()
            }
        })
    }

    // Métodos para el manejo de sensores
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Verificar si googleMap ha sido inicializado antes de utilizarlo
            if (::googleMap.isInitialized) {
                when (it.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)

                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientation)

                        val bearing = Math.toDegrees(orientation[0].toDouble()).toFloat()

                        val cameraPosition = CameraPosition.Builder()
                            .target(googleMap.cameraPosition.target) // Mantener la posición actual
                            .zoom(googleMap.cameraPosition.zoom) // Mantener el nivel de zoom actual
                            .bearing(bearing) // Ajustar la orientación del mapa según el sensor de rotación
                            .tilt(googleMap.cameraPosition.tilt) // Mantener la inclinación actual
                            .build()

                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    }

                    else -> {
                    }
                }
            } else {
                Log.e("SensorError", "Google Map no está inicializado todavía.")
            }
        }
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        rotationSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }
}