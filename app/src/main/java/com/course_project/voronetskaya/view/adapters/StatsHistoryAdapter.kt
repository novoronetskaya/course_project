package com.course_project.voronetskaya.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.TreatmentHistory
import com.course_project.voronetskaya.data.util.Status

class StatsHistoryAdapter(var history: List<TreatmentHistory>, var activity: Context) :
    RecyclerView.Adapter<StatsHistoryAdapter.StatsHistoryViewHolder>() {
    inner class StatsHistoryViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val medName: TextView = itemView.findViewById(R.id.tv_medicine_name)
        val dose: TextView = itemView.findViewById(R.id.tv_dose)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsHistoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.treatment_to_take_viewholder, parent, false)
        return StatsHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StatsHistoryViewHolder, position: Int) {
        holder.medName.text = history[position].getMedicineName() 
        holder.time.text = history[position].getScheduleDate().split(' ')[1] 
        
        if (history[position].getStatus() == Status.TAKEN) {
            holder.dose.text =
                String.format("Доза: %.02f. Статус: принято", history[position].getDose())
        } else if (history[position].getStatus() == Status.MOVED) {
            holder.dose.text =
                String.format("Доза: %.02f. Статус: перенесено", history[position].getDose())
        } else if (history[position].getStatus() == Status.CANCELLED) {
            holder.dose.text =
                String.format("Доза: %.02f. Статус: пропущено", history[position].getDose())
        } else {
            holder.dose.text =
                String.format("Доза: %.02f. Статус: неизвестно", history[position].getDose())
        }
    }

    override fun getItemCount(): Int = history.size

    public fun updateData(data: List<TreatmentHistory>) {
        history = data
        history = history.sortedByDescending {
            it.getScheduleDate().split(' ')[1]
        }

        notifyDataSetChanged()
    }
}