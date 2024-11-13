package com.novaservices.netwalk.adapter

import com.novaservices.netwalk.databinding.ItemTicketBinding
import com.novaservices.netwalk.domain.Operations


import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class CasesViewHolder(view: View):ViewHolder(view) {
    private val binding = ItemTicketBinding.bind(view)
    fun render(ticket: Operations, onClickListener:(Operations) -> Unit) {
        binding.title.text = ticket.titulo!!.replace("_", " ").toUpperCase()
        binding.ticketNumber.text = ticket.documento_origen!!.replace("_", " ").toUpperCase()
//        binding.tvTypePayment.text = lastRechargesModel.servicio_solicitado
//        binding.tvPhoneNumber.text = lastRechargesModel.numero_contrato.toString()
//        binding.tvDate.text = lastRechargesModel.fecha_servicio
//        binding.amount.text = lastRechargesModel.monto


        itemView.setOnClickListener{ onClickListener(ticket) }
    }
}