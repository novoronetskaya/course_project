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

class DependentMemberAdapter(
    private var membersList: List<User>,
    var activity: Context,
    private var isAdmin: Boolean
) :
    RecyclerView.Adapter<DependentMemberAdapter.DependentMemberViewHolder>() {
    inner class DependentMemberViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        val userBirthday: TextView = itemView.findViewById(R.id.tv_user_birthday)
        val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)
        val background: ImageView = itemView.findViewById(R.id.iv_background)
        lateinit var user: User

        init {
            deleteIcon.bringToFront()
            deleteIcon.visibility = View.VISIBLE
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

            background.setOnClickListener {
                (activity as KitActivity).showDependentUserProfile(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DependentMemberViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dependent_users_viewholder, parent, false)
        return DependentMemberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DependentMemberViewHolder, position: Int) {
        holder.user = membersList[position]
        holder.userName.text = String.format(
            "%s %s",
            membersList[position].getFirstName(),
            membersList[position].getSurname()
        )
        holder.userBirthday.text = membersList[position].getBirthday()

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