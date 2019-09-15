
abstract class AbstractInicializable: Inicializable {

    var ejecPausado: Boolean = false
    var ejecCancelado: Boolean = false
    var ejecIniciado: Boolean = false
    var ejecPrecarga: Boolean = false

    override fun preCargar() {
        if (ejecPrecarga){
            return
        }
        else {
            ejecPrecarga = true
        }
    }

    override fun iniciar(){
        if (ejecIniciado){
            return
        }
        else {
            ejecIniciado = true
            ejecCancelado = false
            ejecPausado = false
            ejecPrecarga = false
        }
    }

    override fun pausar(){
        if (ejecPausado || ejecCancelado){
            return
        }
        else {
            ejecPausado = true
        }
    }

    override fun reanudar(){
        if (ejecPausado && !ejecCancelado){
            ejecPausado = false
        }
        else {
            return
        }
    }

    override fun cancelar(){
        if (ejecCancelado){
            return
        }
        else {
            ejecCancelado = true
            ejecIniciado = false
        }
    }
}