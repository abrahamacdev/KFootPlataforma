package Controlador.UI.Main

import Controlador.UI.IController
import Vista.Ajustes.AjustesView
import Vista.IView
import Vista.Main.MainView
import Vista.Plugins.PluginView
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent


class MainController(private val mainView: MainView): IMainController{

    // Ruta de las imagenes disponibles para la sección "Cuenta" del menú
    private val  rutasImagenesMenuCuenta: Array<String> = arrayOf(javaClass.getResource("../../../imagenes/account1.png").toString(),javaClass.getResource("../../../imagenes/account2.png").toString())

    // Vista que esta ocupando actualmente el #fragmentoPrincipal
    private var vistaFragmento: IView? = null

    // Listener asociado a los botones del menú
    private var menuClickListener: EventHandler<MouseEvent>? = null



    override fun preCargar() {}

    override fun obtenerRutaImagenCuenta(): String {
        val posIcono = Math.round(Math.random()).toInt()
        return rutasImagenesMenuCuenta.get(posIcono)
    }

    /**
     * Cambiamos el contenido del fragmento por uno nuevo
     *
     * @param nuevaVistaFrag: Nuevo [IController] que modificará el layout del fragmento
     */
    fun cambiarLayoutFragmento(nuevaVistaFrag: IView){

        // Comprobamos si el controlador del fragmento que hay actualmente establecido
        // es diferente al nuevo que estableceremos
        if (esDiferenteVistaFrag(nuevaVistaFrag)){

            // Comprobamos si hay alguna vista ocupando el fragmento para cancelarla
            if (vistaFragmento != null){
                vistaFragmento!!.cancelar()
            }

            // TODO: Hacer que cada vista se elimine del fragmento cuando llamemos a su método "cancelar"
            // Si hay hijos en la vista los eliminamos
            if (mainView.getFragmentoPrincipal().children.size > 0){
                mainView.limpiarFragmento()
            }

            // Iniciamos el nuevo controlador del fragmento
            vistaFragmento = nuevaVistaFrag
            vistaFragmento!!.iniciar(mainView.getFragmentoPrincipal())
        }
    }

    /**
     * Comprobamos si la nueva [vista] es la misma
     * que la [vistaFragmento]
     */
    private fun esDiferenteVistaFrag(vista: IView): Boolean{
        return vista != this.vistaFragmento
    }

    override fun getMenuClickListener(): EventHandler<MouseEvent> {

        if (menuClickListener == null){
            menuClickListener = object : javafx.event.EventHandler<MouseEvent> {
                override fun handle(event: MouseEvent?) {
                    val botonPulsado = (event!!.source as Button)
                    val noEstaPulsado = botonPulsado.styleClass.contains("boton-menu-normal")

                    when {

                        // Si no estaba pulsado le cambiamos el color de fondo
                        noEstaPulsado -> {

                            // A los demás botones les ponemos el color de fondo normal
                            mainView.botonPulsado(botonPulsado)

                            // Cambiamos el layout del fragmento principal
                            var tempViewFrag: IView? = null
                            when {

                                // Cargamos el fragmento de plugins
                                botonPulsado.id.equals("botonPlugins") -> {

                                    // Creamos una instancia del nuevo controlador del fragmento
                                    tempViewFrag = PluginView()
                                }

                                // Cargamos el fragmento de los ajustes de la aplicación
                                botonPulsado.id.equals("botonAjustes") -> {

                                    // Creamos una instancia del nuevo controlador del fragmento
                                    tempViewFrag = AjustesView()
                                }

                                // Cargamos el fragmento de plugins
                                botonPulsado.id.equals("botonCuenta") -> {

                                    // Creamos una instancia del nuevo controlador del fragmento
                                    //tempControlFrag = CuentaController()
                                }
                            }
                            if (tempViewFrag != null){
                                cambiarLayoutFragmento(tempViewFrag)
                            }
                        }
                    }
                }
            }
        }

        return menuClickListener!!
    }
}