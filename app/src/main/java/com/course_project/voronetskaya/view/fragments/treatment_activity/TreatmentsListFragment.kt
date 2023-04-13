package com.course_project.voronetskaya.view.fragments.treatment_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Treatment
import com.course_project.voronetskaya.view.activities.TreatmentActivity
import com.course_project.voronetskaya.view.adapters.TreatmentAdapter

class TreatmentsListFragment : Fragment() {
    private lateinit var treatmentsRecycler: RecyclerView
    private lateinit var addButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.treatments_list_fragment, container, false)

        treatmentsRecycler = view.findViewById(R.id.rv_treatments)
        addButton = view.findViewById(R.id.b_add)

        addButton.setOnClickListener {
            (activity as TreatmentActivity).showNewTreatment()
        }

        val adapter =
            TreatmentAdapter(List(0) { Treatment() }, (activity as TreatmentActivity))
        treatmentsRecycler.adapter = adapter
        treatmentsRecycler.layoutManager = LinearLayoutManager(activity)
        (activity as TreatmentActivity).getViewModel().getTreatmentsList()
            .observe(viewLifecycleOwner) { treatments ->
                adapter.updateData(treatments)
            }
        return view
    }
}