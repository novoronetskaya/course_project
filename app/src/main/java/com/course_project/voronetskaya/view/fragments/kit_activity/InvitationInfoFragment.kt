package com.course_project.voronetskaya.view.fragments.kit_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.KitActivity

class InvitationInfoFragment : Fragment() {
    private lateinit var kitName: TextView
    private lateinit var invitedById: TextView
    private lateinit var invitedByName: TextView
    private lateinit var message: TextView
    private lateinit var declineButton: Button
    private lateinit var acceptButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.kit_invitation_info, container, false)

        kitName = view.findViewById(R.id.tv_kit_name)
        invitedById = view.findViewById(R.id.tv_invited_by_id)
        invitedByName = view.findViewById(R.id.tv_invited_by_name)
        message = view.findViewById(R.id.tv_invitation_message)
        declineButton = view.findViewById(R.id.b_decline)
        acceptButton = view.findViewById(R.id.b_accept)

        kitName.text = requireArguments().getString("kitName")
        invitedById.append(requireArguments().getString("invitedById"))
        invitedByName.append(requireArguments().getString("invitedByName"))
        message.append(requireArguments().getString("message"))

        declineButton.setOnClickListener {
            (activity as KitActivity).declineInvitation(requireArguments().getString("kitId")!!)
        }

        acceptButton.setOnClickListener {
            (activity as KitActivity).acceptInvitation(requireArguments().getString("kitId")!!)
        }
        return view
    }
}