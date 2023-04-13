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
import com.course_project.voronetskaya.view.activities.TreatmentActivity
import com.course_project.voronetskaya.view.adapters.TreatmentTimeAdapter

class NewTreatmentTimeFragment : Fragment() {
    private lateinit var timeRecycler: RecyclerView
    private lateinit var addTimeButton: Button
    private lateinit var readyButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choose_treatment_time_fragment, container, false)

        timeRecycler = view.findViewById(R.id.rv_time)
        addTimeButton = view.findViewById(R.id.b_add_time)
        readyButton = view.findViewById(R.id.b_ready)
        backButton = view.findViewById(R.id.b_back)

        val adapter = TreatmentTimeAdapter(requireArguments().getString("unit")!!, activity as TreatmentActivity)
        timeRecycler.adapter = adapter
        timeRecycler.layoutManager = LinearLayoutManager(activity)

        addTimeButton.setOnClickListener {
            adapter.addHolder()
        }

        readyButton.setOnClickListener {
            (activity as TreatmentActivity).addTreatmentTime(adapter.getData())
        }

        backButton.setOnClickListener {
            (activity as TreatmentActivity).popStack()
        }
        return view
    }
}