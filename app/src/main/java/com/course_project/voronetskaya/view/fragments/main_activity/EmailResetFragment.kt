package com.course_project.voronetskaya.view.fragments.main_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.MainActivity
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.firebase.auth.EmailAuthProvider

class EmailResetFragment : Fragment() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var readyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.email_reset_fragment, container, false)
        emailEditText = view.findViewById(R.id.et_email)
        passwordEditText = view.findViewById(R.id.et_password)
        cancelButton = view.findViewById(R.id.b_cancel)
        readyButton = view.findViewById(R.id.b_ready)

        cancelButton.setOnClickListener {
            (activity as MainActivity).popStack()
        }

        readyButton.setOnClickListener {
            readyButton.isEnabled = false
            if (emailEditText.text == null || emailEditText.text.toString().trim() == "") {
                emailEditText.error = "Введите новую почту"
                readyButton.isEnabled = true
                return@setOnClickListener
            }

            if (passwordEditText.text == null || passwordEditText.text.toString().trim() == "") {
                passwordEditText.error = "Введите пароль"
                readyButton.isEnabled = true
                return@setOnClickListener
            }

            val currentUser = OrganizerApplication.auth.currentUser
            val credentials = EmailAuthProvider.getCredential(
                currentUser!!.email!!,
                passwordEditText.text.toString()
            )

            currentUser.reauthenticate(credentials).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUser.updateEmail(emailEditText.text.toString())
                        .addOnCompleteListener { update ->
                            if (update.isSuccessful) {
                                (activity as MainActivity).popStack()
                                Toast.makeText(activity, "Почта успешно сменена", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Ошибка. Проверьте корректность адреса почты. ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                readyButton.isEnabled = true
                            }
                        }
                } else {
                    Toast.makeText(activity, "Ошибка. Проверьте пароль.", Toast.LENGTH_SHORT).show()
                    readyButton.isEnabled = true
                }
            }
        }
        return view
    }
}