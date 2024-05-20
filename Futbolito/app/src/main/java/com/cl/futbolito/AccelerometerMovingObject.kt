package com.cl.futbolito

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import kotlin.reflect.KFunction1

@Composable
fun AccelerometerMovingObject(onGoal: KFunction1<Int, Unit>) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    var position by remember { mutableStateOf(Offset(0f, 0f)) }

    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }
    val backgroundImage: Painter = painterResource(id = R.drawable.background_image)

    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    // Calcula el cambio de posición basado en los datos del acelerómetro
                    val deltaX = it.values[0] * -2f
                    val deltaY = it.values[1] * 4f

                    // Acumula los cambios a la posición actual del objeto
                    position = position.copy(
                        x = (position.x + deltaX).coerceIn(0f, canvasSize.width.toFloat()),
                        y = (position.y + deltaY).coerceIn(0f, canvasSize.height.toFloat())
                    )
                    Log.d("Sensor", "onSensorChanged: $position")
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Implementar lógica si es necesario
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentScale = ContentScale.FillBounds // Ajusta la escala del contenido para llenar el tamaño del Box
        )
        Canvas(modifier = Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
            canvasSize = coordinates.size
            if (position == Offset(0f, 0f)) { // Esto asegura que solo se establezca una vez
                position = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
            }
        }) {

            val rectangleWidth = canvasSize.width / 5f
            val rectangleHeight = 50f
            val horizontalOffset = (canvasSize.width - rectangleWidth) / 2f

            // Define los rectángulos que representan las porterias
            val topRectangle = Rect(
                left = horizontalOffset,
                top = 0f,
                right = horizontalOffset + rectangleWidth,
                bottom = rectangleHeight
            )
            val bottomRectangle = Rect(
                left = horizontalOffset,
                top = canvasSize.height - rectangleHeight,
                right = horizontalOffset + rectangleWidth,
                bottom = canvasSize.height.toFloat()
            )

            // Dibuja el círculo
            drawCircle(
                color = Color.White,
                center = position,
                radius = 20f
            )

            // Detecta colisiones individuales
            val collisionTop = collisionBetweenCircleAndRect(position, 20f, topRectangle)
            val collisionBottom = collisionBetweenCircleAndRect(position, 20f, bottomRectangle)

            // Realiza acciones basadas en las colisiones detectadas
            if (collisionTop) {
                // Acción si hay colisión con el rectángulo superior
                // Actualiza el marcador para el equipo 1
                onGoal(1)
                // Restablece la posición del círculo
                position = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
            }
            if (collisionBottom) {
                // Acción si hay colisión con el rectángulo inferior
                // Actualiza el marcador para el equipo 0
                onGoal(0)
                // Restablece la posición del círculo
                position = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
            }



        }
    }
}

// Función para detectar colisión entre un círculo y un rectángulo
fun collisionBetweenCircleAndRect(circleCenter: Offset, circleRadius: Float, rect: Rect): Boolean {
    // Considera el círculo como un rectángulo para simplificar la detección de colisiones
    val circleRect = Rect(
        left = circleCenter.x - circleRadius,
        top = circleCenter.y - circleRadius,
        right = circleCenter.x + circleRadius,
        bottom = circleCenter.y + circleRadius
    )
    return circleRect.overlaps(rect)
}
