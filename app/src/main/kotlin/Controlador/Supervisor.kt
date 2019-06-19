package Controlador

import Modelo.Plugin

/**
 * El [Supervisor] se encarga de gestionar toddo lo relacionado con
 * la ejecucion de los plugins y de la correcta ejecucion de estos
 */
class Supervisor {

    // Lista con los plugins a ejecutar
    private val plugins: ArrayList<Plugin> = ArrayList()

    companion object {

        // Instancia del [Supervisor]
        val instancia: Supervisor = Supervisor()
    }

    private constructor()


    /**
     * Añadimos el plugin pasado por parámetro a la
     * lista de plugins que se ejecutarán
     *
     * @param plugin: Plugin a añadir a la lista
     *
     * @return Boolean: Si ha podido añadirse el plugin a la lista
     */
    fun anadirPlugin(plugin: Plugin): Boolean{

        // Comprobamos si hay otro plugin con el mismo
        val existeCopia = plugins.firstOrNull(){

            // Comprobamos que los objetos no sean iguales
            val sonCopia = plugin.equals(it)

            // Comprobamos que la ruta de los plugins sea diferente (puede
            // que sean objetos diferentes pero se refieren al mismo archivo jar)
            val mismoJar = plugin.jar.canonicalPath.equals(it.jar.canonicalPath)

            sonCopia || mismoJar
        }

        // Si no hay ningun valor significa que podemos añadir el plugin
        if (existeCopia == null){
            // Añadimos el plugin a la lista
            plugins.add(plugin)
            return true
        }
        return false
    }

    /**
     * Añadimos la lista de plugins pasados
     * por parámetros lista de plugins que se ejecutarán
     *
     * @param listaPlugins: LIsta de plugins que se añadirán a la lista
     *
     * @return List<Plugin>: Lista con los plugins que no pueden añadirse a la lista
     */
    fun anadirListaPlugin(listaPlugin: List<Plugin>): List<Plugin>{

        // Lista con los plugins no validos
        val noValidos = ArrayList<Plugin>()


        // Recorremos cada uno de los plugins que quieren
        // añadirse a la lista
        listaPlugin.forEach {

                // Si no se ha podido añadir el plugin a la lista de los que se
                // ejecutara, lo añadimos a la lista de los no validos
                if (!anadirPlugin(it)){
                    noValidos.add(it)
                }
            }

        return noValidos
    }



    /**
     * Eliminamos de la lista el plugin que se
     * encuentra en la posición pasada por parámetro
     *
     * @param indice: Posición del plugin a eliminar
     *
     * @return Boolean: Si se ha eliminado
     */
    fun eliminarPlugin(indice: Int): Boolean{

        // TODO: Comprobar primero que el plugin no se este ejecutando

        // Comprobamos que el índice solicitado sea válido
        if (indice >= 0 && indice < plugins.size){
            plugins.removeAt(indice)
            return true
        }

        return false
    }

    /**
     * Eliminamos de la lista al plugin
     * que tenga el mismo identificador
     * que el pasado por parámetro
     *
     * @param plugin: Plugin que se buscara en el array
     */
    fun eliminarPlugin(plugin: Plugin): Boolean{

        // TODO: Comprobar primero que el plugin no se este ejecutando

        // Buscamos la posición del plugin dentro de la lista
        val posicion = plugins.indexOfFirst {
            it.equals(plugin)
        }

        if (posicion >= 0){
            plugins.removeAt(posicion)
            return true
        }

        return false
    }



    /**
     * Esperamos a que todos los plugins hallan terminado
     * de ejecutarse
     */
    suspend fun esperarFinalizacionPlugins(){

        // Esperamos a que se terminen de ejecutar los plugins
        plugins.forEach {
            it.job.join()
        }

        // Acciones post-ejecucion plugins
        ejecucionFinalizada()
    }

    /**
     * Esta funcion se ejecutara cada
     * vez que todos los plugins hallan finalizado
     */
    fun ejecucionFinalizada(){
        // TODO: Eliminar todos los plugins que se hallan ejecutado de la lista
    }
}
