package Modelo

import KFoot.DEBUG
import KFoot.IMPORTANCIA
import KFoot.Logger
import KFoot.Constantes as KFootConstantes
import KFoot.Utils as KFootUtils
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import java.io.*

object Preferencias{

    private var propiedades: ConfigurationProperties? = null
    private val RUTA_ARCHIVO = javaClass.getResource("../preferencias/").toString()
    private val NOMBRE_ARCHIVO = "KScrap.properties"

    fun getPropiedades(): ConfigurationProperties{
        return obtenerPropiedades()
    }

    /**
     * Obtenemos el archivo que contiene propiedades del
     * programa. En caso de no existir, lo crearemos
     *
     * @return ConfigurationProperties propiedades del programa
     */
    private fun obtenerPropiedades(): ConfigurationProperties {

        // Aún no está cargado
        if (propiedades == null){

            val f = File(RUTA_ARCHIVO + NOMBRE_ARCHIVO)

            println(File(RUTA_ARCHIVO).setReadable(true))
            println(File(RUTA_ARCHIVO).canRead())
            println(File(RUTA_ARCHIVO).canWrite())

            // Comprobamos que el archivo exista y lo cargamos a la variable
            if (!f.exists() || !f.isFile){
                // Creamos el archivo
                FileWriter(f).close()
            }
            propiedades = ConfigurationProperties.fromFile(f)
        }

        return propiedades!!
    }

    /**
     * Guardamos una propiedad en el archivo de coonfiguración predeterminado
     *
     * @param Key<T> k: Clave con el tipo
     */
    fun <T> add(k: String, v: T){

        // Creamos el archivo
        val buffer: BufferedWriter = BufferedWriter(FileWriter(RUTA_ARCHIVO + NOMBRE_ARCHIVO, true))
        buffer.append("${k}=${v}\n")
        buffer.close()

        Logger.getLogger().debug(DEBUG.DEBUG_TEST,"Se ha añadido al archivo de configuración: $k=$v", IMPORTANCIA.BAJA)

    }

    /**
     * Modificamos el valor de una propiedad del archivo de configuración
     *
     * @param Key<T> k: Clave con su respectivo nombre y tipo de dato
     * @param T v: Nuevo valor a establecer
     */
    fun <T> modify(k: Key<T>, v: T, crearSiNoExiste: Boolean = false){

        // Cargamos el archivo de configuración
        if (propiedades == null){
            getPropiedades()
        }

        var modificado = false
        var nuevoDocumento = ""

        // Leemos el contenido del archivo de configuración en busca
        // de la "key" necesitada.
        val reader = BufferedReader(FileReader(RUTA_ARCHIVO + NOMBRE_ARCHIVO))
        reader.forEachLine {

            var linea = ""
            linea = it

            // Comprobamos si la clave de la actual línea
            // coincide con la necesitada
            val claveValor = it.split("=")
            if (claveValor.size > 1){
                if (claveValor[0].equals(k.name) && !modificado){
                    linea = k.name + "=" + v
                    modificado = true

                    Logger.getLogger().debug(DEBUG.DEBUG_SIMPLE,"Se ha modificado una propiedad del archivo de configuración. Key = ${k.name}", IMPORTANCIA.MEDIA)

                }
            }

            linea += "\n"
            nuevoDocumento += linea

        }
        reader.close()

        // Escribimos los nuevos ajustes en el documento
        if (!nuevoDocumento.isEmpty()){

            val writer = BufferedWriter(FileWriter(RUTA_ARCHIVO + NOMBRE_ARCHIVO))
            writer.write(nuevoDocumento)
            writer.close()
        }

        // Añadimos el registro ya que no se encontraba en
        // el archivo
        if (!modificado && crearSiNoExiste){
            add(k.name, v)
        }

    }

}