package net.annedawson.seismiccanada.data.repository

import net.annedawson.seismiccanada.data.model.EarthquakeFeature
import net.annedawson.seismiccanada.data.network.EarthquakeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EarthquakeRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://earthquake.usgs.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(EarthquakeApiService::class.java)

    suspend fun getRecentEarthquakes(): List<EarthquakeFeature> {
        return try {
            val response = apiService.getEarthquakes()
            response.features
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
