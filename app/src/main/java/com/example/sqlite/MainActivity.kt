package com.example.sqlite

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var etRut: EditText
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var tvListado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val guardar = findViewById<Button>(R.id.btnGuardar)
        val listar = findViewById<Button>(R.id.btnListar)
        val buscar = findViewById<Button>(R.id.btnBuscar)
        val actualizar = findViewById<Button>(R.id.btnActualizar)
        val eliminar = findViewById<Button>(R.id.btnEliminar)
        val etRut = findViewById<EditText>(R.id.etxtRut)
        val etNombre = findViewById<EditText>(R.id.etxtNombre)
        val etCorreo = findViewById<EditText>(R.id.etxtCorreo)
        val Listado = findViewById<TextView>(R.id.tvListado)

        val bd = SQLite(this)
        guardar.setOnClickListener {
            val rut = etRut.text.toString()
            val nombre = etNombre.text.toString()
            val correo = etCorreo.text.toString()

            bd.crearUsuario(rut, nombre, correo)

            etRut.text.clear()
            etNombre.text.clear()
            etCorreo.text.clear()
        }

        listar.setOnClickListener {
            val cursor = bd.listarUsuarios()
            Listado.text = ""
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val rut = cursor.getString(1)
                val nombre = cursor.getString(2)
                val correo = cursor.getString(3)

                Listado.append("Id: $id - Rut: $rut - Nombre: $nombre - Correo: $correo\n")
            }
            cursor.close()
        }

        buscar.setOnClickListener {
            val rut = etRut.text.toString()
            val usuario = bd.buscarUsuarioPorRut(rut)

            if (usuario != null) {
                etNombre.setText(usuario.nombre)
                etCorreo.setText(usuario.correo)
            } else {
                mostrarAlerta("Usuario no encontrado", "El usuario con el rut $rut no existe.")
            }
        }

        actualizar.setOnClickListener {
            val rut = etRut.text.toString()
            val nombre = etNombre.text.toString()
            val correo = etCorreo.text.toString()

            if (bd.actualizarUsuario(rut, nombre, correo)) {
                mostrarAlerta("Actualización Exitosa", "Los datos del usuario han sido actualizados.")
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el usuario.")
            }
        }

        eliminar.setOnClickListener {
            val rut = etRut.text.toString()

            if (bd.eliminarUsuario(rut)) {
                mostrarAlerta("Eliminación Exitosa", "El usuario ha sido eliminado.")
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el usuario.")
            }

            etRut.text.clear()
            etNombre.text.clear()
            etCorreo.text.clear()
        }
    }
    @SuppressLint("Range")
    private fun mostrarListado(cursor: Cursor) {
        val sb = StringBuilder()
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
                val correo = cursor.getString(cursor.getColumnIndex("correo"))
                sb.append("Nombre: $nombre, Correo: $correo\n")
            } while (cursor.moveToNext())
        } else {
            mostrarAlerta(
                "Usuarios no encontrados",
                "No se encontraron usuarios en la base de datos."
            )
            etNombre.text.clear()
            etCorreo.text.clear()
        }
        tvListado.text = sb.toString()
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("OK") { _, _ -> }
        builder.create().show()
    }
}