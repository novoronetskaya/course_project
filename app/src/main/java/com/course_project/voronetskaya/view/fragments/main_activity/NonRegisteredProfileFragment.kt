package com.course_project.voronetskaya.view.fragments.main_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.MainActivity

class NonRegisteredProfileFragment : Fragment() {
    private lateinit var signUpButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.non_registered_profile_fragment, container, false)
        signUpButton = view.findViewById(R.id.b_sign_up)
        signUpButton.text = "Регистрация"
        signUpButton.setOnClickListener {
            (activity as MainActivity).registerUser()
        }

        return view
    }
}