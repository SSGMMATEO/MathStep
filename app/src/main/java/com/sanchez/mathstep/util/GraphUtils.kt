package com.sanchez.mathstep.util

import java.net.URLEncoder
import kotlin.math.abs

/**
 * GraphUtils — construye la URL de QuickChart.io (API gratuita, sin key,
 * open source: https://quickchart.io) para graficar una recta
 * y = slope·x + intercept, resaltando el punto donde cruza y = rightSide
 * (la solución de la ecuación).
 */
object GraphUtils {

    fun buildLinearChartUrl(slope: Double, intercept: Double, rightSide: Double): String {
        val solutionX = if (slope != 0.0) (rightSide - intercept) / slope else 0.0
        val range = (abs(solutionX) + 5).coerceAtLeast(5.0)
        val minX = solutionX - range
        val maxX = solutionX + range
        val step = (maxX - minX) / 10.0

        val xValues = (0..10).map { minX + step * it }
        val yValues = xValues.map { slope * it + intercept }

        val labels = xValues.joinToString(",") { "\"%.1f\"".format(it) }
        val data = yValues.joinToString(",") { "%.2f".format(it) }

        val config = """
            {
              type: 'line',
              data: {
                labels: [$labels],
                datasets: [{
                  label: 'y = ${formatCoef(slope)}x + ${formatCoef(intercept)}',
                  data: [$data],
                  borderColor: '#3F51B5',
                  fill: false
                }]
              }
            }
        """.trimIndent()

        val encoded = URLEncoder.encode(config, "UTF-8")
        return "https://quickchart.io/chart?width=500&height=300&c=$encoded"
    }

    private fun formatCoef(v: Double): String =
        if (v % 1.0 == 0.0) v.toLong().toString() else "%.2f".format(v)
}