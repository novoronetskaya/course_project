package com.course_project.voronetskaya.view.fragments.kit_activity

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

class InviteUserFragment : Fragment() {
    private lateinit var userId: EditText
    private lateinit var userMessage: EditText
    private lateinit var readyButton: Button
    private lateinit var cancelButton: Button
    private lateinit var kitName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.invite_user_fragment, container, false)

        userId = view.findViewById(R.id.et_user_id)
        userMessage = view.findViewById(R.id.et_user_message)
        readyButton = view.findViewById(R.id.b_ready)
        cancelButton = view.findViewById(R.id.b_cancel)
        kitName = view.findViewById(R.id.tv_kit_name)

        kitName.text = requireArguments().getString("kitName")

        readyButton.setOnClickListener {
            if (userId.text == null || userId.text.toString().trim() == "") {
                userId.error = "Введите ID пользователя"
                return@setOnClickListener
            }

            (activity as KitActivity).sendInvitation(userId.text.toString(), userMessage.text.toString())
        }

        cancelButton.setOnClickListener {
            (activity as KitActivity).popStack()
        }
        return view
    }
}