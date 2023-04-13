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
import com.course_project.voronetskaya.data.model.FirstAidKit
import com.course_project.voronetskaya.view.activities.KitActivity

class KitsListAdapter(var kits: List<FirstAidKit>, var activity: Context) :
    RecyclerView.Adapter<KitsListAdapter.KitsViewHolder>() {
    inner class KitsViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val kitName: TextView = itemView.findViewById(R.id.tv_name)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)
        private val background: ImageView = itemView.findViewById(R.id.iv_background)
        lateinit var kit: FirstAidKit

        init {
            background.setOnClickListener {
                (activity as KitActivity).showKitInfo(kit)
            }

            deleteIcon.setOnClickListener {
                AlertDialog.Builder(activity).setTitle("Удаление аптечки")
                    .setMessage("Вы уверены, что хотите удалить аптечку?")
                    .setPositiveButton("Да") { _, _ ->
                        (activity as KitActivity).deleteKit(kit)
                    }
                    .setNegativeButton("Отмена") { dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_without_pause, parent, false)
        return KitsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KitsViewHolder, position: Int) {
        holder.kitName.text = kits[position].getName()
        holder.kit = kits[position]
    }

    override fun getItemCount(): Int = kits.size

    public fun updateData(data: List<FirstAidKit>) {
        kits = data
        notifyDataSetChanged()
    }
}