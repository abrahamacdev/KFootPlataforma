package Vista.Ajustes

import Vista.View

interface IAjustesView {

    /**
     * Obtenemos todos los valores de los inputs
     * del layout y los relacionamos con el id
     * de su nodo
     *
     * @return HashMap<String,Any>: Map con los ids de los inputs y sus respectivos valores
     */
    fun obtenerValoresInputs(): HashMap<String,Any>

    /**
     * Establecemos los valores actuales a cada input
     * del layout
     */
    fun establecerLabelsInputs()

    /**
     * Añadimos los validadores a todos los inputs
     * del fragmento
     */
    fun anadirValidadoresInputs()

    /**
     * Bloqueamos todos los inputs para impedir
     * la modificación de los valores de los ajustes
     */
    fun bloquearInputs()
}