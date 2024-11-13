package com.novaservices.lotonovabanklot.domain

import com.novaservices.netwalk.domain.Case

data class RequestResponse(
    val code: Int?,
    val message: String?,
    val rows: String?,
    val status: Int?,
    val username: String?
)
