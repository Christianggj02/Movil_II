package com.cl.backtohomemaps.data.repository

import com.google.android.gms.maps.model.LatLng
import com.cl.backtohomemaps.model.Route
import com.cl.backtohomemaps.utils.Resource

interface MapsRepository {

    suspend fun getDirections(
        origin: LatLng,
        destination: LatLng
    ): Resource<Route>

}