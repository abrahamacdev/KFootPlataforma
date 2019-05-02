package Controlador

import Controlador.Excepciones.ComandoException
import Modelo.PropiedadesService
import Utiles.Constantes
import Utiles.Utils
import com.andreapivetta.kolor.Color
import com.natpryce.konfig.Key
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.stringType
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import java.io.File
import java.lang.Exception

object Setup {

    /**
     * Realizamos las comprobaciones iniciales del programa. Esta función se ejecutará
     * cada vez que lo iniciemos
     *
     * @param Array<String> args: Argumentos pasados por la línea de comandos
     */
    fun realizarComprobaciones(args: Array<String>){

        // Creamos el documentos con las propiedades del programa
        // si no existe, sino lo cargaremos en memoria
        PropiedadesService.getPropiedades()

        // Aplicamos los argumentos pasados por parámetros
        if (args.size > 0){
            // Lista de comando con sus argumentos
            val argumentos = parsearComandos(args)

            // Observable con la lista de comandos
            val observable = argumentos.toObservable()
            observable.subscribe(object : Observer<String> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: String) {
                    aplicarCambios(t,this)
                }

                override fun onError(e: Throwable) {
                    throw e
                }
            })
        }

        // Comprobamos si se está ejecutando por primera vez el programa
        if (esPrimeraVez()){

            // Comprobamos si no hay ninguna ruta del directorio de plugins establecida
            if (PropiedadesService.getPropiedades().getOrNull(Key(Constantes.RUTA_PLUGINS_KEY, stringType)) == null && Constantes.DIRECTORIO_PLUGINS == null){
                crearDirectorioPorDefecto()
            }

            // Primera ejecución del programa realizada
            PropiedadesService.add(Constantes.PRIMERA_VEZ_KEY, true)
        }


        // Comprobamos la integridad del directorio en el que se encuentran
        // los plugins
        var f: File? = File(Constantes.DIRECTORIO_PLUGINS)

        when {

            // Se ha eliminado la ruta del directorio en el que
            // se guardan los plugins del archivo KScrap.properties
            f == null -> {
                // Ruta actual de los plugins no válida
                Utils.debug(Constantes.DEBUG.DEBUG_SIMPLE,"No se ha encontrado la ruta por defecto del directorio con los plugins. Se establecerá " +
                        "el predeterminado en \'${Constantes.DIRECTORIO_DOCUMENTOS + Constantes.DIRECTORIO_PLUGINS}\'",Color.RED)

                // Estableceremos la ruta por defecto '/Documentos/KScrapPlugins
                crearDirectorioPorDefecto()
            }

            // La ruta del directorio de los plugins no es válida
            !f.isDirectory -> {

                // Ruta actual de los plugins no válida
                Utils.debug(Constantes.DEBUG.DEBUG_SIMPLE,"El actual directorio de los plugins no es válido, se establecerá " +
                        "el predeterminado en \'${Constantes.DIRECTORIO_DOCUMENTOS + Constantes.NOMBRE_DIRECTORIO_PLUGINS}\'",Color.RED)

                // Estableceremos la ruta por defecto '/Documentos/KScrapPlugins
                crearDirectorioPorDefecto()
            }

            // No tenemos acceso al directorio
            !f.canRead() || !f.canExecute() || !f.canWrite() -> {
                // Ruta actual de los plugins no válida
                Utils.debug(Constantes.DEBUG.DEBUG_SIMPLE,"No se puede acceder al actual directorio de los plugins \'${f.absolutePath}, " +
                        "comprueba los permisos antes de continuar",Color.RED)// Ruta actual de los plugins no válida

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
        val propiedades = PropiedadesService.getPropiedades()
        val primeraVez = Key("YaEjecutado", booleanType)

        // Es la primera vez que lo ejecutamos
        return propiedades.getOrNull(primeraVez) == null
    }

    /**
     * Intentamos crear el directorio por defecto en el que se
     * guardarán los plugins
     *
     * @throws Exception
     */
    private fun crearDirectorioPorDefecto(){

        val directorioPlugins:File = File(Constantes.DIRECTORIO_DOCUMENTOS + Constantes.NOMBRE_DIRECTORIO_PLUGINS)
        val directorioDocumentos: File = File(Constantes.DIRECTORIO_DOCUMENTOS)

        // Comprobamos que podamos esccriibir en el directorio "Documentos"
        if (directorioDocumentos.canWrite()){

            // Creamos el directorio por defecto
            directorioPlugins.mkdir()

            // Si se ha creado el directorio, lo guardamos en el archivo de configuración
            PropiedadesService.add(Constantes.RUTA_PLUGINS_KEY, Constantes.DIRECTORIO_DOCUMENTOS + Constantes.NOMBRE_DIRECTORIO_PLUGINS)

            // Guardamos la ruta del directorio en memoria
            Constantes.DIRECTORIO_PLUGINS = Constantes.DIRECTORIO_DOCUMENTOS + Constantes.NOMBRE_DIRECTORIO_PLUGINS

        }
    }

    /**
     * Convertimos un array de supuestos comandos en una lista
     * de comandos con sus respectivos argumentos (si tienen)
     *
     * @param Array<String> args: Array con los datos pasados por terminal
     *
     * @return ArrayList<String> con los comandos y sus respectivos agurmentos
     */
    private fun parsearComandos(args: Array<String>): ArrayList<String>{

        val completos: ArrayList<String> = ArrayList()

        for (i in 0 until args.size){

            // Comprobamos si el elemento actual es un comando
            if (args[i].matches(Regex(Constantes.REG_COMANDO))){

                // Comprobamos que el siguiente elemento sea un argumento
                if (i + 1 < args.size){
                    if (args[i+1].matches(Regex(Constantes.REG_VAL_COMANDO))){
                        completos.add((args[i] + " " + args[i+1]).trim()) //Concatenamos el comando con su argumento
                        continue
                    }
                }

                // Comando sin argumento
                completos.add(args[i].trim())
            }
        }
        return completos
    }

    /**
     * Aplicamos los cambios a la ejecución del programa
     *
     * @param String args: Comando con su respectivo valor (si tiene)
     */
    private fun aplicarCambios(args: String, observer: Observer<String>){

        var comando: String
        var valor: String = ""

        // Es un comando con valor
        if (args.split(" ").size > 1){
            comando = args.split(Regex("\\s"))[0]
            valor = args.split(Regex("\\s"))[1]
        }

        // Solo es un comando
        else {
            comando = args
        }


        // Comprobamos que comando es el que se ha pasado
        when {

            // Establecemos el directorio en el que se encuentra la
            // carpeta con los plugins
            comando.equals("-d") -> {

                // No se ha especificado una ruta
                if (valor.length > 0 && !valor.isEmpty()){

                    val f: File = File(valor)

                    // Comprobamos que sea un directorio y exista
                    if (f.isDirectory && f.exists()){

                        // Creamos la key
                        val key = Key(Constantes.RUTA_PLUGINS_KEY, stringType)

                        // Actualizamos la ruta en la que buscaremos los plugins en el archivo de confiruación
                        PropiedadesService.modify(key,f.absolutePath, true)
                        Constantes.DIRECTORIO_PLUGINS = f.absolutePath

                        return
                    }
                }

                // El comando no cumple la sintaxis requerida o la ruta no es válida
                observer.onError(ComandoException("-d", "necesita como parámetro un directorio válido"))
                return
            }
        }

        // Por defecto enviaremos un error indicando que el comando no es válido
        observer.onError(ComandoException("El comando $comando no es válido. Revisa las opciones válidas"))
    }

}
