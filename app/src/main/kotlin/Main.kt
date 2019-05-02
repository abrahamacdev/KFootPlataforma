import Controlador.Office
import Controlador.Setup

/**
 * Desde aquí lanzaremos el "Office" u "Oficina" desde la que se cargaŕan
 * los diferente plugins para la obtención de los inmuebles de sus respectivas
 * páginas web.
 *
 * @author Abraham Álvarez
 * @since 1.0
 */
fun main(args: Array<String>){

    // Realizamos las comprobaciones iniciales del programa
    Setup.realizarComprobaciones(args)

    // Comprobamos los plugins existentes y los ejecutamos
    val office = Office.getInstancia()
    if(office.hayPluginsValidos()){

        office.cargarPlugins()
        office.ejecutarPlugins()
    }
}