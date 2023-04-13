package com.course_project.voronetskaya.view.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Treatment
import com.course_project.voronetskaya.view.activities.TreatmentActivity

class TreatmentAdapter(var treatmentsList: List<Treatment>, var activity: Context) :
    RecyclerView.Adapter<TreatmentAdapter.TreatmentViewHolder>() {
    inner class TreatmentViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val treatmentName: TextView = itemView.findViewById(R.id.tv_medicine_name)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)
        val background: ImageView = itemView.findViewById(R.id.iv_background)
        lateinit var treatment: Treatment
        val pauseIcon: ImageView = itemView.findViewById(R.id.iv_pause_resume)

        init {
            deleteIcon.setOnClickListener {
                AlertDialog.Builder(activity).setTitle("Удаление препарата")
                    .setMessage("Вы уверены, что хотите удалить принимаемый препарат?")
                    .setPositiveButton("Да") { _, _ ->
                        (activity as TreatmentActivity).getViewModel().deleteTreatment(treatment)
                    }
                    .setNegativeButton("Отмена") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }

            pauseIcon.setOnClickListener {
                (activity as TreatmentActivity).changeActiveTreatment(treatment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.treatment_kit_viewholder, parent, false)
        return TreatmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TreatmentViewHolder, position: Int) {
        holder.treatmentName.text = treatmentsList[position].getMedicineName()
        holder.treatment = treatmentsList[position]

        if (treatmentsList[position].getIsActive()) {
            holder.background.setBackgroundColor(0x5796BDE2)
            holder.pauseIcon.setImageResource(R.drawable.active_icon)
        } else {
            holder.background.setBackgroundColor(0x57D9D9D9)
            holder.pauseIcon.setImageResource(R.drawable.pause_icon)
        }
    }

    override fun getItemCount(): Int = treatmentsList.size

    public fun updateData(treatments: List<Treatment>) {
        treatmentsList = treatments
        notifyDataSetChanged()
    }
}