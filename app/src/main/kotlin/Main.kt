import Controlador.Office.Office
import Controlador.Setup.Setup
import kotlinx.coroutines.*

/**
 * Desde aquí lanzaremos el "Office" desde donde se cargaŕan
 * los diferente plugins para la obtención de los datos
 *
 * @author Abraham Álvarez
 * @since 1.0
 */
fun main(args: Array<String>) = runBlocking<Unit> {

    // Realizamos las comprobaciones iniciales del programa
    Setup.realizarComprobaciones(args)

    // Obtenemos el office que se encargara de comprobar los plugins
    // existentes y de lanzarlos
    val office = Office()

    // Si hay plugins validos, los cargaremos y ejecutaremos
    if(office.existenPlugins()){

        // Cargamos en memoria los plugins
        office.cargarPlugins()

        // Ejecutamos los plugins cargados
        val supervisor = office.ejecutarPlugins()
    }



}