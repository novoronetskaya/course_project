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
import com.course_project.voronetskaya.view.activities.MainActivity
import com.course_project.voronetskaya.view.utils.MailSender
import com.course_project.voronetskaya.view.OrganizerApplication
import kotlinx.coroutines.*

class SignUpEmailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.registration_enter_email_fragment, container, false)

        val buttonNext = view.findViewById<Button>(R.id.b_next)
        val mailEditText = view.findViewById<TextView>(R.id.et_enter_email)

        val buttonBack = view.findViewById<Button>(R.id.b_cancel)
        buttonBack.setOnClickListener {
            (activity as MainActivity).popStack()
        }

        buttonNext.setOnClickListener {
            buttonNext.isEnabled = false

            if (mailEditText.text == null || mailEditText.text.toString().trim() == "") {
                mailEditText.error = "Введите адрес почты"
                buttonNext.isEnabled = true
                return@setOnClickListener
            }

            val email = mailEditText.text.toString()
            var isRegistered = false

            OrganizerApplication.auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isRegistered = task.result.signInMethods!!.isNotEmpty()
                    } else {
                        AlertDialog.Builder(activity).setTitle("Ошибка")
                            .setMessage("Проверьте введённый адрес почты и подключение к сети и попробуйте снова.")
                            .create().show()
                        buttonNext.isEnabled = true
                        return@addOnCompleteListener
                    }
                }

            if (buttonNext.isEnabled) {
                return@setOnClickListener
            }

            var code = getString(R.string.email_registered)
            val isEmailSent: Boolean = kotlin.runCatching {
                runBlocking {
                    launch {
                        val message = if (isRegistered) {
                            "Кто-то (возможно, Вы) пытается зарегистрировать новый аккаунт в Органайзере Лекарств, " +
                                    "несмотря на то, что уже существует привязанный к этому адресу почты аккаунт. " +
                                    "Если это были не Вы, то просто игнорируйте это письмо. "
                        } else {
                            code = MailSender.generate()
                            String.format(
                                "Добро пожаловать в Органайзер лекарств!\n" +
                                        "Для завершения регистрации введите код подтверждения в приложении: \n %s",
                                code
                            )
                        }

                        MailSender.sendSignUpCode(
                            email, message
                        )
                    }
                }
            }.isSuccess

            if (isEmailSent) {
                (activity as MainActivity).setCodePreferences(code)
                (activity as MainActivity).showEnterCodeFragment(mailEditText.text.toString())
                (activity as MainActivity).getViewModel().saveSignUpEmail(mailEditText.text.toString())
            } else {
                AlertDialog.Builder(activity).setTitle("Ошибка")
                    .setMessage("Не удалось отправить код. Проверьте введённый адрес почты и подключение к сети и попробуйте снова.")
                    .create().show()
                buttonNext.isEnabled = true
            }
        }
        return view
    }
}