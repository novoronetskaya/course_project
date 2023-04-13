package com.course_project.voronetskaya.view.fragments.kit_activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.KitActivity
import com.course_project.voronetskaya.view.adapters.RefillAdapter
import java.util.Calendar

class MedicineRefillsFragment : Fragment() {
    private lateinit var refillsRecycler: RecyclerView
    private lateinit var fromDate: TextView
    private lateinit var toDate: TextView
    private lateinit var infoButton: Button
    private lateinit var kitName: TextView
    private lateinit var medicineName: TextView

    private lateinit var calendar: Calendar
    private lateinit var adapter: RefillAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.refills_fragment, container, false)

        adapter = RefillAdapter(listOf(), activity as KitActivity)
        refillsRecycler = view.findViewById(R.id.rv_refills)
        refillsRecycler.adapter = adapter
        refillsRecycler.layoutManager = LinearLayoutManager(activity)

        kitName = view.findViewById(R.id.tv_kit_name)
        kitName.text = requireArguments().getString("kitName")

        medicineName = view.findViewById(R.id.tv_medicine_name)
        medicineName.text = requireArguments().getString("medicineName")

        calendar = Calendar.getInstance()
        fromDate = view.findViewById(R.id.tv_from_date)
        fromDate.text = String.format(
            "%02d.%02d.%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        (activity as KitActivity).getViewModel().setFromDate(
            String.format(
                "%d.%02d.%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )

        toDate = view.findViewById(R.id.tv_to_date)
        toDate.text = String.format(
            "%02d.%02d.%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        (activity as KitActivity).getViewModel().setToDate(
            String.format(
                "%d.%02d.%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )

        infoButton = view.findViewById(R.id.b_details)

        fromDate.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as KitActivity,
                { _, year, month, day ->
                    fromDate.text = String.format("%02d.%02d.%d", day, month + 1, year)
                    (activity as KitActivity).getViewModel()
                        .setFromDate(String.format("%d.%02d.%02d", year, month + 1, day))
                    setData()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }

        toDate.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as KitActivity,
                { _, year, month, day ->
                    toDate.text = String.format("%02d.%02d.%d", day, month + 1, year)
                    (activity as KitActivity).getViewModel()
                        .setToDate(String.format("%d.%02d.%02d", year, month + 1, day))
                    setData()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }

        infoButton.setOnClickListener {
            (activity as KitActivity).popStack()
        }
        setData()
        return view
    }

    private fun setData() {
        (activity as KitActivity).getViewModel().getRefills()
            .observe(viewLifecycleOwner) { refills ->
                adapter.updateData(refills)
            }
    }
}