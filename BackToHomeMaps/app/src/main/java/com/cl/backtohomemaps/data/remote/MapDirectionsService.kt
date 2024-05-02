package com.cl.backtohomemaps.data.remote

import com.cl.backtohomemaps.BuildConfig
import com.cl.backtohomemaps.data.remote.dto.DirectionsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapDirectionsService {

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") originLatLng: String,
        @Query("destination") destinationLatLang: String,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ): Response<DirectionsDto>

}