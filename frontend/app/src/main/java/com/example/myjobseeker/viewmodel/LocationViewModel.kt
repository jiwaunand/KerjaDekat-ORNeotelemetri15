package com.example.myjobseeker.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.*

class LocationViewModel : ViewModel() {
    private val _currentLocation = mutableStateOf("Mencari lokasi...")
    val currentLocation: State<String> = _currentLocation

    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            val city = address.locality ?: address.subAdminArea ?: ""
                            val province = address.adminArea ?: ""
                            _currentLocation.value = if (city.isNotEmpty() && province.isNotEmpty()) {
                                "$city, $province"
                            } else {
                                city.ifEmpty { province }.ifEmpty { "Lokasi tidak diketahui" }
                            }
                        } else {
                            _currentLocation.value = "Lokasi tidak ditemukan"
                        }
                    } catch (e: Exception) {
                        _currentLocation.value = "Gagal memproses lokasi"
                    }
                } else {
                    _currentLocation.value = "Lokasi tidak tersedia"
                }
            }
            .addOnFailureListener {
                _currentLocation.value = "Gagal mengambil lokasi"
            }
    }
}
