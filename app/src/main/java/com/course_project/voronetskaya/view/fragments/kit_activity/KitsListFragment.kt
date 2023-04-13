package com.course_project.voronetskaya.view.fragments.kit_activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.FirstAidKit
import com.course_project.voronetskaya.view.activities.KitActivity
import com.course_project.voronetskaya.view.adapters.KitsListAdapter
import com.course_project.voronetskaya.view.OrganizerApplication

class KitsListFragment : Fragment() {
    private lateinit var invitationsButton: Button
    private lateinit var plusIcon: ImageView
    private lateinit var kitsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.kit_list_fragment, container, false)

        invitationsButton = view.findViewById(R.id.b_invitations)
        invitationsButton.setOnClickListener {
            if (OrganizerApplication.auth.currentUser == null) {
                Toast.makeText(activity, "Недоступно в гостевом режиме", Toast.LENGTH_SHORT).show()
            } else {
                (activity as KitActivity).showInvitations()
            }
        }

        plusIcon = view.findViewById(R.id.iv_plus)
        plusIcon.setOnClickListener {
            val kitName = EditText(activity)
            kitName.hint = "Название"
            kitName.textSize = 19F
            AlertDialog.Builder(activity).setTitle("Новая аптечка").setView(kitName)
                .setPositiveButton("Готово") { _, _ ->
                    if (kitName.text == null || kitName.text.toString().trim() == "") {
                        kitName.error = "Введите название аптечки"
                    } else {
                        (activity as KitActivity).createNewKit(kitName.text.toString())
                    }
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                }.show()
        }

        kitsRecyclerView = view.findViewById(R.id.rv_kits)
        kitsRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter =
            KitsListAdapter(List(0) { FirstAidKit() }, (activity as KitActivity))

        kitsRecyclerView.adapter = adapter
        (activity as KitActivity).getViewModel().getKitsList().observe(viewLifecycleOwner) {
            kits -> if (kits == null) {
                return@observe
        }
            adapter.updateData(kits)
        }
        return view
    }
}