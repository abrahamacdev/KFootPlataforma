package Utiles

import Modelo.Preferencias

object Constantes {

    // -- Nombres de las keys de las preferencias --
    val RUTA_PLUGINS_KEY = "RutaPlugins"
    val PRIMERA_VEZ_KEY = "YaEjecutado"
    // -------------------------------------------------------

    // Nombre por defecto del directorio en el que se buscarán plugins
    val NOMBRE_DIRECTORIO_PLUGINS_DEFECTO = "/KScrapPlugins"

    // Ancho y alto mínimo permitido
    val ANCHO_MINIMO = 1200.0
    val ALTO_MINIMO = 900.0

    // Patrón que deben de seguir los comandos
    //val REG_COMANDO = "(?:^[-]{1,2}[A-z0-9]+)(?:(?:[-][0-9A-z]+)*)\$"

    // Patrón que deben de seguir los valores de los comandos
    //val REG_VAL_COMANDO = "^[\\w/]+\$"

    // Patrón completo que comprueba si hay un comando y su respectivo argumento (opcional)
    //val REG_COMANDO_COMPLETO = "(?:^[-]{1,2}[A-z0-9]+)(?:(?:[-][0-9A-z]+)*)(?:\$|\\s[A-z0-9]+\$)"
}