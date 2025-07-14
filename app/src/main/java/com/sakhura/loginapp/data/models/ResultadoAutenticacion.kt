package com.sakhura.loginapp.data.models

sealed class ResultadoAutenticacion {
    data class Exitoso(val usuario: Usuario) : ResultadoAutenticacion()
    data class Error(val mensaje: String) : ResultadoAutenticacion()
}