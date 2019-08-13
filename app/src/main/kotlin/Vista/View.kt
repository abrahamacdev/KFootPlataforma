package Vista

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.layout.Pane
import AbstractInicializable

abstract class View: AbstractInicializable() {

    companion object {
        private var fragmentoEstablecido = false
        private lateinit var fragmentoPrincipal: Pane
    }


    /**
     * Será el método que llamemos para iniciar cada vista
     * de cada opción del menú
     *
     * @param fragmento: Fragmento sobre el que pondremos los layouts de cada opción del menú
     */
    open fun iniciar(fragmento: Pane){
        super.iniciar()
        if (!fragmentoEstablecido){
            fragmentoPrincipal = fragmento
            fragmentoEstablecido = true
        }
    }

    /**
     * Actualizamos el layout que contendrá el fragmento
     * principal
     *
     * @param nuevoNodo: Nuevo nodo que sustituirá al anterior
     *
     */
    fun renovarContenidoFragmento(nuevoNodo: Node){
        Platform.runLater {
            fragmentoPrincipal.children.clear()
            fragmentoPrincipal.children.add(nuevoNodo)
        }
    }

    /**
     * Dejamos el contenedor principal del fragmento
     * sin ningún layout
     */
    fun limpiarFragmento(){

        Platform.runLater {
            fragmentoPrincipal.children.clear()
        }
    }

    fun getFragmentoPrincipal(): Pane {
        return fragmentoPrincipal
    }
}