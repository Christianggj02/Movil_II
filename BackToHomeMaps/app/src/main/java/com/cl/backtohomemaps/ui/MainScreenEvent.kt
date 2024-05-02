package com.cl.backtohomemaps.ui

import com.google.android.gms.maps.model.LatLng

sealed interface MainScreenEvent {
    object UiMessageDisplayed : MainScreenEvent
    object DrawRoute : MainScreenEvent
    object ClearMap : MainScreenEvent
    data class SetCoordinates(val coordinates:LatLng) : MainScreenEvent
    data class SetCoordinatesOrigin(val coordinates:LatLng) : MainScreenEvent
}