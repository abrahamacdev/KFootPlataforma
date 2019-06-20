package Modelo

import KFoot.Constantes as KFootConstantes
import KFoot.Utils as KFootUtils
import com.andreapivetta.kolor.Color
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.Key
import java.io.*

object Preferencias{

    private var propiedades: ConfigurationProperties? = null
    private val RUTA_ARCHIVO = File("app/src/main/resources/KScrap.properties")

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
            // Comprobamos que el archivo exista y lo cargamos a la variable
            if (!RUTA_ARCHIVO.exists() || !RUTA_ARCHIVO.isFile){
                // Creamos el archivo
                FileWriter(RUTA_ARCHIVO).close()
            }
            propiedades = ConfigurationProperties.fromFile(RUTA_ARCHIVO)
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
        val buffer: BufferedWriter = BufferedWriter(FileWriter(RUTA_ARCHIVO, true))
        buffer.append("${k}=${v}\n")
        buffer.close()

        KFootUtils.debug(KFootConstantes.DEBUG.DEBUG_TEST,"Se ha añadido al archivo de configuración: $k=$v", Color.LIGHT_BLUE)

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
        val reader = BufferedReader(FileReader(RUTA_ARCHIVO))
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

                    KFootUtils.debug(KFootConstantes.DEBUG.DEBUG_SIMPLE,"Se ha modificado una propiedad del archivo de configuración. Key = ${k.name}", Color.BLUE)

                }
            }

            linea += "\n"
            nuevoDocumento += linea

        }
        reader.close()

        // Escribimos los nuevos ajustes en el documento
        if (!nuevoDocumento.isEmpty()){

            val writer = BufferedWriter(FileWriter(RUTA_ARCHIVO))
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