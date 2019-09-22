package Vista

import AbstractInicializable
import Controlador.Excepciones.CreacionEscenarioException
import Datos.Modelo.ParametrosEscenario
import KFoot.DEBUG
import KFoot.Logger
import Vista.Dialogos.Dialogo
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.events.JFXDialogEvent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
import javafx.application.Platform
import javafx.event.EventType
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.awt.Dialog
import java.lang.Exception
import java.util.*

open class View: AbstractInicializable() {

    companion object {

        private var layoutBase: Parent? = null
        private var layoutPrincipal: Pane? = null
        private var layoutDialogosSecundario: Pane? = null
        private var layoutDialogosPrimario: Pane? = null
        private var escenarioPrincipal: Stage? = null

        private var dialogoPrimario: Dialogo? = null
        private var dialogoSecundario: Dialogo? = null

        fun getLayoutPrincipal(): Pane? {
            return layoutPrincipal
        }

        fun getEscenarioPrincipal(): Stage?{
            return escenarioPrincipal
        }

        /**
         * Seteamos los diferentes layouts que se utilizarán como base a lo largo
         * de la aplicación
         *
         * @param layoutBase: Layout base que contiene los 3 principales (layoutPrincipal, layoutDialogosSecundario y
         * layoutDialogosPrimario)
         */
        fun setearLayoutBase(layoutBase: Parent) {

            if (Companion.layoutBase == null){

                // Guardamos el layoutBase
                Companion.layoutBase = layoutBase

                if (layoutPrincipal == null) {
                    layoutPrincipal = layoutBase.lookup("#layoutPrincipal") as Pane
                }

                if (layoutDialogosPrimario == null){
                    layoutDialogosPrimario = layoutBase.lookup("#layoutDialogosPrincipal") as Pane
                    dialogoPrimario = Dialogo(layoutDialogosPrimario!!)
                }

                if (layoutDialogosSecundario == null){
                    layoutDialogosSecundario = layoutBase.lookup("#layoutDialogosSecundario") as Pane
                    dialogoSecundario = Dialogo(layoutDialogosSecundario!!)
                }
            }
        }

        /**
         * Creamos la escena principal con el escenario que recibamos como parámetro
         *
         * @param escenario: Escenario que utilizaremos a lo largo de la aplicación
         */
        fun crearEscenaConEscenario(escenario: Stage, parametrosEscenario: ParametrosEscenario? = null){

            // Comprobamos si no hay ningún layoutBase
            if (layoutBase == null){
                if (Logger.getLogger().getDebugLevel() == DEBUG.DEBUG_TEST){
                    throw CreacionEscenarioException("No tenemos un layout base con el que crear el escenario")
                }
                else {
                    throw Exception()
                }
            }

            val escena: Scene = Scene(layoutBase)

            // Establecemos la escena al escenario, maximizamos la ventana y la mostramos
            escenario.scene = escena

            // Comprobamos que halla unos parámetros a usar para el escenario
            if (parametrosEscenario != null){

                when {

                    // Hay que agrandar el escenario
                    parametrosEscenario.pantallaMaximizada -> escenario.isMaximized = true
                }

                if (parametrosEscenario.mostrarEscenarioInmediatamente){
                    escenario.show()
                }
            }

            // Guardamos la dirección del escenario
            escenarioPrincipal = escenario
        }
    }

    fun mostrarDialogSecundario(dialog: JFXDialog){
        dialogoSecundario!!.mostrarDialogo(dialog)
    }

    fun mostrarDialogPrimario(dialog: JFXDialog){
        dialogoPrimario!!.mostrarDialogo(dialog)
    }
}