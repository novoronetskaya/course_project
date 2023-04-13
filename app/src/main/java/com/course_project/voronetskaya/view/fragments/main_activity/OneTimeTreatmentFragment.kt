package com.course_project.voronetskaya.view.fragments.main_activity

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.TreatmentHistory
import com.course_project.voronetskaya.view.activities.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class OneTimeTreatmentFragment : Fragment() {
    private lateinit var medicineName: EditText
    private lateinit var time: TextView
    private lateinit var dose: EditText

    private lateinit var ready: Button
    private lateinit var cancel: Button

    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.one_time_treatment_fragment, container, false)

        medicineName = view.findViewById(R.id.et_treatment_medicine_name)
        time = view.findViewById(R.id.tv_time)
        dose = view.findViewById(R.id.et_dose_number)

        ready = view.findViewById(R.id.b_ready)
        cancel = view.findViewById(R.id.b_cancel)

        ready.setOnClickListener {
            if (medicineName.text == null || medicineName.text.toString().trim() == "") {
                medicineName.error = "Введите наименование препарата"
                return@setOnClickListener
            }

            if (dose.text == null || dose.text.toString().trim() == "" || dose.text.toString()
                    .toDoubleOrNull() == null
            ) {
                dose.error = "Введите дозу"
                return@setOnClickListener
            }

            val history = TreatmentHistory()
            history.setId(UUID.randomUUID().toString())
            history.setDose(dose.text.toString().toDouble())
            history.setMedicineName(medicineName.text.toString())
            val dateString = String.format("%s %s", dateFormatter.format(Date()), time.text)
            history.setScheduleDate(dateString)
            history.setMovedTo(dateString)

            (activity as MainActivity).popStack()
            (activity as MainActivity).getViewModel().addHistory(history)
        }

        cancel.setOnClickListener {
            (activity as MainActivity).popStack()
        }

        time.setOnClickListener {
            TimePickerDialog(
                activity,
                { _, hour, minute ->
                    time.text = String.format("%02d:%02d", hour, minute)
                },
                12,
                0,
                true
            ).show()
        }

        return view
    }
}