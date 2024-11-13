package com.novaservices.netwalk.domain

import java.io.Serializable

data class NovaWalkUser (
    val name: String?,
    val lastname: String?,
    val username: String?,
    val password: String?,
    val dni: String?,
    val id: Int?,
    val email: String?,
    val phone: String?,
    val technicalType: String?,
    val region_id: Int?
)