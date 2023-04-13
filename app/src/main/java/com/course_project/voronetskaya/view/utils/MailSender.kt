package com.course_project.voronetskaya.view.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailSender {
    companion object {
        public fun generate(): String {
            val array = Array(6) { (0..9).random() }
            return array.joinToString("")
        }

        public suspend fun sendSignUpCode(email: String, message: String) {
            withContext(Dispatchers.IO) {
                val props = Properties()
                props["mail.smtp.host"] = "smtp.yandex.ru"
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.port"] = "465"
                props["mail.smtp.socketFactory.port"] = "465"
                props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"

                val session =
                    Session.getDefaultInstance(props, object : javax.mail.Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication("medicine-organizer@yandex.ru", "")
                        }
                    })

                val mime = MimeMessage(session)
                mime.sender = InternetAddress("medicine-organizer@yandex.ru")
                mime.setRecipients(Message.RecipientType.TO, email)
                mime.subject = "Регистрация в Органайзере Лекарств"
                mime.setText(message)

                Transport.send(mime)
            }
        }
    }
}
