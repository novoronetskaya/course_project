package com.course_project.voronetskaya.view.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.User
import com.course_project.voronetskaya.view.activities.KitActivity

class IndependentMemberAdapter(
    private var membersList: List<User>,
    var activity: Context,
    private var isAdmin: Boolean,
    var uid: String
) :
    RecyclerView.Adapter<IndependentMemberAdapter.IndependentMemberViewHolder>() {
    inner class IndependentMemberViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        val userId: TextView = itemView.findViewById(R.id.tv_user_id)
        val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)
        lateinit var user: User

        init {
            deleteIcon.setOnClickListener {
                AlertDialog.Builder(activity).setTitle("Удаление участника")
                    .setMessage("Вы уверены, что хотите удалить участника?")
                    .setPositiveButton("Да") { _, _ ->
                        (activity as KitActivity).deleteKitMember(user)
                    }
                    .setNegativeButton("Отмена") { dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndependentMemberViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.independent_users_viewholder, parent, false)
        return IndependentMemberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IndependentMemberViewHolder, position: Int) {
        holder.userName.text = String.format(
            "%s %s",
            membersList[position].getFirstName(),
            membersList[position].getSurname()
        )
        holder.userId.text = String.format("ID: %s", membersList[position].getId())

        if (membersList[position].getId() == uid) {
            holder.userName.text = "Вы"
            holder.deleteIcon.isClickable = false
            holder.deleteIcon.isVisible = false
        }

        if (!isAdmin) {
            holder.deleteIcon.isClickable = false
            holder.deleteIcon.isVisible = false
        }
    }

    override fun getItemCount(): Int = membersList.size

    public fun updateData(members: List<User>) {
        membersList = members
        notifyDataSetChanged()
    }
}