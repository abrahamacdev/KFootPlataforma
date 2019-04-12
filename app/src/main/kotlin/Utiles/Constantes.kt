package Utiles

object Constantes {

    // Directorio por defecto en el que se buscarán plugins
    var DIRECTORY = Utils.getDirectorioDefecto()

    // Patrón que deben de seguir los comandos
    val REG_COMANDO = "(?:^[-]{1,2}[A-z0-9]+)(?:(?:[-][0-9A-z]+)*)\$"

    // Patrón que deben de seguir los valores de los comandos
    val REG_VAL_COMANDO = "^[A-z0-9]+\$"

    val REG_COMANDO_COMPLETO = "(?:^[-]{1,2}[A-z0-9]+)(?:(?:[-][0-9A-z]+)*)(?:\$|\\s[A-z0-9]+\$)"


}