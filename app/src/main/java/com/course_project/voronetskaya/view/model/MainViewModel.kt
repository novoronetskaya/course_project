@file:Suppress("RedundantVisibilityModifier")

package com.course_project.voronetskaya.view.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.course_project.voronetskaya.data.model.*
import com.course_project.voronetskaya.data.util.ConsumptionType
import com.course_project.voronetskaya.data.util.Status
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.android.gms.tasks.Task
import com.google.common.math.IntMath.pow
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Double.max
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.HashMap

class MainViewModel : ViewModel() {
    companion object {
        private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")
    }

    private var uid: String = ""
    private var notEnoughMedicine: Boolean = false
    private lateinit var signUpEmail: String
    private var treatmentWithTime: TreatmentWithTime = TreatmentWithTime(Treatment(), "", 1.0)
    private val firestore = Firebase.firestore
    private var user: MutableLiveData<User> = MutableLiveData<User>(User())
    private var todayTreatments: MutableLiveData<List<TreatmentHistoryAndTreatment>> =
        MutableLiveData<List<TreatmentHistoryAndTreatment>>(
            listOf()
        )
    private var treatments: MutableLiveData<List<TreatmentWithTime>> =
        MutableLiveData<List<TreatmentWithTime>>(
            listOf()
        )
    private val executor = Executors.newSingleThreadExecutor()
    private var date: Date = Date()

    init {
        uid = if (OrganizerApplication.sharedPreferences.getString("dependentId", "") != "") {
            OrganizerApplication.sharedPreferences.getString("dependentId", "")!!
        } else if (OrganizerApplication.auth.currentUser != null) {
            OrganizerApplication.auth.currentUser!!.uid
        } else {
            OrganizerApplication.sharedPreferences.getString("uid", "")!!
        }

        uploadTreatmentsForToday()
    }

    public fun setId(uid: String) {
        this.uid = uid
    }

