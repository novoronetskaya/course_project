package com.course_project.voronetskaya.view.fragments.stats_activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.TreatmentHistory
import com.course_project.voronetskaya.data.util.Status
import com.course_project.voronetskaya.view.activities.StatsActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GraphStatsFragment : Fragment() {
    private lateinit var historyButton: Button
    private lateinit var fromDate: TextView
    private lateinit var toDate: TextView
    private lateinit var lineChart: LineChart

    private lateinit var calendar: Calendar
    private val formatter = SimpleDateFormat("yyyy.MM.dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.graph_fragment, container, false)

        historyButton = view.findViewById(R.id.b_history)
        fromDate = view.findViewById(R.id.tv_from_date)
        toDate = view.findViewById(R.id.tv_to_date)
        lineChart = view.findViewById(R.id.chart)

        calendar = Calendar.getInstance()
        fromDate.text = String.format(
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

        toDate.text = String.format(
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

        fromDate.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as StatsActivity,
                { _, year, month, day ->
                    fromDate.text = String.format("%02d.%02d.%d", day, month + 1, year)
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

        toDate.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as StatsActivity,
                { _, year, month, day ->
                    toDate.text = String.format("%02d.%02d.%d", day, month + 1, year)
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

        historyButton.setOnClickListener {
            (activity as StatsActivity).showHistory(formatter.format(Date()))
        }
        return view
    }

    private fun setData() {
        (activity as StatsActivity).getViewModel().getHistory()
            .observe(viewLifecycleOwner) { history ->
                updateGraph(history)
            }
    }

    private fun updateGraph(data: List<TreatmentHistory>) {
        val grouppedData = data.groupBy { it.getTreatmentId() }
        val dates = mutableSetOf<String>()

        for (item in data) {
            dates.add(item.getScheduleDate().split(' ')[0])
        }
        val datesList = dates.sorted()

        val datasets = arrayListOf<ILineDataSet>()
        for (key in grouppedData.keys) {
            val line = ArrayList<Entry>()

            for (i in 0..(datesList.size - 1)) {
                val shouldTake =
                    grouppedData[key]!!.filter {
                        it.getScheduleDate().startsWith(datesList[i])
                    }.size
                val took = grouppedData[key]!!.filter {
                    it.getScheduleDate()
                        .startsWith(datesList[i]) && it.getStatus() == Status.TAKEN && it.getTakenAt()
                        .startsWith(datesList[i])
                }.size

                if (shouldTake == 0) {
                    line.add(Entry(i.toFloat(), 100.0F))
                } else {
                    line.add(Entry(i.toFloat(), took.toFloat() / shouldTake * 100))
                }
            }

            datasets.add(LineDataSet(line, grouppedData[key]!![0].getMedicineName()))
        }

        lineChart.data = LineData(datasets)
        val xAxis = lineChart.xAxis
        xAxis.labelCount = datesList.size
        xAxis.valueFormatter = object: ValueFormatter() {
            public override fun getFormattedValue(value: Float, axis: AxisBase): String {
                return datesList[value.toInt()]
            }
        }

        lineChart.invalidate()
    }
}