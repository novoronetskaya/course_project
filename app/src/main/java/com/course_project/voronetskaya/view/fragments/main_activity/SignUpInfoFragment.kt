package com.course_project.voronetskaya.view.fragments.main_activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.MainActivity
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.util.*

class SignUpInfoFragment : Fragment() {
    private lateinit var nameEditText: TextView
    private lateinit var surnameEditText: TextView
    private lateinit var birthdayTextView: TextView
    private lateinit var passwordEditText: TextView
    private lateinit var confirmPasswordEditText: TextView
    private lateinit var readyButton: Button
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.registration_enter_info_fragment, container, false)

        readyButton = view.findViewById(R.id.b_ready)
        nameEditText = view.findViewById(R.id.et_user_name)
        birthdayTextView = view.findViewById(R.id.et_birth_date)
        passwordEditText = view.findViewById(R.id.et_password)
        confirmPasswordEditText = view.findViewById(R.id.et_confirm_password)
        surnameEditText = view.findViewById(R.id.et_surname)

        calendar = Calendar.getInstance()
        birthdayTextView.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as MainActivity,
                { _, year, month, day ->
                    birthdayTextView.text = String.format("%02d.%02d.%d", day, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }

        readyButton.setOnClickListener {
            handleReadyButtonClick()
        }

        val backButton = view.findViewById<Button>(R.id.b_back)
        backButton.setOnClickListener {
            (activity as MainActivity).popStack()
        }
        return view
    }

    private fun handleReadyButtonClick() {
        readyButton.isEnabled = false
        if (nameEditText.text == null || nameEditText.text.toString().trim() == "") {
            nameEditText.error = "Введите имя"
            readyButton.isEnabled = true
            return
        }

        if (surnameEditText.text == null || surnameEditText.text.toString().trim() == "") {
            surnameEditText.error = "Введите фамилию"
            readyButton.isEnabled = true
            return
        }

        if (birthdayTextView.text == null) {
            birthdayTextView.error = "Введите дату рождения"
            readyButton.isEnabled = true
            return
        }

        if (passwordEditText.text == null || passwordEditText.text.toString().trim() == "") {
            passwordEditText.error = "Введите пароль"
            readyButton.isEnabled = true
            return
        }

        if (confirmPasswordEditText.text == null || confirmPasswordEditText.text.toString()
                .trim() == ""
        ) {
            confirmPasswordEditText.error = "Повторите пароль"
            readyButton.isEnabled = true
            return
        }

        if (passwordEditText.text.toString() != confirmPasswordEditText.text.toString()) {
            confirmPasswordEditText.error = "Пароли не совпадают"
            readyButton.isEnabled = true
            return
        }

        OrganizerApplication.auth.createUserWithEmailAndPassword(
            requireArguments().getString("email")!!,
            passwordEditText.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                OrganizerApplication.auth.signInWithEmailAndPassword(
                    requireArguments().getString("email")!!,
                    passwordEditText.text.toString()
                )
                handleRegistration()
            } else if (task.exception is FirebaseAuthWeakPasswordException) {
                passwordEditText.error = "Пароль должен состоять из хотя бы 6 символов"
                readyButton.isEnabled = true
            } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(
                    activity,
                    "Неверный адрес почты или пароль",
                    Toast.LENGTH_LONG
                ).show()

                readyButton.isEnabled = true
            }
        }
    }

    private fun handleRegistration() {
        val userId = OrganizerApplication.auth.currentUser!!.uid
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val birthday = birthdayTextView.text.toString()

        (activity as MainActivity).getViewModel().saveUser(userId, name, surname, birthday)
        (activity as MainActivity).endRegistration()
    }
}
