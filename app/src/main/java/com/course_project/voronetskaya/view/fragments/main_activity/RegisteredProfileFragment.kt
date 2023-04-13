package com.course_project.voronetskaya.view.fragments.main_activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.User
import com.course_project.voronetskaya.view.activities.MainActivity
import com.course_project.voronetskaya.view.OrganizerApplication

class RegisteredProfileFragment : Fragment() {
    private lateinit var saveButton: Button
    private lateinit var logOutButton: Button
    private lateinit var deleteTextView: TextView
    private lateinit var changeEmailTextView: TextView
    private lateinit var changePasswordTextView: TextView

    private lateinit var userId: TextView
    private lateinit var userName: TextView
    private lateinit var userSurname: TextView
    private lateinit var birthday: TextView
    private lateinit var email: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.registered_profile_fragment, container, false)

        saveButton = view.findViewById(R.id.b_save)
        logOutButton = view.findViewById(R.id.b_log_out)
        deleteTextView = view.findViewById(R.id.tv_delete_account)
        changeEmailTextView = view.findViewById(R.id.tv_change_email)
        changePasswordTextView = view.findViewById(R.id.tv_change_password)

        userId = view.findViewById(R.id.tv_user_id)
        userName = view.findViewById(R.id.tv_user_name)
        userSurname = view.findViewById(R.id.tv_user_surname)
        birthday = view.findViewById(R.id.tv_birth_date)
        email = view.findViewById(R.id.tv_email)

        (activity as MainActivity).getViewModel().getUser().observe(viewLifecycleOwner) { user ->
            setData(user)
        }

        logOutButton.setOnClickListener {
            AlertDialog.Builder(activity).setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да") { _, _ ->
                    (activity as MainActivity).logOut()
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                }.create().show()
        }

        deleteTextView.setOnClickListener {
            AlertDialog.Builder(activity).setTitle("Удаление аккаунта")
                .setMessage("Вы уверены, что хотите удалить аккаунт?")
                .setPositiveButton("Да") { _, _ ->
                    (activity as MainActivity).deleteAccount()
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                }.create().show()
        }

        changeEmailTextView.setOnClickListener {
            (activity as MainActivity).changeEmail()
        }

        changePasswordTextView.setOnClickListener {
            OrganizerApplication.auth.sendPasswordResetEmail(OrganizerApplication.auth.currentUser!!.email!!)
        }

        saveButton.setOnClickListener {
            if (userName.text.toString().trim() != "") {
                userName.error = "Введите имя"
            }

            if (userSurname.text.toString().trim() != "") {
                userSurname.error = "Введите фамилию"
            }

            if (birthday.text.toString().trim() != "") {
                birthday.error = "Введите дату рождения"
            }

            (activity as MainActivity).getViewModel().updateUser(
                userId.text.toString(),
                userName.text.toString(),
                userSurname.text.toString(),
                birthday.text.toString()
            )
        }
        return view
    }

    private fun setData(user: User) {
        userId.text = user.getId()
        userName.text = user.getFirstName()
        userSurname.text = user.getSurname()
        birthday.text = user.getBirthday()
        email.text = OrganizerApplication.auth.currentUser?.email
    }
}