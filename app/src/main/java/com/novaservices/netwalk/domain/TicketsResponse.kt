package com.novaservices.netwalk.domain

data class TicketsResponse(
    val code: String?,
    val message: String?,
    val data: List<Operations>?
)
data class Operations(
    val id: String?,
    val titulo: String?,
    val documento_origen: String?,
    val proyecto: String?,
    val asignada_a: String?,
    val fecha_de_inicio: String?,
    val fecha_final: String?,
    val timesheet_timer_first_use: String?,
    val timesheet_timer_last_use: String?,
    val cluster: String?,
    val fecha_inicio: String?,
    val tipo_de_tarea: String?,
    val etapa: String?,
    val fallas: String?,
    val zona_de_atencion: String?,
    val numero_de_afiliacion: String?,
    val denominacion_comercial: String?,
    val equipo: String?,
    val equipo_a_instalar: String?,
    val RIF: String?,
    val direccion: String?,
    val user_id: String?,
    val region_id: String?,
    val tecnico_id: String?,
    val status: String?,
    val telefono_2: String
)