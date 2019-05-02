package Controlador

class Supervisor {

    private val plugins: ArrayList<Plugin> = ArrayList()
    private var estaEjecutando: Boolean = false

    companion object {

        private var instancia: Supervisor? = null;

        @Synchronized
        fun getInstancia(): Supervisor{

            if (instancia == null){
                instancia = Supervisor()
            }

            return instancia!!
        }

    }

    private constructor()

    /**
     * Añadimos el plugin pasado por parámetro a la
     * lista de plugins que se ejecutarán
     *
     * @param plugin: Plugin a añadir a la lista
     */
    fun anadirPlugin(plugin: Plugin){

        // Evitamos añadir plugins cuando se están ejecutando
        if (!estaEjecutando){

            val yaAnadido = plugins.firstOrNull{ it.equals(plugin) }

            // El plugin no ha sido añadido aún
            if (yaAnadido == null){
                plugins.add(plugin)
            }
        }
    }

    /**
     * Añadimos la lista de plugins pasados
     * por parámetros lista de plugins que se ejecutarán
     *
     * @param listaPlugins: LIsta de plugins que se añadirán a la lista
     */
    fun anadirListaPlugin(listaPlugin: List<Plugin>){

        if (!estaEjecutando){
            listaPlugin.forEach {
                anadirPlugin(it)
            }
        }
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

        if (!estaEjecutando){
            if (indice >= 0 && indice < plugins.size){
                plugins.removeAt(indice)
                return true
            }
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

        if (!estaEjecutando){

            // Buscamos la posición del plugin dentro de la lista
            val posicion = plugins.indexOfFirst {
                it.equals(plugin)
            }

            if (posicion >= 0){
                plugins.removeAt(posicion)
                return true
            }
        }

        return false
    }

    /**
     *
     */
    fun lanzarPlugins(){

    }

}