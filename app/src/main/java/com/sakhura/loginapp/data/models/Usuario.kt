package com.sakhura.loginapp.data.models

data class Usuario(
    val id: Int,
    val nombreUsuario: String,
    val password: String,
    val email: String,
    val estaActivo: Boolean = true
)