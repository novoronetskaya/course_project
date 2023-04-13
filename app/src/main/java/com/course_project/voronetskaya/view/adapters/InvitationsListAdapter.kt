package com.course_project.voronetskaya.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Invitation
import com.course_project.voronetskaya.view.activities.KitActivity

class InvitationsListAdapter (private var invitations: List<Invitation>, var activity: Context) :
    RecyclerView.Adapter<InvitationsListAdapter.InvitationViewHolder>() {
    inner class InvitationViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val kitName: TextView = itemView.findViewById(R.id.tv_name)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.iv_delete)
        private val background: ImageView = itemView.findViewById(R.id.iv_background)
        lateinit var invitation: Invitation

        init {
            background.setOnClickListener {
                (activity as KitActivity).showInvitationInfo(invitation)
            }

            deleteIcon.isEnabled = false
            deleteIcon.isVisible = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_without_pause, parent, false)
        return InvitationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        holder.kitName.text = invitations[position].getKitName()
        holder.invitation = invitations[position]
    }

    override fun getItemCount(): Int = invitations.size

    public fun updateData(data: List<Invitation>) {
        invitations = data
        notifyDataSetChanged()
    }
}