package net.annedawson.seismiccanada.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.annedawson.seismiccanada.data.model.EarthquakeFeature
import net.annedawson.seismiccanada.data.repository.EarthquakeRepository

/**
 * EarthquakeViewModel acts as the middleman between the data layer (Repository) and the UI (Compose).
 * It holds the state of the screen (list of earthquakes, loading status) and survives configuration changes like screen rotation.
 */
class EarthquakeViewModel : ViewModel() {
    // The repository is responsible for actually fetching the data from the API.
    private val repository = EarthquakeRepository()

    // _earthquakes is a private mutable state flow. We use it to update the list internally.
    // We initialize it with an empty list.
    private val _earthquakes = MutableStateFlow<List<EarthquakeFeature>>(emptyList())
    
    // earthquakes is the public read-only version of the state. The UI observes this.
    // This separation ensures the UI cannot modify the state directly, following good architecture practices.
    val earthquakes: StateFlow<List<EarthquakeFeature>> = _earthquakes.asStateFlow()

    // _isLoading tracks whether we are currently fetching data from the network.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // The init block runs immediately when the ViewModel is created.
    init {
        fetchEarthquakes()
    }

    /**
     * Triggers the network request to get earthquake data.
     * It launches a coroutine (a lightweight thread) to perform the operation asynchronously
     * so the UI doesn't freeze.
     */
    fun fetchEarthquakes() {
        // viewModelScope is a coroutine scope tied to the lifecycle of this ViewModel.
        // Jobs launched here are automatically cancelled when the ViewModel is cleared.
        viewModelScope.launch {
            _isLoading.value = true // Show loading spinner
            _earthquakes.value = repository.getRecentEarthquakes() // Fetch data and update state
            _isLoading.value = false // Hide loading spinner
        }
    }
}
