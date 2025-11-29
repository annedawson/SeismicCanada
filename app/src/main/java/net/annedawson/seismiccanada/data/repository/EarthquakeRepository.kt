package net.annedawson.seismiccanada.data.repository

import net.annedawson.seismiccanada.data.model.EarthquakeFeature
import net.annedawson.seismiccanada.data.network.EarthquakeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The Repository acts as a single source of truth for data.
 * It hides the details of how data is fetched (API, database, cache) from the rest of the app.
 * In this simple app, it just forwards requests to the Retrofit API service.
 */
class EarthquakeRepository {
    // Configure Retrofit to fetch data from the USGS API.
    // baseUrl: The starting part of the web address for the API.
    // addConverterFactory: Tells Retrofit how to convert JSON data into our Kotlin objects using Gson.
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://earthquake.usgs.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Create an implementation of our EarthquakeApiService interface.
    private val apiService = retrofit.create(EarthquakeApiService::class.java)

    /**
     * Fetches the list of recent earthquakes.
     * 
     * @return A list of EarthquakeFeature objects, or an empty list if the network request fails.
     */
    suspend fun getRecentEarthquakes(): List<EarthquakeFeature> {
        return try {
            // Call the API endpoint
            val response = apiService.getEarthquakes()
            // Return the list of features (earthquakes) from the response
            response.features
        } catch (e: Exception) {
            // If something goes wrong (no internet, server error), print the error and return an empty list
            // so the app doesn't crash.
            e.printStackTrace()
            emptyList()
        }
    }
}
