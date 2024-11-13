package com.novaservices.lotonovabanklot.domain

import java.io.Serializable

data class Play (
    val hour_draw: String?,
    val animal_number: String?,
    val animal_name: String?,
    val amount: String?,
    val lottery_brand: String?,
    val ticket_created_at: String?,
    val reference: String?
): Serializable
