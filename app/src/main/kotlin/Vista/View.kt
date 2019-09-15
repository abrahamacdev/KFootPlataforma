package Vista

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.layout.Pane
import AbstractInicializable
import KFoot.DEBUG
import KFoot.IMPORTANCIA
import KFoot.Logger
import kotlinx.coroutines.runBlocking

abstract class View: AbstractInicializable() {

    companion object {
        private var fragmentoPrincipal: Pane? = null
        private var fragmentoAlertas: Pane? = null

        /**
         * Establecemos los fragmentos que se usarán a lo largo de la aplicación
         * para mostrar el contenido de cada opción del menú y las alertas generales
         *
         * @param fragPrincipal: Fragmento en el que se incluirán las distintas opciones del menú
         * @param fragAlertas: Fragmento en el que se incluirán las alertas generales de la aplicación
         */
        fun setearFragmentos(fragPrincipal: Pane, fragAlertas: Pane){
            if(fragmentoPrincipal == null){
                fragmentoPrincipal = fragPrincipal
            }

            if (fragmentoAlertas == null){
                fragmentoAlertas = fragAlertas
            }
        }
    }

    /**
     * Actualizamos el layout que contendrá el fragmento
     * principal
     *
     * @param nuevoNodo: Nuevo nodo que sustituirá al anterior
     *
     */
    fun renovarFragmentoPrincipal(nuevoNodo: Node){
        Platform.runLater {
            fragmentoPrincipal!!.children.clear()
            fragmentoPrincipal!!.children.add(nuevoNodo)
        }
    }

    /**
     * Dejamos el contenedor del fragmento principal
     * sin ningún layout
     */
    fun limpiarFragmentoPrincipal(){
        Platform.runLater {
            fragmentoPrincipal!!.children.clear()
        }
    }



    /**
     * Reemplazamos el contenido del fragmento alertas con
     * el nodo recibido por parámetros
     *
     * @param nuevoNodo: Nuevo nodo que colocaremos en el fragmento de alertas
     */
    fun mostrarFragmentoAlertas(nuevoNodo: Node){

        Platform.runLater {
            fragmentoAlertas!!.children.clear()
            fragmentoAlertas!!.children.add(nuevoNodo)
            fragmentoAlertas!!.isFocusTraversable = true
            fragmentoAlertas!!.isVisible = true
        }
    }

    /**
     * Reemplazamos el contenido del fragmento alertas con
     * el nodo recibido por parámetros
     *
     * @param nuevoNodo: Nuevo nodo que colocaremos en el fragmento de alertas
     * @param block: Código a ejecutar justo despues de mostrar la alerta
     */
    fun mostrarFragmentoAlertas(nuevoNodo: Node, block: () -> Unit){

        // Comprobamos que no haya una alerta que se esté mostrando con el
        // nodo recibido por parámetros
        if (!seEstaMostrandoAlertaCon(nuevoNodo)){
            Platform.runLater {
                fragmentoAlertas!!.children.clear()
                fragmentoAlertas!!.children.add(nuevoNodo)
                fragmentoAlertas!!.isFocusTraversable = true
                fragmentoAlertas!!.isVisible = true
                block.invoke()
            }
        }
    }

    /**
     * Comprobamos si hay una alerta mostrándose con el
     * [nodo] recibido
     *
     * @param nodo: Nodo a comprobar en la alerta
     *
     * @return Boolean: Si se está mostrando una alerta que contenga el [nodo]
     */
    private fun seEstaMostrandoAlertaCon(nodo: Node): Boolean{
        return fragmentoAlertas!!.children.firstOrNull { it == nodo } != null && fragmentoAlertas!!.isVisible
    }

    /**
     * Ocultamos el fragmento de alertas para poder interactuar
     * con el fragmento principal
     */
    fun ocultarFragmentoAlertas(){
        if (fragmentoAlertas != null){
            fragmentoAlertas!!.isFocusTraversable = false
            fragmentoAlertas!!.isVisible = false
        }
    }



    fun getFragmentoPrincipal(): Pane {
        return fragmentoPrincipal!!
    }

    fun getFragmentoAlertas(): Pane? {
        return fragmentoAlertas!!
    }
}