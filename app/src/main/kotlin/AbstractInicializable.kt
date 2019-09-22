
abstract class AbstractInicializable: Inicializable {

    var ejecPausado: Boolean = false
    var ejecCancelado: Boolean = false
    var ejecIniciado: Boolean = false
    var ejecPrecarga: Boolean = false

    override fun preCargar(block: () -> Unit) {
        if (ejecPrecarga){
            return
        }
        else {
            ejecPrecarga = true
            block.invoke()
        }
    }

    override fun preCargar() {}

    override fun iniciar(block: () -> Unit){
        if (ejecIniciado){
            return
        }
        else {
            ejecIniciado = true
            ejecCancelado = false
            ejecPausado = false
            ejecPrecarga = false
            block.invoke()
        }
    }

    override fun iniciar() {}

    override fun pausar(block: () -> Unit){
        if (ejecPausado || ejecCancelado){
            return
        }
        else {
            ejecPausado = true
            block.invoke()
        }
    }

    override fun pausar() {}

    override fun reanudar(block: () -> Unit){
        if (ejecPausado && !ejecCancelado){
            ejecPausado = false
            block.invoke()
        }
        else {
            return
        }
    }

    override fun reanudar() {}

    override fun cancelar(block: () -> Unit){
        if (ejecCancelado){
            return
        }
        else {
            ejecCancelado = true
            ejecIniciado = false
            block.invoke()
        }
    }

    override fun cancelar() {}
}