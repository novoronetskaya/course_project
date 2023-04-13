package com.course_project.voronetskaya.view.fragments.auth_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.view.activities.AuthActivity

class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.welcome_fragment, container, false)
        val signInView = view.findViewById<TextView>(R.id.tv_sign_in)
        signInView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_welcomeFragment_to_signInFragment))

        val signUpButton = view.findViewById<Button>(R.id.b_sign_up)
        signUpButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_welcomeFragment_to_mainActivity,
                bundleOf("signUp" to "signUp")
            )
        )

        val guestSignInButton = view.findViewById<Button>(R.id.b_guest)
        guestSignInButton.setOnClickListener {
            (activity as AuthActivity).signInAsGuest()
        }
        return view
    }
}