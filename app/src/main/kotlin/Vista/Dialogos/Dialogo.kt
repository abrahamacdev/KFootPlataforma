package Vista.Dialogos

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.events.JFXDialogEvent
import io.reactivex.processors.PublishProcessor
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.layout.Pane
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicInteger

class Dialogo: IDialogo.onDialogMostrado {

    private lateinit var layoutDialogos: Pane

    private val procesadorDialogos: PublishProcessor<JFXDialog> = PublishProcessor.create()

    private var subsProcesadorDialogos: Subscription? = null

    // Cantidad de dialogos a mostrar aún (están en el buffer del #procesadorDialogos)
    @Volatile
    private var dialogosAMostrar: AtomicInteger = AtomicInteger(0)

    // Cantidad de diálogos mostrados
    @Volatile
    private var dialogosMostrados: AtomicInteger = AtomicInteger(0)

    // Controla si se está mostrando algún diálogo ahora mismo
    @Volatile
    private var mostrando = false

    private val handlerCierreDialogos: EventHandler<JFXDialogEvent> = object : EventHandler<JFXDialogEvent> {
        override fun handle(p0: JFXDialogEvent?) {

            // El evento es CIERRE DEL DIÁLOGO
            if (p0!!.eventType == JFXDialogEvent.CLOSED){

                Platform.runLater {
                    // Eliminamos el contenido del layoutDialogos
                    layoutDialogos.children.clear()

                    // Avisamos de la finalizacion del diálogo
                    onDialogoMostrado()
                }
            }
        }
    }


    constructor(layoutDialogos: Pane) {
        this.layoutDialogos = layoutDialogos
        atenderMuestraDialogos()
    }


    private fun bloquearLlegadaDialogos(){
        synchronized(mostrando){
            mostrando = true
        }
    }

    private fun permitirLlegadaDialogos(){
        synchronized(mostrando){
            mostrando = false
        }
    }



    private fun atenderMuestraDialogos(){

        val subscriber: Subscriber<JFXDialog> = object : Subscriber<JFXDialog> {
            override fun onSubscribe(p0: Subscription?) {
                subsProcesadorDialogos = p0!!
                subsProcesadorDialogos!!.request(1)
            }

            override fun onNext(p0: JFXDialog) {

                val temp = synchronized(mostrando){
                    mostrando
                }

                // Comprobamos que no estamos mostrando ningún diálogo
                if (!temp){

                    // Bloqueamos la llegada de nuevos diálogos
                    bloquearLlegadaDialogos()

                    // Si el layoutDialogos no está visible, lo mostramos
                    if (!layoutDialogos.isVisible){

                        // Hacemos el layout del diálogo visible
                        layoutDialogos.isFocusTraversable = true
                        layoutDialogos.isVisible = true
                    }

                    // Mostramos el diálogo
                    Platform.runLater {
                        layoutDialogos.children.add(p0.dialogContainer)
                        p0.show()
                    }
                }
            }

            override fun onComplete() {}
            override fun onError(p0: Throwable?) {}
        }

        // Nos subscribimos al procesador de diálogos
        procesadorDialogos.subscribe(subscriber)
    }


    fun mostrarDialogo(dialogo: JFXDialog){

        // Añadimos el handler para cuando el diálogo se cierre
        dialogo.addEventHandler(JFXDialogEvent.CLOSED,handlerCierreDialogos)

        // Añadimos el diálogo al buffer del procesador de diálogos
        procesadorDialogos.onNext(dialogo)

        // Aumentamos en 1 la cuenta de diálogos a mostrar
        dialogosAMostrar.incrementAndGet()
    }

    override fun onDialogoMostrado() {

        // Añadimos 1 a la cantidad de dialogos mostrados
        dialogosMostrados.incrementAndGet()

        // Comprobamos si ya no quedan más dialogos por procesar
        if (dialogosAMostrar.get() <= dialogosMostrados.get()){

            // Escondemos el layout de los plugins
            Platform.runLater {
                layoutDialogos.isVisible = false
                layoutDialogos.isFocusTraversable = false
            }
        }

        // Permitimos la llegada de diálogos
        permitirLlegadaDialogos()

        // Solicitamos un diálogo más
        subsProcesadorDialogos!!.request(1)

    }
}