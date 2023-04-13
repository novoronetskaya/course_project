package com.course_project.voronetskaya.view.fragments.main_activity

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

class SignUpCodeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.registration_enter_code_fragment, container, false)
        val nextButton = view.findViewById<Button>(R.id.b_next)
        val codeEditText = view.findViewById<TextView>(R.id.et_enter_code)
        val emailTextView = view.findViewById<TextView>(R.id.tv_entered_email)

        emailTextView.hint = requireArguments().getString("email")

        nextButton.setOnClickListener {
            nextButton.isEnabled = false

            if (codeEditText.text == null) {
                Toast.makeText(activity, "Введите код подтверждения", Toast.LENGTH_SHORT).show()
                nextButton.isEnabled = true
                return@setOnClickListener
            }

            val code = requireArguments().getString("code")
            if (code != getString(R.string.email_registered) && codeEditText.text.toString().equals(code)) {
                (activity as MainActivity).showEnterInfoFragment()
            } else {
                Toast.makeText(activity, "Неверный код подтверждения", Toast.LENGTH_SHORT).show()
                nextButton.isEnabled = true
                return@setOnClickListener
            }
        }

        val backButton = view.findViewById<Button>(R.id.b_back)
        backButton.setOnClickListener {
            (activity as MainActivity).popStack()
        }
        return view
    }
}