package com.course_project.voronetskaya.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.course_project.voronetskaya.data.model.Treatment
import com.course_project.voronetskaya.data.model.TreatmentHistory
import com.course_project.voronetskaya.data.model.TreatmentTime
import com.course_project.voronetskaya.data.util.ConsumptionType
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.common.math.IntMath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class UploadBroadcastReceiver : BroadcastReceiver() {
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")
    private val dateTimeFormatter = SimpleDateFormat("yyyy.MM.dd HH:mm")
    private var counter = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        uploadTreatmentHistory(context!!)

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, UploadBroadcastReceiver::class.java)
        val pendIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val curDate = Calendar.getInstance()
        curDate.add(Calendar.DAY_OF_MONTH, 1)
        curDate.set(Calendar.HOUR_OF_DAY, 0)
        curDate.set(Calendar.MINUTE, 3)

        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            curDate.timeInMillis,
            pendIntent
        )
    }

    private fun uploadTreatmentHistory(context: Context) {
        val calendar = Calendar.getInstance()
        val curDate = Date()
        val firestore = Firebase.firestore
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatments")
                .whereEqualTo("userId", OrganizerApplication.auth.currentUser!!.uid)
                .whereEqualTo("isActive", true)
                .get().addOnSuccessListener { value ->
                    for (item in value) {
                        val treatment = item.toObject(Treatment::class.java)
                        if (isTreatmentTrueForDate(curDate, treatment)) {
                            firestore.collection("treatment_time")
                                .whereEqualTo("treatmentId", treatment.getId()).get()
                                .addOnSuccessListener { times ->
                                    for (time in times) {
                                        val timeObj = time.toObject(TreatmentTime::class.java)
                                        val treatmentHistory = TreatmentHistory()
                                        treatmentHistory.setDose(timeObj.getDose())
                                        treatmentHistory.setTreatmentId(treatment.getId())
                                        treatmentHistory.setId(UUID.randomUUID().toString())

                                        val dateString = String.format(
                                            "%d.%02d.%02d %02d:%02d",
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH) + 1,
                                            calendar.get(Calendar.DAY_OF_MONTH),
                                            timeObj.getHour(),
                                            timeObj.getMinute()
                                        )

                                        treatmentHistory.setScheduleDate(dateString)
                                        treatmentHistory.setMovedTo(dateString)
                                        treatmentHistory.setUserId(OrganizerApplication.auth.currentUser!!.uid)
                                        firestore.collection("treatment_history")
                                            .add(treatmentHistory)

                                        createNotification(
                                            context,
                                            treatmentHistory.getId(),
                                            treatment.getMedicineName(),
                                            dateTimeFormatter.parse(dateString)!!.time
                                        )
                                    }
                                }
                        }
                    }

                }
        } else {
            val executor = Executors.newSingleThreadExecutor()

            executor.execute {
                val user =
                    OrganizerApplication.getInstance().getDatabase().userDao().getIndependentUser()
                val treatments = OrganizerApplication.getInstance().getDatabase().treatmentDao()
                    .getUserActiveTreatments(user.getId())

                for (item in treatments) {
                    if (isTreatmentTrueForDate(curDate, item)) {
                        val times =
                            OrganizerApplication.getInstance().getDatabase().treatmentTimeDao()
                                .getTimeForTreatment(item.getId())

                        for (time in times) {
                            val history = TreatmentHistory()
                            history.setTreatmentId(item.getId())
                            history.setDose(time.getDose())

                            val dateToTake = String.format(
                                "%d.%02d.%02d %02d:%02d",
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1,
                                calendar.get(Calendar.DAY_OF_MONTH),
                                time.getHour(),
                                time.getMinute()
                            )
                            history.setMedicineName(item.getMedicineName())
                            history.setScheduleDate(dateToTake)
                            history.setMovedTo(dateToTake)
                            history.setId(UUID.randomUUID().toString())
                            history.setUserId(user.getId())
                            OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                                .addHistory(history)

                            createNotification(
                                context,
                                history.getId(),
                                item.getMedicineName(),
                                dateTimeFormatter.parse(dateToTake)!!.time
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createNotification(
        context: Context,
        treatId: String,
        treatName: String,
        time: Long
    ) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("treatId", treatId)
            putExtra("notId", counter)
            putExtra("treatName", treatName)
        }

        counter += 1
        val pendIntent =
            PendingIntent.getBroadcast(
                context,
                Date().time.toInt(),
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendIntent
        )
    }

    private fun isTreatmentTrueForDate(date: Date, treatment: Treatment): Boolean {
        val startDate = dateFormatter.parse(treatment.getStartDate())!!
        if (date < startDate) {
            return false
        }

        if ((date.time - startDate.time) / (1000 * 60 * 60 * 24) >= treatment.getLength()) {
            return false
        }

        if (treatment.getConsumptionType() == ConsumptionType.EVERY_DAY) {
            return true
        }

        if (treatment.getConsumptionType() == ConsumptionType.EACH_N_DAYS) {
            return (date.time - startDate.time) / (1000 * 60 * 60 * 24) % treatment.getEachNDays() == 0L
        }

        if (treatment.getConsumptionType() == ConsumptionType.DAYS_OF_WEEK) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date.time
            return treatment.getDaysOfWeek() and IntMath.pow(
                2,
                calendar.get(Calendar.DAY_OF_WEEK) - 1
            ) > 0
        }

        val active = treatment.getConsumptionPeriod()
        val pause = treatment.getHaltPeriod()
        return (date.time - startDate.time) / (1000 * 60 * 60 * 24) % (active + pause) < active
    }
}