package com.course_project.voronetskaya.view.fragments.main_activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.MainActivity
import com.course_project.voronetskaya.view.adapters.TimelineAdapter
import com.course_project.voronetskaya.view.OrganizerApplication
import java.text.SimpleDateFormat
import java.util.*

class TimelineFragment : Fragment() {
    private lateinit var plusIcon: ImageView
    private lateinit var profileIcon: ImageView
    private lateinit var dateTextView: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var returnText: TextView
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")
    private val calendar = Calendar.getInstance()
    private lateinit var adapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeline_fragment, container, false)
        plusIcon = view.findViewById(R.id.iv_plus)
        profileIcon = view.findViewById(R.id.iv_profile)
        dateTextView = view.findViewById(R.id.tv_date)
        recycler = view.findViewById(R.id.rv_hour_treatments)

        adapter = TimelineAdapter(listOf(), activity as MainActivity)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        dateTextView.text = SimpleDateFormat("dd.MM.yyyy").format(Date())

        (activity as MainActivity).getViewModel().getTreatmentsForToday()
            .observe(viewLifecycleOwner) { treatments ->
                adapter.updateData(treatments)
            }

        plusIcon.setOnClickListener {
            (activity as MainActivity).showOneTimeTreatmentFragment()
        }

        returnText = view.findViewById(R.id.tv_return)
        returnText.setOnClickListener {
            OrganizerApplication.sharedPreferences.edit().remove("dependentId").apply()
            (activity as MainActivity).finishDependentUser()
        }

        if (OrganizerApplication.sharedPreferences.getString("dependentId", "") != "") {
            plusIcon.visibility = View.GONE
            returnText.visibility = View.VISIBLE
        }

        profileIcon.setOnClickListener {
            (activity as MainActivity).showProfile()
        }

        dateTextView.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as MainActivity,
                { _, year, month, day ->
                    dateTextView.text = String.format("%02d.%02d.%d", day, month + 1, year)
                    val date = dateFormatter.parse(String.format("%d.%02d.%02d", year, month + 1, day))!!
                    setDataForDate(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = calendar.timeInMillis
            dialog.show()
        }

        return view
    }

    private fun setDataForDate(date: Date) {
        if (dateFormatter.format(date).equals(dateFormatter.format(Date()))) {
            (activity as MainActivity).getViewModel().getTreatmentsForToday()
                .observe(viewLifecycleOwner) { treatments ->
                    adapter.updateData(treatments)
                }
        } else {
            (activity as MainActivity).getViewModel().setDate(date)
            (activity as MainActivity).getViewModel().getTreatmentsForDate()
                .observe(viewLifecycleOwner) { treatments ->
                    adapter.updateData(treatments)
                }
        }
    }
}