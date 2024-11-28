package com.novaservices.netwalk.domain

data class MerchantData(
    val id: Int?,
    val nro_afiliado: String?,
    val actual_address: String,
    val telefono_celular: String?,
    val telefono_cantv: String,
    val punto_de_refencia: String?,
    val nombre_comercio: String?,
    val serial_equipo: String?,
    val modelo_equipo: String?,
)
