package com.cl.backtohomemaps.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.cl.backtohomemaps.data.repository.MapsRepository
import com.cl.backtohomemaps.utils.Resource
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mapsRepository: MapsRepository
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState = _mainUiState.asStateFlow()

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.ClearMap -> {
                _mainUiState.update {
                    it.copy(
                        routeCoordinates = emptyList(),
                        originLatLng = null,
                        destinationLatLng = null,
                        currentStep = CurrentStep.SetOriginCoordinates
                    )
                }
            }
            MainScreenEvent.DrawRoute -> {
                getRoutePath()
            }
            MainScreenEvent.UiMessageDisplayed -> {
                _mainUiState.update {
                    it.copy(
                        uiMessage = null
                    )
                }
            }
            is MainScreenEvent.SetCoordinates -> {
                when (mainUiState.value.currentStep) {
                    CurrentStep.SetDestinationCoordinates -> {
                        Log.d("SetDestination","ENTRO")
                        _mainUiState.update {
                            it.copy(
                                destinationLatLng = event.coordinates,
                                currentStep = CurrentStep.DrawPath
                            )
                        }
                    }
                    else -> {}
                }
            }
            is MainScreenEvent.SetCoordinatesOrigin -> {
                when (mainUiState.value.currentStep) {
                    CurrentStep.SetOriginCoordinates -> {
                        Log.d("SetOrigin","ENTRO")
                        _mainUiState.update {
                            it.copy(
                                originLatLng = event.coordinates,
                                currentStep = CurrentStep.SetDestinationCoordinates
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getRoutePath() {

        if (mainUiState.value.originLatLng == null) {
            _mainUiState.update {
                it.copy(
                    uiMessage = "Establecer punto de origen"
                )
            }
            return
        }


        if (mainUiState.value.destinationLatLng == null) {
            _mainUiState.update {
                it.copy(
                    uiMessage = "Establecer punto de destino"
                )
            }
            return
        }

        viewModelScope.launch {

            val pathResult = mapsRepository.getDirections(
                origin = mainUiState.value.originLatLng!!,
                destination = mainUiState.value.destinationLatLng!!
            )

            when (pathResult) {
                is Resource.Error -> {
                    _mainUiState.update { it.copy(uiMessage = pathResult.message) }
                }
                is Resource.Success -> {
                    pathResult.data?.let { route ->
                        _mainUiState.update {
                            it.copy(
                                routeCoordinates = route.routePoints,
                                currentStep = null
                            )
                        }
                    }
                }
            }

        }
    }

}


data class MainUiState(
    val routeCoordinates: List<List<LatLng>> =  emptyList(),
    val originLatLng: LatLng? = null,
    val destinationLatLng: LatLng? = null,
    val uiMessage: String? = null,
    val currentStep: CurrentStep? = CurrentStep.SetOriginCoordinates
)

enum class CurrentStep {
    SetOriginCoordinates,
    SetDestinationCoordinates,
    DrawPath
}