    public fun setUser(uid: String) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("users").whereEqualTo("id", uid)
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null) {
                        Log.w("User listener", e)
                        return@EventListener
                    }

                    for (document in value!!) {
                        user.value = document.toObject(User::class.java)
                    }
                })
        }
    }

    private fun uploadTreatmentsForToday() {
        if (OrganizerApplication.auth.currentUser != null) {
            val today = dateFormatter.format(Date())
            val tomorrow = dateFormatter.format(Date(Date().time + 1000 * 60 * 60 * 24))

            firestore.collection("treatment_history")
                .whereEqualTo("userId", uid)
                .whereGreaterThanOrEqualTo("movedTo", today)
                .whereLessThan("movedTo", tomorrow)
                .addSnapshotListener(
                    EventListener { value, e ->
                        if (e != null) {
                            Log.w("Today treatments listener", e)
                            return@EventListener
                        }

                        val todayList: MutableList<TreatmentHistoryAndTreatment> = mutableListOf()

                        for (document in value!!) {
                            val treatmentHistory = document.toObject(TreatmentHistory::class.java)

                            if (treatmentHistory.getStatus() == Status.CANCELLED || treatmentHistory.getStatus() == Status.TAKEN) {
                                continue
                            }

                            firestore.collection("treatments")
                                .whereEqualTo("id", treatmentHistory.getTreatmentId()).get()
                                .addOnSuccessListener { result ->
                                    for (treatment in result) {
                                        val treatmentHistoryAndTreatment =
                                            TreatmentHistoryAndTreatment(
                                                treatment.toObject(Treatment::class.java),
                                                treatmentHistory
                                            )
                                        todayList.add(treatmentHistoryAndTreatment)
                                    }

                                    if (document.equals(value.last())) {
                                        todayTreatments.value = todayList
                                    }
                                }
                        }
                    }
                )
        } else {
            val calendar = Date()
            val today = dateFormatter.format(calendar)

            OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                .getForDateLive(String.format("%s%%", today), uid)
                .observeForever { treatmentHistory ->

                    executor.execute {
                        val todayList: MutableList<TreatmentHistoryAndTreatment> = mutableListOf()
                        for (item in treatmentHistory) {
                            val treatment =
                                OrganizerApplication.getInstance().getDatabase().treatmentDao()
                                    .getTreatmentWithId(item.getTreatmentId())
                            todayList.add(
                                TreatmentHistoryAndTreatment(
                                    treatment ?: Treatment(),
                                    item
                                )
                            )
                        }

                        todayTreatments.postValue(todayList)
                    }

                }
        }
    }

    public fun setDate(date: Date) {
        this.date = date
        uploadTreatments(date)
    }

    public fun getTreatmentsForToday(): LiveData<List<TreatmentHistoryAndTreatment>> {
        return this.todayTreatments
    }

    public fun getTreatmentsForDate(): LiveData<List<TreatmentWithTime>> {
        return this.treatments
    }

    public fun getUser(): LiveData<User> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.user
        }
        return OrganizerApplication.getInstance().getDatabase().userDao().getUserWithId(uid)
    }

    public fun saveSignUpEmail(email: String) {
        this.signUpEmail = email
    }

    public fun getSignUpEmail(): String {
        return this.signUpEmail
    }

    public fun saveUser(
        userId: String,
        name: String,
        surname: String,
        birthday: String
    ) {
        val data = HashMap<String, Any>()
        data["id"] = userId
        data["firstName"] = name
        data["surname"] = surname
        data["birthday"] = birthday
        data["isIndependent"] = true

        firestore.collection("users").add(data)
    }

    public fun updateUser(
        userId: String,
        name: String,
        surname: String,
        birthday: String
    ) {
        val data = HashMap<String, Any>()
        data["firstName"] = name
        data["surname"] = surname
        data["birthday"] = birthday
        data["isIndependent"] = true

        firestore.collection("users").whereEqualTo("id", userId).get()
        .addOnSuccessListener { result ->
            for (document in result) {
                document.reference.update(data)
            }
        }
    }

    public fun synchronizeData() {
        val oldUid = OrganizerApplication.sharedPreferences.getString("uid", "")
        val newUid = OrganizerApplication.auth.currentUser!!.uid

        executor.execute {
            for (item in OrganizerApplication.getInstance().getDatabase().firstAidKitDao()
                .getAll()) {
                if (item.getAdminId().equals(oldUid)) {
                    item.setAdminId(newUid)
                }
                firestore.collection("kits").add(item)
            }

            for (item in OrganizerApplication.getInstance().getDatabase().kitUsersDao().getAll()) {
                if (item.getUserId().equals(oldUid)) {
                    item.setUserId(newUid)
                }
                firestore.collection("kit_users").add(item)
            }

            for (item in OrganizerApplication.getInstance().getDatabase().medicineDao().getAll()) {
                firestore.collection("medicine").add(item)
            }

            for (item in OrganizerApplication.getInstance().getDatabase().medicineRefillsDao()
                .getAll()) {
                if (item.getUserId().equals(oldUid)) {
                    item.setUserId(newUid)
                }
                firestore.collection("medicine_refills").add(item)
            }

            for (item in OrganizerApplication.getInstance().getDatabase().treatmentDao().getAll()) {
                if (item.getUserId().equals(oldUid)) {
                    item.setUserId(newUid)
                }
                firestore.collection("treatments").add(item)
            }

            for (item in OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                .getAll()) {
                if (item.getUserId().equals(oldUid)) {
                    item.setUserId(newUid)
                }
                firestore.collection("treatment_history").add(item)
            }

            for (item in OrganizerApplication.getInstance().getDatabase().treatmentTimeDao()
                .getAll()) {
                firestore.collection("treatment_time").add(item)
            }

            for (item in OrganizerApplication.getInstance().getDatabase().userDao().getAll()) {
                if (!item.getId().equals(oldUid)) {
                    firestore.collection("users").add(item)
                }
            }
        }
    }

    public fun deleteAccount(uid: String) {
        OrganizerApplication.getInstance().deleteAccount(uid)
    }

    private fun uploadTreatments(date: Date) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatments").whereEqualTo("userId", user.value!!.getId())
                .whereEqualTo("isActive", true).addSnapshotListener(
                    EventListener<QuerySnapshot> { value, e ->
                        if (e != null) {
                            Log.w("Date treatments listener", e)
                            return@EventListener
                        }

                        val dateList: MutableList<TreatmentWithTime> = mutableListOf()
                        for (document in value!!) {
                            val treatment = document.toObject(Treatment::class.java)

                            if (isTreatmentTrueForDate(date, treatment)) {
                                firestore.collection("treatment_time")
                                    .whereEqualTo("treatmentId", treatment.getId())
                                    .addSnapshotListener { valueTime, eTime ->
                                        if (eTime != null) {
                                            Log.w("Treatments time listener", eTime)
                                        } else {
                                            for (time in valueTime!!) {
                                                val trTime =
                                                    time.toObject(TreatmentTime::class.java)
                                                val stringTime = String.format(
                                                    "%02d:%02d",
                                                    trTime.getHour(),
                                                    trTime.getMinute()
                                                )
                                                dateList.add(
                                                    TreatmentWithTime(
                                                        treatment,
                                                        stringTime,
                                                        trTime.getDose()
                                                    )
                                                )
                                            }
                                        }
                                    }
                            }
                        }
                        val curDate = dateFormatter.format(date)
                        val nextDate = Date(date.time + 1000 * 60 * 60 * 24)
                        val nextDay = dateFormatter.format(nextDate)

                        firestore.collection("treatment_history")
                            .whereEqualTo("userId", user.value!!.getId())
                            .whereGreaterThanOrEqualTo("movedTo", curDate)
                            .whereLessThan("movedTo", nextDay)
                            .addSnapshotListener { vPostponed, _ ->
                                for (document in vPostponed ?: listOf()) {
                                    val treatmentHistory =
                                        document.toObject(TreatmentHistory::class.java)

                                    if (treatmentHistory.getStatus() == Status.CANCELLED || treatmentHistory.getStatus() == Status.TAKEN) {
                                        continue
                                    }

                                    firestore.collection("treatment")
                                        .whereEqualTo("id", treatmentHistory.getTreatmentId())
                                        .get()
                                        .addOnSuccessListener { result ->

                                            for (treatment in result) {
                                                val treatmentHistoryAndTreatment =
                                                    TreatmentHistoryAndTreatment(
                                                        treatment.toObject(Treatment::class.java),
                                                        treatmentHistory
                                                    )
                                                dateList.add(treatmentHistoryAndTreatment)
                                            }

                                            if (document.equals(vPostponed?.last())) {
                                                treatments.value = dateList
                                            }
                                        }
                                }
                                treatments.value = dateList
                            }
                    }
                )
        } else {
            OrganizerApplication.getInstance().getDatabase().treatmentDao()
                .getUserActiveTreatmentsLive(uid).observeForever { list ->
                    executor.execute {
                        val dateList: MutableList<TreatmentWithTime> = mutableListOf()
                        for (item in list) {
                            if (isTreatmentTrueForDate(date, item)) {
                                val times = OrganizerApplication.getInstance().getDatabase()
                                    .treatmentTimeDao().getTimeForTreatment(item.getId())
                                for (time in times) {
                                    val timeString = String.format(
                                        "%02d:%02d",
                                        time.getHour(),
                                        time.getMinute()
                                    )
                                    dateList.add(
                                        TreatmentWithTime(
                                            item,
                                            timeString,
                                            time.getDose()
                                        )
                                    )
                                }
                            }
                        }

                        val dateString = dateFormatter.format(date)
                        val treatmentHistory =
                            OrganizerApplication.getInstance().getDatabase()
                                .treatmentHistoryDao()
                                .getForDate(String.format("%s%%", dateString), uid)
                        for (item in treatmentHistory) {
                            val tr = OrganizerApplication.getInstance().getDatabase()
                                .treatmentDao()
                                .getTreatmentWithId(item.getTreatmentId())
                            dateList.add(TreatmentHistoryAndTreatment(tr, item))
                        }

                        treatments.postValue(dateList)
                    }
                }
        }
    }

    private fun isTreatmentTrueForDate(date: Date, treatment: Treatment): Boolean {
        if (treatment.getConsumptionType() == ConsumptionType.EVERY_DAY) {
            return true
        }

        val startDate = dateFormatter.parse(treatment.getStartDate())!!
        if (treatment.getConsumptionType() == ConsumptionType.EACH_N_DAYS) {
            return (date.time - startDate.time) / (1000 * 60 * 60 * 24) % treatment.getEachNDays() == 0L
        }

        if (treatment.getConsumptionType() == ConsumptionType.DAYS_OF_WEEK) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date.time
            return treatment.getDaysOfWeek() and pow(2, calendar.get(Calendar.DAY_OF_WEEK) - 1) > 0
        }

        val active = treatment.getConsumptionPeriod()
        val pause = treatment.getHaltPeriod()
        return (date.time - startDate.time) / (1000 * 60 * 60 * 24) % (active + pause) < active
    }

    public fun setTreatmentWithTime(treatmentWithTime: TreatmentWithTime) {
        this.treatmentWithTime = treatmentWithTime
    }

    public fun getTreatmentWithTime(): TreatmentWithTime {
        return this.treatmentWithTime
    }

    public fun updateHistoryStatus(
        treatment: TreatmentHistory,
        status: Status,
        date: String = ""
    ) {
        if (OrganizerApplication.auth.currentUser != null) {
            val data = HashMap<String, Any>()
            data["status"] = status.toString()
            if (status == Status.TAKEN) {
                data["takenAt"] = date
            }

            firestore.collection("treatment_history").whereEqualTo("id", treatment.getId())
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        document.reference.update(data)
                    }
                }
        } else {
            executor.execute {
                treatment.setStatus(status)
                if (status == Status.TAKEN) {
                    treatment.setTakenAt(date)
                }

                OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                    .updateHistory(treatment)
            }
        }
    }

    public fun reduceMedicine(medicineId: String, dose: Double) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("medicine").whereEqualTo("id", medicineId).get()
                .addOnSuccessListener { result ->
                    var medicine = Medicine()
                    for (document in result) {
                        medicine = document.toObject(Medicine::class.java)
                    }

                    val newAmount = max(medicine.getAmount() - dose, 0.0)
                    if (newAmount <= medicine.getNotificationRemains()) {
                        notEnoughMedicine = true
                    }

                    firestore.collection("medicine").whereEqualTo("id", medicineId).get()
                        .addOnSuccessListener { values ->
                            for (document in values) {
                                document.reference.update("amount", newAmount)
                            }
                        }
                }
        } else {
            executor.execute {
                val medicine = OrganizerApplication.getInstance().getDatabase().medicineDao()
                    .getMedicineById(medicineId)

                val newAmount = max(medicine.getAmount() - dose, 0.0)
                if (newAmount <= medicine.getNotificationRemains()) {
                    notEnoughMedicine = true
                }

                medicine.setAmount(newAmount)
                OrganizerApplication.getInstance().getDatabase().medicineDao()
                    .updateMedicine(medicine)
            }
        }
    }

    public fun notEnoughMedicine(): Boolean {
        return this.notEnoughMedicine
    }

    public fun updateHistoryTime(treatment: TreatmentHistory, newDate: String) {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatment_history").whereEqualTo("id", treatment.getId())
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null) {
                        return@EventListener
                    }

                    for (document in value!!) {
                        document.reference.update("movedTo", newDate)
                    }
                })
        } else {
            executor.execute {
                treatment.setMovedTo(newDate)
                OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                    .updateHistory(treatment)
            }
        }
    }

    public fun addHistory(history: TreatmentHistory) {
        history.setUserId(uid)

        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatment_history").add(history)
        } else {
            executor.execute {
                OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
                    .addHistory(history)
            }
        }
    }
}