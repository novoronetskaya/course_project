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
import com.course_project.voronetskaya.data.model.Medicine
import com.course_project.voronetskaya.view.activities.KitActivity
import java.text.SimpleDateFormat
import java.util.*

class MedicineListAdapter(var medicineList: List<Medicine>, var activity: Context) :
    RecyclerView.Adapter<MedicineListAdapter.MedicineViewHolder>() {
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")

    inner class MedicineViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName: TextView = itemView.findViewById(R.id.tv_name)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)
        private val background: ImageView = itemView.findViewById(R.id.iv_background)
        lateinit var medicine: Medicine

        init {
            background.setOnClickListener {
                (activity as KitActivity).showMedicineInfo(medicine)
            }

            deleteIcon.setOnClickListener {
                AlertDialog.Builder(activity).setTitle("Удаление препарата")
                    .setMessage("Вы уверены, что хотите удалить препарат?")
                    .setPositiveButton("Да") { _, _ ->
                        (activity as KitActivity).deleteMedicine(medicine)
                    }
                    .setNegativeButton("Отмена") { dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_without_pause, parent, false)
        return MedicineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.medicineName.text = medicineList[position].getName()
        holder.medicine = medicineList[position]
    }

    override fun getItemCount(): Int = medicineList.size

    public fun updateData(medicine: List<Medicine>) {
        medicineList = medicine
        notifyDataSetChanged()
    }

    public fun sortByName() {
        medicineList = medicineList.sortedBy { it.getName() }
        notifyDataSetChanged()
    }

    public fun sortByDate() {
        medicineList = medicineList.sortedBy { it.getExpirationDate() }
        notifyDataSetChanged()
    }

    public fun filterByType(type: String, data: List<Medicine>) {
        if (type.equals("в наличии")) {
            medicineList = data.filter{it.getExpirationDate() >= dateFormatter.format(Date()) && it.getAmount() > 0.0}
        } else if (type.equals("просроченные")) {
            medicineList = data.filter{it.getExpirationDate() < dateFormatter.format(Date()) }
        } else if (type.equals("закончившиеся")) {
            medicineList = data.filter{it.getAmount() == 0.0}
        } else if (type.equals("все")) {
            medicineList = data
        }
        notifyDataSetChanged()
    }

    public fun filterBySymptom(symptom: String, data: List<Medicine>) {
        medicineList = data.filter { it.getSymptom().lowercase().trim().equals(symptom) }
        notifyDataSetChanged()
    }

    public fun filterByPharmEffect(effect: String, data: List<Medicine>) {
        medicineList = data.filter { it.getPharmEffect().lowercase().trim().equals(effect) }
        notifyDataSetChanged()
    }
}