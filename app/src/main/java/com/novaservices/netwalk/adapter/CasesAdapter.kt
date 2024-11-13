package com.novaservices.netwalk.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.novaservices.netwalk.R
import com.novaservices.netwalk.databinding.ItemTicketBinding
import com.novaservices.netwalk.domain.Operations

class CaseAdapter(
    private val lastCasesList: List<Operations>,
    private val onClickListener:(Operations) -> Unit) : RecyclerView.Adapter<CasesViewHolder>(){
    private var listData: MutableList<Operations> = lastCasesList as MutableList<Operations>
//    inner class MyViewHolder(val view: View): RecyclerView.ViewHolder(view){
//        val binding = ItemTicketBinding.bind(view)
//        fun bind(property: Operations, index: Int, onClickListener: (Operations) -> Unit){
//            binding.title.text = property.titulo
//            binding.ticket.text = property.documento_origen
////            binding.Amount.text = property.amount
////            binding.animal.text = "${property.animal_number}  ${property.animal_name}"
////            binding.playHour.text = property.hour_draw
////            binding.deleteCurrent.setOnClickListener{
////                deleteItem(index)
////            }
//
//
//        }
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CasesViewHolder(layoutInflater.inflate(R.layout.item_ticket, parent, false))
    }
    override fun getItemCount(): Int = lastCasesList.size
    override fun onBindViewHolder(holder: CasesViewHolder, position: Int) {
        val item = lastCasesList[position]
        holder.render(item, onClickListener)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(index: Int){
        listData.removeAt(index)
        notifyDataSetChanged()
    }
}
//
//
//
//
//
//
//package com.novaservices.lotonovabanklot.adapter
//
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.novaservices.lotonovabanklot.R
//import com.novaservices.lotonovabanklot.databinding.ItemPlayBinding
