package com.course_project.voronetskaya.view.fragments.kit_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.KitActivity
import com.course_project.voronetskaya.view.adapters.InvitationsListAdapter

class InvitationsListFragment : Fragment() {
    private lateinit var kitsListButton: Button
    private lateinit var invitationsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.kit_invitations_list, container, false)

        kitsListButton = view.findViewById(R.id.b_my_kits)
        invitationsRecyclerView = view.findViewById(R.id.rv_invitations)

        val adapter = InvitationsListAdapter(listOf(), activity as KitActivity)
        invitationsRecyclerView.adapter = adapter
        invitationsRecyclerView.layoutManager = LinearLayoutManager(activity)
        kitsListButton.setOnClickListener {
            (activity as KitActivity).popStack()
        }

        (activity as KitActivity).getViewModel().getInvitationsList()
            .observe(viewLifecycleOwner) { invitations ->
                adapter.updateData(invitations)
            }
        return view
    }
}