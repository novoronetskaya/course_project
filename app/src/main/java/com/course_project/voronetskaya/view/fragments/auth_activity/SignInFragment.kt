package com.course_project.voronetskaya.view.fragments.auth_activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.AuthActivity
import com.course_project.voronetskaya.view.OrganizerApplication
import com.course_project.voronetskaya.view.activities.KitActivity
import com.course_project.voronetskaya.view.activities.MainActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInFragment : Fragment() {
    private lateinit var loginTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var forgotPasswordText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sign_in_fragment, container, false)
        val button = view.findViewById<Button>(R.id.b_ready)

        loginTextView = view.findViewById(R.id.et_login)
        passwordTextView = view.findViewById(R.id.et_password)
        forgotPasswordText = view.findViewById(R.id.tv_forgot_password)

        button.setOnClickListener {
            button.isEnabled = false

            if (loginTextView.text == null || loginTextView.text.toString().trim() == "") {
                loginTextView.error = "Введите адрес почты"
                button.isEnabled = true
                return@setOnClickListener
            }

            if (passwordTextView.text == null || passwordTextView.text.toString().trim() == "") {
                passwordTextView.error = "Введите пароль"
                button.isEnabled = true
                return@setOnClickListener
            }

            OrganizerApplication.auth.signInWithEmailAndPassword(
                loginTextView.text.toString(),
                passwordTextView.text.toString()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Navigation.findNavController(activity as AuthActivity, R.id.f_auth)
                            .navigate(R.id.action_signInFragment_to_mainActivity)
                    } else if (task.exception is FirebaseNetworkException) {
                        Toast.makeText(activity, "Проверьте подключение к сети", Toast.LENGTH_LONG)
                            .show()
                        button.isEnabled = true
                    } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            activity,
                            "Неверный адрес почты или пароль",
                            Toast.LENGTH_LONG
                        ).show()

                        button.isEnabled = true
                    }
                }
        }

        forgotPasswordText.setOnClickListener {
            val email = EditText(activity)
            email.hint = "Адрес почты"
            email.textSize = 19F
            AlertDialog.Builder(activity).setTitle("Сброс пароля").setView(email)
                .setPositiveButton("Готово") { _, _ ->
                    if (email.text == null || email.text.toString().trim() == "") {
                        email.error = "Введите название аптечки"
                    } else {
                        OrganizerApplication.auth.sendPasswordResetEmail(email.text.toString())
                    }
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                }.show()
        }
        return view
    }
}