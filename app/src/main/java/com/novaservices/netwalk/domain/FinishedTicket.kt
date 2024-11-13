package com.novaservices.netwalk.domain

data class FinishedTicket(
    val ticket_id: Int,
    val service_result: String,
    val observations: String,
    val proof: String,
)
