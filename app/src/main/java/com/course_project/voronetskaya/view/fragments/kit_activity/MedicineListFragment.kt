package com.course_project.voronetskaya.view.fragments.kit_activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Medicine
import com.course_project.voronetskaya.view.activities.KitActivity
import com.course_project.voronetskaya.view.adapters.MedicineListAdapter

class MedicineListFragment : Fragment() {
    private lateinit var plusIcon: ImageView
    private lateinit var membersIcon: ImageView
    private lateinit var sortIcon: ImageView
    private lateinit var filterIcon: ImageView
    private lateinit var medicineRecycler: RecyclerView
    private lateinit var recyclerAdapter: MedicineListAdapter
    private lateinit var kitName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.kit_medicine_list_fragment, container, false)

        kitName = view.findViewById(R.id.tv_kit_title)
        kitName.text = requireArguments().getString("kitName")
        plusIcon = view.findViewById(R.id.iv_plus)
        membersIcon = view.findViewById(R.id.iv_members_icon)
        sortIcon = view.findViewById(R.id.iv_sort_icon)
        filterIcon = view.findViewById(R.id.iv_filter_icon)
        medicineRecycler = view.findViewById(R.id.rv_medicine)

        recyclerAdapter =
            MedicineListAdapter(List(0) { Medicine() }, (activity as KitActivity))
        medicineRecycler.layoutManager = LinearLayoutManager(activity)
        medicineRecycler.adapter = recyclerAdapter

        (activity as KitActivity).getViewModel().getMedicineFromKit()
            .observe(viewLifecycleOwner) { medicine ->
                recyclerAdapter.updateData(medicine)
            }

        plusIcon.setOnClickListener {
            (activity as KitActivity).showAddMedicineFragment()
        }

        membersIcon.setOnClickListener {
            (activity as KitActivity).showMembers()
        }

        sortIcon.setOnClickListener {
            var chosenPos = -1
            AlertDialog.Builder(activity).setTitle("Сортировать по: ")
                .setPositiveButton("Готово") { dialog, _ ->
                    if (chosenPos == 0) {
                        recyclerAdapter.sortByName()
                    } else if (chosenPos == 1) {
                        recyclerAdapter.sortByDate()
                    } else {
                        dialog.dismiss()
                    }
                }.setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }.setSingleChoiceItems(
                    arrayOf(
                        "наименованию",
                        "сроку годности (сначала истекающие)"
                    ), chosenPos
                ) { _, item ->
                    chosenPos = item
                }.create().show()
        }

        filterIcon.setOnClickListener {
            val dialog = Dialog(activity as KitActivity)
            dialog.setContentView(R.layout.filter_medicine_dialog)

            val typeCheckBox: CheckBox = dialog.findViewById(R.id.cb_type)
            val symptomCheckBox: CheckBox = dialog.findViewById(R.id.cb_symptom)
            val pharmEffectCheckBox: CheckBox = dialog.findViewById(R.id.cb_pharm_effect)

            val typeSpinner: Spinner = dialog.findViewById(R.id.sp_type)
            val symptomSpinner: Spinner = dialog.findViewById(R.id.sp_symptom)
            val pharmEffectSpinner: Spinner = dialog.findViewById(R.id.sp_pharm_effect)

            val readyButton: Button = dialog.findViewById(R.id.b_ready)
            val cancelButton: Button = dialog.findViewById(R.id.b_cancel)

            val typeAdapter = ArrayAdapter(
                activity as KitActivity,
                android.R.layout.simple_spinner_item,
                arrayOf("все", "в наличии", "просроченные", "закончившиеся")
            )
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = typeAdapter

            val symptomsData = getMedicineSymptoms()
            val symptomAdapter = ArrayAdapter(
                activity as KitActivity,
                android.R.layout.simple_spinner_item,
                symptomsData
            )
            symptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            symptomSpinner.adapter = symptomAdapter

            val effectData = getMedicinePharmEffect()
            val effectAdapter = ArrayAdapter(
                activity as KitActivity,
                android.R.layout.simple_spinner_item,
                effectData
            )
            effectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            pharmEffectSpinner.adapter = effectAdapter

            readyButton.setOnClickListener {
                if (typeCheckBox.isChecked) {
                    recyclerAdapter.filterByType(
                        typeSpinner.selectedItem as String,
                        (activity as KitActivity).getViewModel().getMedicineFromKit().value
                            ?: listOf()
                    )
                }

                if (symptomCheckBox.isChecked) {
                    recyclerAdapter.filterBySymptom(
                        symptomSpinner.selectedItem as String,
                        (activity as KitActivity).getViewModel().getMedicineFromKit().value
                            ?: listOf()
                    )
                }

                if (pharmEffectCheckBox.isChecked) {
                    recyclerAdapter.filterByPharmEffect(
                        pharmEffectSpinner.selectedItem as String,
                        (activity as KitActivity).getViewModel().getMedicineFromKit().value
                            ?: listOf()
                    )
                }
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
        return view
    }

    private fun getMedicineSymptoms(): Array<String> {
        val symptomsSet = mutableSetOf<String>()

        for (medicine in recyclerAdapter.medicineList) {
            symptomsSet.add(medicine.getSymptom().lowercase().trim())
        }
        return symptomsSet.toTypedArray()
    }

    private fun getMedicinePharmEffect(): Array<String> {
        val symptomsSet = mutableSetOf<String>()

        for (medicine in recyclerAdapter.medicineList) {
            symptomsSet.add(medicine.getPharmEffect().lowercase().trim())
        }
        return symptomsSet.toTypedArray()
    }
}