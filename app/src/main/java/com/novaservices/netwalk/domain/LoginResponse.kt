package com.novaservices.lotonovabanklot.domain

data class LoginResponse(
//    val username: String,
//    val clave :String
    val code: Int,
    val message: String,
    val message2: String,
    val result: List<LoginResult>?,
    val id: Int?,
    val region_id: String?
)

data class LoginResult(
    val email: String?,
    val username: String?,
    val role_id: Int?
)
