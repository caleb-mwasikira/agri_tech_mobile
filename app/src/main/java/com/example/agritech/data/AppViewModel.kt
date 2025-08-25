package com.example.agritech.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agritech.remote.CropThreshold
import com.example.agritech.remote.Weather
import com.example.agritech.remote.weatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AppViewModel : ViewModel() {
    companion object {
        const val TAG: String = "AGRI_TECH"
    }

    private val _locations = MutableStateFlow<List<String>>(emptyList())
    private val _selectedLocation = MutableStateFlow<String?>(null)
    private val _currentDate = MutableStateFlow(LocalDate.now())
    private val _selectedCrop = MutableStateFlow<String?>(null)
    private val _todaysWeather = MutableStateFlow<Weather?>(null)
    private val _weeklyWeatherData = MutableStateFlow<List<Weather>>(emptyList())
    private val _nextWeeksWeatherCondition = MutableStateFlow<String?>(null)
    private val _suitableCrops = MutableStateFlow<List<CropThreshold>>(emptyList())
    private val _cropThreshold = MutableStateFlow<CropThreshold?>(null)
    private val _recommendations = MutableStateFlow<List<String>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    // UI State
    val locations: StateFlow<List<String>> = _locations.asStateFlow()
    val selectedLocation: StateFlow<String?> = _selectedLocation.asStateFlow()
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()
    val selectedCrop: StateFlow<String?> = _selectedCrop.asStateFlow()
    val todaysWeather: StateFlow<Weather?> = _todaysWeather.asStateFlow()
    val weeklyWeatherData: StateFlow<List<Weather>> = _weeklyWeatherData.asStateFlow()
    val nextWeeksWeatherCondition: StateFlow<String?> = _nextWeeksWeatherCondition.asStateFlow()
    val suitableCrops: StateFlow<List<CropThreshold>> = _suitableCrops.asStateFlow()
    val cropThreshold: StateFlow<CropThreshold?> = _cropThreshold.asStateFlow()
    val recommendations: StateFlow<List<String>> = _recommendations.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val errors = MutableSharedFlow<String>()

    init {
        // Fetch initial data in the background
        viewModelScope.launch {
            _isLoading.value = true
            fetchData()
            _isLoading.value = false
        }

        // Combine block will trigger whenever any of the flows change
        viewModelScope.launch {
            combine(
                _selectedLocation,
                _selectedCrop,
                _currentDate
            ) { selectedLocation, selectedCrop, currentDate ->
                Triple(selectedLocation, selectedCrop, currentDate)
            }.collect { (selectedLocation, selectedCrop, currentDate) ->
                _isLoading.value = true
                if (selectedLocation != null) {
                    fetchWeatherData()
                    if (selectedCrop != null) {
                        fetchCropData()
                    }
                }
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchData() {
        withContext(Dispatchers.IO) {
            try {
                val allLocations = weatherApi.getAllLocations()
                _locations.value = allLocations
                _selectedLocation.value = allLocations.firstOrNull()

                val allCrops = weatherApi.getAllCrops()
                _suitableCrops.value = allCrops
                _selectedCrop.value = allCrops.firstOrNull()?.name
            } catch (e: Exception) {
                Log.e(TAG, "fetchData error: ${e.message}")
                errors.emit("Failed to load initial data.")
            }
        }
    }

    private suspend fun fetchWeatherData() {
        withContext(Dispatchers.IO) {
            _selectedLocation.value?.let { location ->
                try {
                    _todaysWeather.value = weatherApi.getTodaysWeather(location)

                    val thisWeeksWeather = weatherApi.getThisWeeksWeather(
                        location,
                        _currentDate.value.monthValue,
                        _currentDate.value.dayOfMonth
                    )
                    _weeklyWeatherData.value = thisWeeksWeather

                    // Fetch next week's weather condition
                    val nextWeek = _currentDate.value.plusDays(7)
                    val nextWeeksWeatherData = weatherApi.getThisWeeksWeather(
                        location,
                        nextWeek.monthValue,
                        nextWeek.dayOfMonth
                    )
                    _nextWeeksWeatherCondition.value =
                        getMostFrequentWeatherCondition(nextWeeksWeatherData)
                } catch (e: Exception) {
                    Log.e(TAG, "fetchWeatherData error: ${e.message}")
                    errors.emit("Failed to load weather data.")
                }
            }
        }
    }

    private suspend fun fetchCropData() {
        withContext(Dispatchers.IO) {
            _selectedLocation.value?.let { location ->
                _selectedCrop.value?.let { crop ->
                    try {
                        val cropThreshold = weatherApi.getCropThresholds(crop)
                        _cropThreshold.value = cropThreshold

                        val recommendations = weatherApi.getWeeklyRecommendations(
                            location,
                            _currentDate.value.monthValue,
                            _currentDate.value.dayOfMonth,
                            crop
                        )
                        _recommendations.value = recommendations.recommendations
                    } catch (e: Exception) {
                        Log.e(TAG, "fetchCropData error: ${e.message}")
                        errors.emit("Failed to load crop data.")
                    }
                }
            }
        }
    }

    // Helper functions to update state from the UI
    fun updateSelectedLocation(location: String) {
        _selectedLocation.value = location
    }

    fun updateSelectedCrop(crop: String) {
        _selectedCrop.value = crop
    }

    fun updateCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }
}
