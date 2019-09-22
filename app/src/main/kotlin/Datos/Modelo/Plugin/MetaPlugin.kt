package Datos.Modelo.Plugin

import com.beust.klaxon.Json

class MetaPlugin(
        @Json(name = "nombre")
        val nombrePlugin: String = "Desconocido",
        val version: Double = Double.NaN)