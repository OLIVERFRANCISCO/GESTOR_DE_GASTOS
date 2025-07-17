package com.oliver.gestor_de_gastos.view

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.math.PI

fun Double.toRad() = this * PI / 180

fun generarColorAleatorio(): Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}

@Composable
fun ChartPay(porcentajes: FloatArray, labels: List<String>) {
    val initialAngle = -90f
    var currentAngle = initialAngle
    val total = porcentajes.sum()
    val colores = List(porcentajes.size) { generarColorAleatorio() }

    Box(modifier = Modifier.padding(4.dp)) {
        Canvas(modifier = Modifier.size(400.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            porcentajes.forEachIndexed { index, element ->
                val sweepAngle = (element / total) * 360f
                val middleAngle = currentAngle + (sweepAngle / 2)
                val color = colores[index]
                var color2 = colores[index].toArgb()
                drawArc(
                    color = color,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    style = Fill
                )
                val textX = (centerX + (size.width / 3) * cos(middleAngle.toDouble().toRad())).toFloat()
                val textY = (centerY + (size.height / 3) * sin(middleAngle.toDouble().toRad())).toFloat()
                val percentageText = "${(element / total * 100).toInt()}%"
                val labelText = if (labels.size > index) labels[index] else ""
                val textPaint = Paint().apply {
                    color2 = color2 and 0x00FFFFFF // Make color opaque
                    textSize = 40f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                }
                drawContext.canvas.nativeCanvas.drawText(
                    percentageText,
                    textX,
                    textY,
                    textPaint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    textX,
                    textY + 45f,
                    textPaint
                )
                currentAngle += sweepAngle
            }
        }
    }
}