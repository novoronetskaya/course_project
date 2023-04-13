@file:Suppress("RedundantVisibilityModifier")

package com.course_project.voronetskaya.view.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.course_project.voronetskaya.data.model.*
import com.course_project.voronetskaya.data.util.ConsumptionType
import com.course_project.voronetskaya.view.activities.TreatmentActivity
import com.course_project.voronetskaya.receivers.NotificationReceiver
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.common.math.IntMath.pow
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class TreatmentsViewModel : ViewModel() {
    private var treatments: MutableLiveData<List<Treatment>> = MutableLiveData()
    private var kitsListData: MutableLiveData<List<FirstAidKit>> = MutableLiveData()
    private val firestore = Firebase.firestore
    private lateinit var savedTreatment: Treatment
    private lateinit var chosenKit: FirstAidKit
    private var kitMedicine: MutableLiveData<List<Medicine>> = MutableLiveData()
    private val executor = Executors.newSingleThreadExecutor()
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")
    private var uid: String

    init {
        if (OrganizerApplication.sharedPreferences.getString("dependentId", "") != "") {
            uid = OrganizerApplication.sharedPreferences.getString("dependentId", "")!!

            if (OrganizerApplication.auth.currentUser != null) {
                uploadTreatments()
                uploadKits()
            }
        } else if (OrganizerApplication.auth.currentUser != null) {
            uid = OrganizerApplication.auth.currentUser!!.uid
            uploadTreatments()
            uploadKits()
        } else {
            uid = OrganizerApplication.sharedPreferences.getString("uid", "")!!
        }
    }

    private fun uploadTreatments() {
        firestore.collection("treatments").whereEqualTo("userId", uid).addSnapshotListener(
            EventListener { value, e ->
                if (e != null) {
                    Log.w("Treatments listener", e)
                    return@EventListener
                }

                val curTreatments: MutableList<Treatment> = mutableListOf()
                for (document in value!!) {
                    curTreatments.add(document.toObject(Treatment::class.java))
                }

                treatments.value = curTreatments
            }
        )
    }

    private fun uploadKits() {
        firestore.collection("kit_users").whereEqualTo("userId", uid).addSnapshotListener(
            EventListener { value, e ->
                if (e != null) {
                    Log.w("Kits list listener", e)
                    return@EventListener
                }

                val kitsList: MutableList<FirstAidKit> = mutableListOf()
                var counter = 0
                for (document in value!!) {
                    val kitUsers = document.toObject(KitUsers::class.java)
                    firestore.collection("kits").whereEqualTo("id", kitUsers.getKitId())
                        .addSnapshotListener { valueKit, _ ->

                            for (doc in valueKit!!) {
                                counter += 1
                                kitsList.add(doc.toObject(FirstAidKit::class.java))
                            }

                            if (counter == value.size()) {
                                kitsListData.value = kitsList
                            }
                        }
                }
            }
        )
    }

    public fun getTreatmentsList(): LiveData<List<Treatment>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.treatments
        }

        return OrganizerApplication.getInstance().getDatabase().treatmentDao()
            .getUserTreatments(uid)
    }

    public fun getUserKits(): LiveData<List<FirstAidKit>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return kitsListData
        }

        return OrganizerApplication.getInstance().getDatabase().firstAidKitDao().getAllKits()
    }

    public fun setKit(kit: FirstAidKit) {
        chosenKit = kit
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("medicine").whereEqualTo("kitId", kit.getId()).addSnapshotListener(
                EventListener { value, e ->
                    if (e != null) {
                        Log.w("Medicine list listener", e)
                        return@EventListener
                    }

                    val medicineList: MutableList<Medicine> = mutableListOf()
                    for (document in value!!) {
                        medicineList.add(document.toObject(Medicine::class.java))
                    }

                    kitMedicine.value = medicineList
                }
            )
        }
    }

    public fun getMedicineFromKit(): LiveData<List<Medicine>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return kitMedicine
        }

        return OrganizerApplication.getInstance().getDatabase().medicineDao()
            .getMedicineFromKit(chosenKit.getId())
    }

    public fun addTreatment(treatment: Treatment) {
        savedTreatment = treatment
        savedTreatment.setUserId(uid)
    }

    public fun addTreatmentTime(time: List<TreatmentTime>, activity: TreatmentActivity) {
        savedTreatment.setId(UUID.randomUUID().toString())
        val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm")

        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatments").add(savedTreatment)
            for (item in time) {
                item.setTreatmentId(savedTreatment.getId())
                item.setId(UUID.randomUUID().toString())
                firestore.collection("treatment_time").add(item)

                val today = dateFormatter.format(Date())
                val power = pow(2, Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

                if (savedTreatment.getStartDate().equals(today) &&
                    (savedTreatment.getConsumptionType() != ConsumptionType.DAYS_OF_WEEK ||
                            power and savedTreatment.getDaysOfWeek() > 0)
                ) {
                    val history = TreatmentHistory()
                    history.setId(UUID.randomUUID().toString())
                    history.setTreatmentId(savedTreatment.getId())
                    history.setDose(item.getDose())

                    val timeHistory =
                        String.format("%s %02d:%02d", today, item.getHour(), item.getMinute())
                    history.setScheduleDate(timeHistory)
                    history.setMovedTo(timeHistory)
                    history.setUserId(uid)

                    firestore.collection("treatment_history").add(history)

                    createNotification(
                        activity,
                        history.getId(),
                        savedTreatment.getMedicineName(),
                        formatter.parse(history.getScheduleDate())!!.time
                    )
                }
            }
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().treatmentDao()
                    .insertTreatment(savedTreatment)
                for (item in time) {
                    item.setId(UUID.randomUUID().toString())
                    item.setTreatmentId(savedTreatment.getId())
                    OrganizerApplication.getInstance().getDatabase().treatmentTimeDao()
                        .addTreatmentTime(item)

                    val today = dateFormatter.format(Date())
                    val power = pow(2, Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

                    if (savedTreatment.getStartDate().equals(today) &&
                        (savedTreatment.getConsumptionType() != ConsumptionType.DAYS_OF_WEEK ||
                                power and savedTreatment.getDaysOfWeek() > 0)
                    ) {
                        val history = TreatmentHistory()
                        history.setTreatmentId(savedTreatment.getId())
                        history.setDose(item.getDose())

                        val timeHistory =
                            String.format("%s %02d:%02d", today, item.getHour(), item.getMinute())
                        history.setMedicineName(savedTreatment.getMedicineName())
                        history.setScheduleDate(timeHistory)
                        history.setMovedTo(timeHistory)
                        history.setUserId(uid)
                        history.setId(UUID.randomUUID().toString())

                        OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                            .addHistory(history)
                        createNotification(
                            activity,
                            history.getId(),
                            savedTreatment.getMedicineName(),
                            formatter.parse(history.getScheduleDate())!!.time
                        )
                    }
                }
            }
        }
    }

    public fun deleteTreatment(treatment: Treatment) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatments").whereEqualTo("id", treatment.getId())
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null) {
                        return@EventListener
                    }
                    for (document in value!!) {
                        document.reference.delete()
                    }
                })
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().treatmentDao()
                    .deleteTreatment(treatment)
            }
        }
    }

    public fun updateTreatment(treatment: Treatment) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatments").whereEqualTo("id", treatment.getId())
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null) {
                        return@EventListener
                    }
                    for (document in value!!) {
                        document.reference.update("isActive", treatment.getIsActive())
                    }
                })
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().treatmentDao()
                    .updateTreatment(treatment)
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
            putExtra("notId", time.toInt())
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
            time,
            pendIntent
        )
    }
}