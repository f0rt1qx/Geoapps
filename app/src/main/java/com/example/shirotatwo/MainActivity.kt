package com.example.shirotatwo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.location.Geocoder
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var btnGetLocation: Button
    private lateinit var tvLocation: TextView
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetLocation = findViewById(R.id.btnGetLocation)
        tvLocation = findViewById(R.id.tvLocation)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnGetLocation.setOnClickListener {
            if (hasLocationPermission()) {
                requestLocation()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses != null) {
            return if (addresses.isNotEmpty()) {
                val address = addresses?.get(0)
                address?.getAddressLine(0) ?: "Адрес не найден"
            } else {
                "Адрес не найден"
            }
        }
        return TODO("Provide the return value")
    }



    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude

                val address = getAddress(latitude, longitude)

                tvLocation.text = "Широта: $latitude, Долгота: $longitude\n$address"
                locationManager.removeUpdates(this)
            }


            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // Метод не используется в данном примере
            }

            override fun onProviderEnabled(provider: String) {
                // Метод не используется в данном примере
            }

            override fun onProviderDisabled(provider: String) {
                tvLocation.text = "GPS выключен"
            }

        }

        // Запрашиваем обновления местоположения
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            } else {
                tvLocation.text = "Отсутствует разрешение на местоположение"
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
git init