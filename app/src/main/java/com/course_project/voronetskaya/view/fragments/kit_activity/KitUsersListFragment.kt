package com.course_project.voronetskaya.view.fragments.kit_activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.User
import com.course_project.voronetskaya.view.activities.KitActivity
import com.course_project.voronetskaya.view.adapters.DependentMemberAdapter
import com.course_project.voronetskaya.view.adapters.IndependentMemberAdapter
import com.course_project.voronetskaya.view.OrganizerApplication

class KitUsersListFragment : Fragment() {
    private lateinit var plusIcon: ImageView
    private lateinit var independentRecycler: RecyclerView
    private lateinit var dependentRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.kit_users_list_fragment, container, false)

        plusIcon = view.findViewById(R.id.iv_plus)
        independentRecycler = view.findViewById(R.id.rv_independent_users)
        dependentRecycler = view.findViewById(R.id.rv_managed_users)

        plusIcon.setOnClickListener {
            AlertDialog.Builder(activity).setTitle("Добавление пользователя")
                .setMessage("Какого пользователя Вы хотите добавить? ")
                .setPositiveButton("Самостоятельного") { _, _ ->
                    if (OrganizerApplication.auth.currentUser == null) {
                        Toast.makeText(
                            activity,
                            "Недоступно в гостевом режиме. ",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }
                    (activity as KitActivity).showAddIndependentUser()
                }
                .setNeutralButton("Под управлением") { _, _ ->
                    (activity as KitActivity).showAddDependentUser()
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        val independentAdapter =
            IndependentMemberAdapter(
                List(0) { User() },
                (activity as KitActivity),
                requireArguments().getBoolean("isAdmin"),
                requireArguments().getString("userId")!!
            )

        val dependentAdapter =
            DependentMemberAdapter(
                List(0) { User() },
                (activity as KitActivity),
                requireArguments().getBoolean("isAdmin")
            )

        independentRecycler.layoutManager = LinearLayoutManager(activity)
        dependentRecycler.layoutManager = LinearLayoutManager(activity)
        independentRecycler.adapter = independentAdapter
        dependentRecycler.adapter = dependentAdapter

        (activity as KitActivity).getViewModel().getIndependentMembers()
            .observe(viewLifecycleOwner) { users ->
                independentAdapter.updateData(users)
            }

        (activity as KitActivity).getViewModel().getDependentMembers()
            .observe(viewLifecycleOwner) { users ->
                dependentAdapter.updateData(users)
            }
        return view
    }
}