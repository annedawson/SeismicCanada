package net.annedawson.seismiccanada.data.model

import com.google.gson.annotations.SerializedName

data class EarthquakeResponse(
    val features: List<EarthquakeFeature>
)

data class EarthquakeFeature(
    val id: String,
    val properties: EarthquakeProperties,
    val geometry: EarthquakeGeometry
)

data class EarthquakeProperties(
    val mag: Double,
    val place: String,
    val time: Long,
    val url: String,
    val alert: String?, // "green", "yellow", "orange", "red"
    val status: String,
    val type: String,
    val title: String
)

data class EarthquakeGeometry(
    val coordinates: List<Double> // longitude, latitude, depth
)
