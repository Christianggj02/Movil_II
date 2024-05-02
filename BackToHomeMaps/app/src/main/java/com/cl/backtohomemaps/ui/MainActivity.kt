package com.cl.backtohomemaps.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import dagger.hilt.android.AndroidEntryPoint
import com.cl.backtohomemaps.ui.theme.MapsRouteDrawExampleTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.LocationSource
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationSource = MyLocationSource()

    companion object{
        const val MY_PERMISSIONS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(permissions, MY_PERMISSIONS)
        }

        setContent {
            MapsRouteDrawExampleTheme {
                val viewModel by viewModels<MainViewModel>()
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val context = LocalContext.current
                    val uiState by viewModel.mainUiState.collectAsState()
                    var latitude = 20.1410256
                    var longitude = -101.1500227

                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            Log.d("Ubicacion","${latitude},$longitude")
                            locationSource.onLocationChanged(location)
                            Log.d("ORIGEN","${latitude},$longitude")
                            viewModel.onEvent(
                                MainScreenEvent.SetCoordinatesOrigin(
                                    coordinates = LatLng(latitude, longitude)
                                )
                            )
                        }
                    }

                    uiState.uiMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.onEvent(MainScreenEvent.UiMessageDisplayed)
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        val actualLocation = LatLng(latitude,longitude)

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(actualLocation, 15f)
                        }
                        val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            locationSource = locationSource,
                            properties = mapProperties
                        ) {
                            when (uiState.currentStep) {
                                CurrentStep.SetDestinationCoordinates -> {
                                    Marker(state = MarkerState(position = cameraPositionState.position.target))
                                }
                                else -> {}
                            }
//
//                            uiState.originLatLng?.let {
//                                Marker(
//                                    title = "Origin",
//                                    state = MarkerState(position = it)
//                                )
//                            }

                            uiState.destinationLatLng?.let {
                                Marker(
                                    title = "Destination",
                                    state = MarkerState(position = it)
                                )
                            }

                            if (uiState.routeCoordinates.isNotEmpty()) {
                                uiState.routeCoordinates.forEach {
                                    Polyline(points = it, color = Color.Red)
                                }
                            }

                        }

                        Button(modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(50.dp),
                            onClick = {
                                when (uiState.currentStep) {
                                    CurrentStep.SetDestinationCoordinates -> {
                                        viewModel.onEvent(
                                            MainScreenEvent.SetCoordinates(
                                                coordinates = cameraPositionState.position.target
                                            )
                                        )
                                    }
                                    CurrentStep.DrawPath -> viewModel.onEvent(MainScreenEvent.DrawRoute)
                                    null -> viewModel.onEvent(MainScreenEvent.ClearMap)
                                    CurrentStep.SetOriginCoordinates -> {}
                                }

                            }) {
                            Text(
                                text = when (uiState.currentStep) {
                                    CurrentStep.SetOriginCoordinates -> "Establecer origen"
                                    CurrentStep.SetDestinationCoordinates -> "Establecer Destino"
                                    CurrentStep.DrawPath -> "Mostrar Ruta"
                                    null -> "Quitar ruta"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de ubicacion concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permiso de ubicacion denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

private class MyLocationSource : LocationSource {

    private var listener: LocationSource.OnLocationChangedListener? = null

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        this.listener = listener
    }

    override fun deactivate() {
        listener = null
    }

    fun onLocationChanged(location: Location) {
        listener?.onLocationChanged(location)
    }
}
