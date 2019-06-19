package Controlador.Office

interface IOffice {

    /**
     * Comprueba si existen plugins en el directorio de plugins
     */
    fun existenPlugins(): Boolean

    /**
     * Cargamos los plugins validos en memoria
     * para ejecutarlos
     */
    fun cargarPlugins()
}