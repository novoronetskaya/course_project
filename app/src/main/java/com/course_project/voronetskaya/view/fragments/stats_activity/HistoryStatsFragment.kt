package com.course_project.voronetskaya.view.fragments.stats_activity

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
import com.course_project.voronetskaya.view.activities.StatsActivity
import com.course_project.voronetskaya.view.adapters.StatsHistoryAdapter
import java.util.*

class HistoryStatsFragment : Fragment() {
    private lateinit var graphButton: Button
    private lateinit var fromText: TextView
    private lateinit var toText: TextView
    private lateinit var recycler: RecyclerView

    private lateinit var calendar: Calendar
    private lateinit var adapter: StatsHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.history_fragment, container, false)

        graphButton = view.findViewById(R.id.b_graph)
        fromText = view.findViewById(R.id.tv_from_date)
        toText = view.findViewById(R.id.tv_to_date)
        recycler = view.findViewById(R.id.rv_history)

        adapter = StatsHistoryAdapter(listOf(), activity as StatsActivity)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(activity)

        graphButton.setOnClickListener {
            (activity as StatsActivity).popStack()
        }

        calendar = Calendar.getInstance()
        fromText.text = String.format(
            "%02d.%02d.%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        (activity as StatsActivity).getViewModel().setFromDate(
            String.format(
                "%d.%02d.%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )

        toText.text = String.format(
            "%02d.%02d.%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        (activity as StatsActivity).getViewModel().setToDate(
            String.format(
                "%d.%02d.%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )

        setData()

        fromText.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as StatsActivity,
                { _, year, month, day ->
                    fromText.text = String.format("%02d.%02d.%d", day, month + 1, year)
                    (activity as StatsActivity).getViewModel()
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

        toText.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as StatsActivity,
                { _, year, month, day ->
                    toText.text = String.format("%02d.%02d.%d", day, month + 1, year)
                    (activity as StatsActivity).getViewModel()
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
        return view
    }

    private fun setData() {
        (activity as StatsActivity).getViewModel().getHistory()
            .observe(viewLifecycleOwner) { history ->
                adapter.updateData(history)
            }
    }
}