package Utiles

import Modelo.PropiedadesService
import com.natpryce.konfig.Key
import com.natpryce.konfig.stringType
import java.io.File


object Constantes {

    // -- Nombres de las keys del archivo de configuración --
    val RUTA_PLUGINS_KEY = "RutaPlugins"
    val PRIMERA_VEZ_KEY = "YaEjecutado"
    // -------------------------------------------------------

    // -- DEBUG --
    enum class DEBUG(val value: Int) {
        DEBUG_TEST(3),             // Nos permite seguir el flujo del programa al realizar tests
        DEBUG_AVANZADO(2),         // Imprimirá mucha más información
        DEBUG_SIMPLE(1),           // Imprimirá la información más básica
        DEBUG_NONE(0),             // No queremos debug
        DEBUG_LEVEL(DEBUG_TEST.value)    // Debug que queremos para la ejecución actual
    }
    // -----------------------------------------------

    // Nombre por defecto del directorio en el que se buscarán plugins
    val NOMBRE_DIRECTORIO_PLUGINS = "/KScrapPlugins"

    // Directorio por defecto en el que se buscarán plugins
    var DIRECTORIO_PLUGINS = PropiedadesService.getPropiedades().getOrNull(Key(RUTA_PLUGINS_KEY, stringType))

    // Directorio personal del usuario
    val DIRECTORIO_PERSONAL = System.getProperty("user.home")

    // Directorio "Documentos" del usuario
    var DIRECTORIO_DOCUMENTOS = DIRECTORIO_PERSONAL + "/" + File(DIRECTORIO_PERSONAL).list().filter { it.matches(Regex("^[Dd]ocument[A-z]+\$")) }.get(0)

    // Patrón que deben de seguir los comandos
    val REG_COMANDO = "(?:^[-]{1,2}[A-z0-9]+)(?:(?:[-][0-9A-z]+)*)\$"

    // Patrón que deben de seguir los valores de los comandos
    val REG_VAL_COMANDO = "^[\\w/]+\$"

    // Patrón completo que comprueba si hay un comando y su respectivo argumento (opcional)
    //val REG_COMANDO_COMPLETO = "(?:^[-]{1,2}[A-z0-9]+)(?:(?:[-][0-9A-z]+)*)(?:\$|\\s[A-z0-9]+\$)"

}