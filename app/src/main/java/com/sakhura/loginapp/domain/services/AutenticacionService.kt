package com.sakhura.loginapp.domain.services

// Importar las clases necesarias
import com.sakhura.loginapp.data.models.Usuario
import com.sakhura.loginapp.data.models.ResultadoAutenticacion

// Servicio de autenticación
class AutenticacionService {

    // Lista de usuarios de prueba
    private val listaUsuarios = mutableListOf<Usuario>()

    // Constructor - inicializar usuarios
    init {
        cargarUsuariosPrueba()
    }

    // Cargar usuarios de prueba
    private fun cargarUsuariosPrueba() {
        listaUsuarios.add(Usuario(1, "admin", "admin123", "admin@empresa.com", true))
        listaUsuarios.add(Usuario(2, "usuario1", "password123", "user1@empresa.com", true))
        listaUsuarios.add(Usuario(3, "test", "test123", "test@empresa.com", true))
        listaUsuarios.add(Usuario(4, "demo", "demo123", "demo@empresa.com", true))
    }

    // Método principal de autenticación
    fun autenticar(nombreUsuario: String, password: String): ResultadoAutenticacion {

        // Verificar que no estén vacíos
        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            return ResultadoAutenticacion.Error("Campos obligatorios")
        }

        // Buscar el usuario
        for (usuario in listaUsuarios) {
            if (usuario.nombreUsuario == nombreUsuario &&
                usuario.password == password &&
                usuario.estaActivo) {
                return ResultadoAutenticacion.Exitoso(usuario)
            }
        }

        // Si no encuentra al usuario
        return ResultadoAutenticacion.Error("Credenciales incorrectas")
    }
}