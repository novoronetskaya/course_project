package com.course_project.voronetskaya.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.MedicineRefill

class RefillAdapter(private var refills: List<MedicineRefill>, var activity: Context) :
    RecyclerView.Adapter<RefillAdapter.RefillViewHolder>() {
    inner class RefillViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountText: TextView = itemView.findViewById(R.id.tv_refill_amount)
        val refilledBy: TextView = itemView.findViewById(R.id.tv_refilled_by)
        val refillDate: TextView = itemView.findViewById(R.id.tv_refill_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefillViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.refills_viewholder, parent, false)
        return RefillViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RefillViewHolder, position: Int) {
        val date = refills[position].getDate().split('.')
        holder.refillDate.text = String.format("%s.%s.%s", date[2], date[1], date[0])
        holder.amountText.text = String.format("+%d", refills[position].getAmount())
        holder.refilledBy.text = String.format("ID пополнившего: %s", refills[position].getUserId())
    }

    override fun getItemCount(): Int = refills.size

    public fun updateData(data: List<MedicineRefill>) {
        refills = data
        refills = refills.sortedByDescending { it.getDate() }
        notifyDataSetChanged()
    }
}