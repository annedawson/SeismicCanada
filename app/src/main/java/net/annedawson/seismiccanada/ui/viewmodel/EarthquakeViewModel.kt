package net.annedawson.seismiccanada.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.annedawson.seismiccanada.data.model.EarthquakeFeature
import net.annedawson.seismiccanada.data.repository.EarthquakeRepository

class EarthquakeViewModel : ViewModel() {
    private val repository = EarthquakeRepository()

    private val _earthquakes = MutableStateFlow<List<EarthquakeFeature>>(emptyList())
    val earthquakes: StateFlow<List<EarthquakeFeature>> = _earthquakes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchEarthquakes()
    }

    fun fetchEarthquakes() {
        viewModelScope.launch {
            _isLoading.value = true
            _earthquakes.value = repository.getRecentEarthquakes()
            _isLoading.value = false
        }
    }
}
