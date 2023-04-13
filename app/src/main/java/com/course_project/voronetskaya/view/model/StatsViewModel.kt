package com.course_project.voronetskaya.view.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.course_project.voronetskaya.data.model.TreatmentHistory
import com.course_project.voronetskaya.view.OrganizerApplication
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StatsViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private var fromDate: String = ""
    private var toDate: String = ""
    private var uid: String = if (OrganizerApplication.sharedPreferences.getString("dependentId", "") != "") {
        OrganizerApplication.sharedPreferences.getString("dependentId", "")!!
    } else if (OrganizerApplication.auth.currentUser != null) {
        OrganizerApplication.auth.currentUser!!.uid
    } else {
        OrganizerApplication.sharedPreferences.getString("uid", "")!!
    }

    private var historyList: MutableLiveData<List<TreatmentHistory>> = MutableLiveData()

    public fun setFromDate(date: String) {
        this.fromDate = date

        if (OrganizerApplication.auth.currentUser != null) {
            updateHistory()
        }
    }

    public fun setToDate(date: String) {
        this.toDate = String.format("%s 23:59", date)

        if (OrganizerApplication.auth.currentUser != null) {
            updateHistory()
        }
    }

    private fun updateHistory() {
        if (OrganizerApplication.auth.currentUser != null) {
            firestore.collection("treatment_history").whereEqualTo("userId", uid)
                .whereGreaterThanOrEqualTo("movedTo", this.fromDate)
                .whereLessThanOrEqualTo("movedTo", this.toDate).addSnapshotListener(
                    EventListener { value, e ->
                        if (e != null) {
                            Log.w("Stats listener", e)
                            return@EventListener
                        }

                        val history: MutableList<TreatmentHistory> = mutableListOf()
                        for (document in value!!) {
                            history.add(document.toObject(TreatmentHistory::class.java))
                        }

                        this.historyList.value = history
                    })
        }
    }

    public fun getHistory(): LiveData<List<TreatmentHistory>> {
        if (OrganizerApplication.auth.currentUser != null) {
            return this.historyList
        }

        return OrganizerApplication.getInstance().getDatabase().treatmentHistoryDao()
            .getBetweenDates(this.fromDate, String.format("%s 23:59", this.toDate), uid)
    }
}