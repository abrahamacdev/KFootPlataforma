package Controlador.Excepciones

import java.lang.Exception

class ComandoException: Exception {

    constructor(detalle: String): super(detalle){}

    constructor(comando: String, detalle: String) : super("El comando $comando $detalle") {}

}