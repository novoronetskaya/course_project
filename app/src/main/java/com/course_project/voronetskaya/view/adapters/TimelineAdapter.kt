package com.course_project.voronetskaya.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.MainActivity
import com.course_project.voronetskaya.view.model.TreatmentHistoryAndTreatment
import com.course_project.voronetskaya.view.model.TreatmentWithTime

class TimelineAdapter(var treatments: List<TreatmentWithTime>, var activity: Context) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {
    inner class TimelineViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.tv_time)
        val medName: TextView = itemView.findViewById(R.id.tv_medicine_name)
        val dose: TextView = itemView.findViewById(R.id.tv_dose)
        private val background: ImageView = itemView.findViewById(R.id.iv_background)
        lateinit var treatmentWithTime: TreatmentWithTime

        init {
            background.setOnClickListener {
                (activity as MainActivity).showTreatmentInfo(treatmentWithTime)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.treatment_to_take_viewholder, parent, false)
        return TimelineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.treatmentWithTime = treatments[position]
        holder.time.text = treatments[position].time
        holder.medName.text = treatments[position].treatment.getMedicineName()

        if (treatments[position] is TreatmentHistoryAndTreatment) {
            holder.medName.text = (treatments[position] as TreatmentHistoryAndTreatment).treatmentHistory.getMedicineName()
        }
        holder.dose.text = String.format(
            "Доза: %.02f %s",
            treatments[position].dose,
            treatments[position].treatment.getUnit()
        )
    }

    override fun getItemCount(): Int = treatments.size

    public fun updateData(data: List<TreatmentWithTime>) {
        treatments = data
        treatments = treatments.sortedBy { it.time }
        notifyDataSetChanged()
    }
}