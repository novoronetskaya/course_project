package com.course_project.voronetskaya.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.course_project.voronetskaya.R
import com.course_project.voronetskaya.data.model.Medicine
import com.course_project.voronetskaya.data.model.Treatment
import com.course_project.voronetskaya.data.model.TreatmentHistory
import com.course_project.voronetskaya.data.util.Status
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Double.max
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class NotificationResultReceiver : BroadcastReceiver() {
    private val firestore = Firebase.firestore
    private val executor = Executors.newSingleThreadExecutor()
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd HH:mm")

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }

        if (intent == null) {
            return
        }

        val result = intent.extras?.getString("Result") ?: return
        val notId = intent.extras?.getInt("notId") ?: return
        val treatId = intent.extras?.getString("treatId") ?: return
        val treatName = intent.extras?.getString("treatName") ?: return

        val manager = NotificationManagerCompat.from(context)
        manager.cancel(notId)

        if (result.equals("TAKEN")) {
            setStatus(treatId, Status.TAKEN)
            reduceMedicine(treatId, context)
        } else if (result.equals("MISSED")) {
            setStatus(treatId, Status.CANCELLED)
        } else if (result.equals("POSTPONED")) {
            setStatus(treatId, Status.MOVED)
            postponeNotification(treatId, context, treatName, notId)
        }
    }

    private fun reduceMedicine(treatId: String, context: Context) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatment_history").whereEqualTo("id", treatId).get()
                .addOnSuccessListener { result ->
                    val treatmentHistory =
                        result.documents[0].toObject(TreatmentHistory::class.java)
                    firestore.collection("treatments")
                        .whereEqualTo("id", treatmentHistory!!.getTreatmentId()).get()
                        .addOnSuccessListener { treat ->
                            val treatment = treat.documents[0].toObject(Treatment::class.java)
                            firestore.collection("medicine")
                                .whereEqualTo("id", treatment!!.getMedicineId()).get()
                                .addOnSuccessListener { med ->
                                    val medicine = med.documents[0].toObject(Medicine::class.java)!!
                                    val newAmount =
                                        max(0.0, medicine.getAmount() - treatmentHistory.getDose())
                                    if (newAmount <= medicine.getNotificationRemains()) {
                                        sendMedicineNotification(medicine.getName(), context)
                                    }

                                    firestore.collection("medicine")
                                        .whereEqualTo("id", medicine.getId()).get()
                                        .addOnSuccessListener { values ->
                                            for (item in values) {
                                                item.reference.update("amount", newAmount)
                                            }
                                        }
                                }
                        }
                }
        } else {
            executor.execute {
                val history = OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                    .getHistoryWithId(treatId)
                val treatment = OrganizerApplication.getInstance().getDatabase().treatmentDao()
                    .getTreatmentWithId(history.getTreatmentId())
                val medicine = OrganizerApplication.getInstance().getDatabase().medicineDao()
                    .getMedicineById(treatment.getMedicineId())

                medicine.setAmount(max(0.0, medicine.getAmount() - history.getDose()))
                OrganizerApplication.getInstance().getDatabase().medicineDao()
                    .updateMedicine(medicine)

                if (medicine.getAmount() <= medicine.getNotificationRemains()) {
                    sendMedicineNotification(medicine.getName(), context)
                }
            }
        }
    }

    private fun sendMedicineNotification(medName: String, context: Context) {
        val notification = NotificationCompat.Builder(context, "1")
            .setContentText(
                String.format(
                    "Препарат %s заканчивается! ",
                    medName
                )
            )
            .setSmallIcon(R.drawable.auth_screen_icon)
            .setContentTitle("Органайзер лекарств")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
        val manager = NotificationManagerCompat.from(context)
        manager.notify(-1, notification.build())
    }

    private fun setStatus(treatId: String, status: Status) {
        if (OrganizerApplication.auth.currentUser != null) {
            val data = HashMap<String, Any>()
            data["status"] = status.toString()
            if (status == Status.TAKEN) {
                data["takenAt"] = dateFormatter.format(Date())
            }

            firestore.collection("treatment_history").whereEqualTo("id", treatId).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.update(data)
                    }
                }
        } else {
            executor.execute {
                val treatHistory =
                    OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                        .getHistoryWithId(treatId)
                treatHistory.setStatus(status)
                if (status == Status.TAKEN) {
                    treatHistory.setTakenAt(dateFormatter.format(Date()))
                }

                OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                    .updateHistory(treatHistory)
            }
        }
    }

    private fun postponeNotification(
        treatId: String,
        context: Context,
        treatName: String,
        notId: Int
    ) {
        val newTime = Date().time + 1000 * 60 * 30

        val timeString = dateFormatter.format(Date(newTime))

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("treatId", treatId)
            putExtra("notId", notId)
            putExtra("treatName", treatName)
        }

        val pendIntent =
            PendingIntent.getBroadcast(
                context,
                Date().time.toInt(),
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            newTime,
            pendIntent
        )

        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatment_history").whereEqualTo("id", treatId).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.update("movedTo", timeString)
                    }
                }
        } else {
            executor.execute {
                val history = OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                    .getHistoryWithId(treatId)
                history.setMovedTo(timeString)
                OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                    .updateHistory(history)
            }
        }
    }
}