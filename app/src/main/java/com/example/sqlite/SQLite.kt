package com.example.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(context: Context): SQLiteOpenHelper(context,"Usuarios", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        var query = "CREATE TABLE usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, rut VARCHAR(12), nombre VARCHAR(50), correo VARCHAR(100));"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var queryDrop = "DROP TABLE IF EXISTS usuario"
        db?.execSQL(queryDrop)
        onCreate(db)
    }

    data class Usuario(val rut: String, val nombre: String, val correo: String)

    //CRUD
    fun crearUsuario(rut:String, nombre:String, correo:String){
        val bd = this.writableDatabase //Modo edicion de la bd
        val contenedor = ContentValues()
        contenedor.put("rut", rut)
        contenedor.put("nombre",nombre)
        contenedor.put("correo",correo)
        bd.insert("usuario", null, contenedor)
        bd.close()//cierro la conexion
    }

    fun listarUsuarios(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM usuario", null)
    }

    @SuppressLint("Range")
    fun buscarUsuarioPorRut(rut: String):Usuario?{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuario WHERE rut = ?", arrayOf(rut))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            val correo = cursor.getString(cursor.getColumnIndex("correo"))
            cursor.close()
            return Usuario(rut, nombre, correo)
        } else {
            cursor.close()
            return null
        }
    }

    fun actualizarUsuario(rut: String, nombre: String, correo: String): Boolean {
        val db = this.writableDatabase
        val contenedor = ContentValues()
        contenedor.put("nombre", nombre)
        contenedor.put("correo", correo)
        val rowsUpdated = db.update("usuario", contenedor, "rut = ?", arrayOf(rut))
        db.close()
        return rowsUpdated > 0
    }

    fun eliminarUsuario(rut: String): Boolean {
        val db = this.writableDatabase
        val rowsDeleted = db.delete("usuario", "rut = ?", arrayOf(rut))
        db.close()
        return rowsDeleted > 0
    }

}