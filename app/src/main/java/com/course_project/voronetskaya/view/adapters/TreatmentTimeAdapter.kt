package com.course_project.voronetskaya.view.adapters

import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.TreatmentTime

class TreatmentTimeAdapter(val unit: String, var activity: Context) :
    RecyclerView.Adapter<TreatmentTimeAdapter.TreatmentTimeViewHolder>() {
    private val timeList = arrayListOf<TreatmentTime>()

    init {
        val time = TreatmentTime()
        time.setDose(1.0)
        timeList.add(time)
    }

    inner class TreatmentTimeViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val timeText: TextView = itemView.findViewById(R.id.tv_time)
        private val doseText: EditText = itemView.findViewById(R.id.tv_dose_number)
        private val medicineUnit: TextView = itemView.findViewById(R.id.tv_dose_type)
        val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)
        var treatmentTime: TreatmentTime = TreatmentTime()

        init {
            timeText.text = String.format(
                "%02d.%02d",
                treatmentTime.getHour(),
                treatmentTime.getMinute()
            )
            medicineUnit.text = unit
            doseText.setText(treatmentTime.getDose().toString())
            doseText.doAfterTextChanged {
                if (doseText.text.toString().trim() != "") {
                    treatmentTime.setDose(doseText.text.toString().toDouble())
                }
            }

            timeText.setOnClickListener {
                TimePickerDialog(
                    activity,
                    { _, hour, minute ->
                        timeText.text = String.format("%02d:%02d", hour, minute)
                        treatmentTime.setHour(hour)
                        treatmentTime.setMinute(minute)
                    },
                    12,
                    0,
                    true
                ).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentTimeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.treatment_time_viewholder, parent, false)
        return TreatmentTimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TreatmentTimeViewHolder, position: Int) {
        holder.treatmentTime = timeList[position]
        holder.deleteIcon.setOnClickListener {
            timeList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = timeList.size

    public fun addHolder() {
        val time = TreatmentTime()
        time.setDose(1.0)
        time.setHour(12)
        time.setMinute(34)

        timeList.add(time)
        notifyItemInserted(timeList.size - 1)
    }

    public fun getData(): List<TreatmentTime> {
        return timeList
    }
}