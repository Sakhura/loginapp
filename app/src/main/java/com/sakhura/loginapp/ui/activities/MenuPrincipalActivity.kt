package com.sakhura.loginapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sakhura.loginapp.ui.activities.LoginActivity
import java.text.SimpleDateFormat
import java.util.*

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var tvFecha: TextView
    private lateinit var btnPerfil: Button
    private lateinit var btnConfiguracion: Button
    private lateinit var btnCerrarSesion: Button

    private var usuarioId: Int = 0
    private var usuarioNombre: String = ""
    private var usuarioEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        inicializarVistas()
        obtenerDatosUsuario()
        configurarEventos()
        mostrarBienvenida()
    }

    private fun inicializarVistas() {
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvFecha = findViewById(R.id.tv_fecha)
        btnPerfil = findViewById(R.id.btn_perfil)
        btnConfiguracion = findViewById(R.id.btn_configuracion)
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion)
    }

    private fun obtenerDatosUsuario() {
        usuarioId = intent.getIntExtra("USUARIO_ID", 0)
        usuarioNombre = intent.getStringExtra("USUARIO_NOMBRE") ?: "Usuario"
        usuarioEmail = intent.getStringExtra("USUARIO_EMAIL") ?: "email@empresa.com"
    }

    private fun configurarEventos() {
        btnPerfil.setOnClickListener {
            mostrarPerfil()
        }

        btnConfiguracion.setOnClickListener {
            Toast.makeText(this, "⚙️ Configuración próximamente", Toast.LENGTH_SHORT).show()
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun mostrarBienvenida() {
        tvBienvenida.text = "¡Bienvenido, $usuarioNombre!"
        tvFecha.text = "Sesión iniciada: ${obtenerFechaActual()}"
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }

    private fun mostrarPerfil() {
        val perfilInfo = """
            👤 Información del Perfil
            
            🆔 ID: $usuarioId
            👤 Usuario: $usuarioNombre
            📧 Email: $usuarioEmail
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("👤 Mi Perfil")
            .setMessage(perfilInfo)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("🚪 Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                Toast.makeText(this, "👋 ¡Hasta luego!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}