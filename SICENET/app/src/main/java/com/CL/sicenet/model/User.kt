package com.CL.sicenet.model

data class User(
    val acceso: Boolean,
    val estatus: String,
    val tipoUsuario: Int,
    val contrasenia: String,
    val matricula: String
)