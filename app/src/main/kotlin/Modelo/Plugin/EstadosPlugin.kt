package Modelo.Plugin

enum class EstadosPlugin {
    INACTIVO,       // El plugin no se esta ejcutando
    ACTIVO,         // El plugin esta ejecutandose
    COMPLETADO,     // Ha terminado de ejecutarse
    ERROR          // Ha ocurrido un error que imposibilita su ejecución
}