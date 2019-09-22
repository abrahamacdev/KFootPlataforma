package Controlador.Excepciones

class CreacionEscenarioException: Exception{

    constructor(): super()

    constructor(msg: String): super(msg)
}