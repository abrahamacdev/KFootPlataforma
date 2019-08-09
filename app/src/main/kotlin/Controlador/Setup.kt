package Controlador

import KFoot.DEBUG
import KFoot.IMPORTANCIA
import KFoot.Logger
import Modelo.Preferencias
import Utiles.Constantes
import KFoot.Constantes as KFootConstantes
import KFoot.Utils as KFootUtils
import java.io.File
import java.lang.Exception

object Setup {

    /**
     * Realizamos las comprobaciones iniciales del programa. Esta función se ejecutará
     * cada vez que lo iniciemos
     *
     * @param Array<String> args: Argumentos pasados por la línea de comandos
     */
    fun realizarComprobaciones(args: Array<String>? = null){

        // Comprobamos si se está ejecutando por primera vez el programa
        if (Setup.esPrimeraVez()){

            // Comprobamos si no hay ninguna ruta del directorio de plugins establecida
            if (Preferencias.obtenerOrNulo(Constantes.RUTA_PLUGINS_KEY) == null){
                Controlador.Setup.crearDirectorioPorDefecto()
            }

            // Primera ejecución del programa realizada
            Preferencias.anadir(Constantes.PRIMERA_VEZ_KEY, true)
        }

        // Comprobamos la integridad del directorio en el que se encuentran
        // los plugins
        var f: File? = File(Preferencias.obtener(Constantes.RUTA_PLUGINS_KEY).toString())

        when {

            // Se ha eliminado la ruta del directorio en el que
            // se guardan los plugins del archivo KScrap.properties
            f == null -> {
                // Ruta actual de los plugins no válida
                Logger.getLogger().debug(DEBUG.DEBUG_SIMPLE,"No se ha encontrado la ruta por defecto del directorio con los plugins. Se establecerá " +
                        "el predeterminado en \'${KFootUtils.obtenerDirDocumentos() + Constantes.NOMBRE_DIRECTORIO_PLUGINS_DEFECTO}\'",IMPORTANCIA.ALTA)

                // Estableceremos la ruta por defecto '/Documentos/KScrapPlugins
                Controlador.Setup.crearDirectorioPorDefecto()
            }

            // La ruta del directorio de los plugins no es válida
            !f.isDirectory -> {

                // Ruta actual de los plugins no válida
                Logger.getLogger().debug(DEBUG.DEBUG_SIMPLE,"El actual directorio de los plugins no es válido, se establecerá " +
                        "el predeterminado en \'${KFootUtils.obtenerDirDocumentos() + Constantes.NOMBRE_DIRECTORIO_PLUGINS_DEFECTO}\'",IMPORTANCIA.ALTA)

                // Estableceremos la ruta por defecto '/Documentos/KScrapPlugins
                Controlador.Setup.crearDirectorioPorDefecto()
            }

            // No tenemos acceso al directorio
            !f.canRead() || !f.canExecute() || !f.canWrite() -> {
                // Ruta actual de los plugins no válida
                Logger.getLogger().debug(DEBUG.DEBUG_SIMPLE,"No se puede acceder al actual directorio de los plugins \'${f.absolutePath}\', " +
                        "comprueba los permisos antes de continuar",IMPORTANCIA.ALTA)

                // Terminamos la ejecución
                System.exit(1)
            }
        }
    }

    /**
     * Comprobamos si es la primera vez que ejecutamos el programa.
     *
     * @return Boolean: Si ya lo  hemos ejecutado anteriormente
     */
    private fun esPrimeraVez(): Boolean{
        return Preferencias.obtenerOrNulo("YaEjecutado") == null
    }

    /**
     * Intentamos crear el directorio por defecto en el que se
     * guardarán los plugins
     *
     * @throws Exception
     */
    private fun crearDirectorioPorDefecto(){

        // Directorio en el que se almacenaran los pluginss
        val directorioPlugins:File = File(KFootUtils.obtenerDirDocumentos() + Constantes.NOMBRE_DIRECTORIO_PLUGINS_DEFECTO)

        // Directorio en el que crearemos la carpeta por defecto
        val directorioDocumentos: File = File(KFootUtils.obtenerDirDocumentos())

        // Comprobamos que podamos esccriibir en el directorio "Documentos"
        if (directorioDocumentos.canWrite()){

            // Creamos el directorio por defecto
            directorioPlugins.mkdir()

            // Si se ha creado el directorio, lo guardamos en el archivo de configuración
            Preferencias.anadir(Constantes.RUTA_PLUGINS_KEY, KFootUtils.obtenerDirDocumentos() + Constantes.NOMBRE_DIRECTORIO_PLUGINS_DEFECTO)
        }
    }
}
