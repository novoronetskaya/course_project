package com.course_project.voronetskaya.view.fragments.kit_activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.KitActivity
import java.util.*

class AddDependentUserFragment : Fragment() {
    private lateinit var userNameText: EditText
    private lateinit var userBirthdayText: TextView
    private lateinit var kitNameText: TextView
    private lateinit var readyButton: Button
    private lateinit var cancelButton: Button
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_user_fragment, container, false)

        userNameText = view.findViewById(R.id.et_user_name)
        userBirthdayText = view.findViewById(R.id.birth_date)
        kitNameText = view.findViewById(R.id.tv_kit_name)
        readyButton = view.findViewById(R.id.b_ready)
        cancelButton = view.findViewById(R.id.b_cancel)

        kitNameText.text = requireArguments().getString("kitName")

        calendar = Calendar.getInstance()
        userBirthdayText.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as KitActivity,
                { _, year, month, day ->
                    userBirthdayText.text = String.format("%02d.%02d.%d", day, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }

        readyButton.setOnClickListener {
            if (userNameText.text == null || userNameText.text.toString().trim() == "") {
                userNameText.error = "Введите имя пользователя"
                return@setOnClickListener
            }

            if (userBirthdayText.text == null || userBirthdayText.text.toString().trim() == "") {
                userBirthdayText.error = "Введите дату рождения пользователя"
                return@setOnClickListener
            }

            (activity as KitActivity).addDependentMember(
                userNameText.text.toString(),
                userBirthdayText.text.toString()
            )
        }

        cancelButton.setOnClickListener {
            (activity as KitActivity).popStack()
        }
        return view
    }
}