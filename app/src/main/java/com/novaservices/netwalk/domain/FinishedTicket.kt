package com.novaservices.netwalk.domain

import java.util.Date

data class FinishedTicket(
    val ticket_id: Int,
    val service_result: String,
    val observations: String,
    val proof: String,
    val close_date: String,
    val result_ticket_number: String,
)
