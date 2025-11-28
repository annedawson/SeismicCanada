package net.annedawson.seismiccanada.data.network

import net.annedawson.seismiccanada.data.model.EarthquakeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthquakeApiService {
    // Using USGS API as a proxy for example, filtered for a region including Western Canada
    // In a real app, you might use specific Canadian government APIs or other dedicated sources.
    // This endpoint fetches earthquakes.
    @GET("fdsnws/event/1/query")
    suspend fun getEarthquakes(
        @Query("format") format: String = "geojson",
        @Query("minlatitude") minLat: Double = 48.0, // Approx Southern border of Western Canada
        @Query("maxlatitude") maxLat: Double = 60.0, // Approx Northern border
        @Query("minlongitude") minLon: Double = -140.0, // Approx Western border
        @Query("maxlongitude") maxLon: Double = -110.0, // Approx Eastern border
        @Query("limit") limit: Int = 20,
        @Query("orderby") orderBy: String = "time"
    ): EarthquakeResponse
}
