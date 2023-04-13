package com.course_project.voronetskaya.data.model

import androidx.room.*
import com.course_project.voronetskaya.data.util.Status

@Entity(tableName = "treatment_history")
class TreatmentHistory {
    @PrimaryKey
    private var id: String = ""

    @ColumnInfo(name = "user_id")
    private var userId: String = ""

    private var dose: Double = 0.0

    @ColumnInfo(name = "medicine_name")
    private var medicineName: String = ""

    private var status: Status = Status.UNKNOWN

    @ColumnInfo(name = "schedule_date")
    private var scheduleDate: String = ""

    @ColumnInfo(name = "treatment_id")
    private var treatmentId: String = ""

    @ColumnInfo(name = "moved_to")
    private var movedTo: String = ""

    @ColumnInfo(name = "taken_at")
    private var takenAt: String = ""

    public fun getId(): String {
        return this.id
    }

    public fun getTreatmentId(): String {
        return this.treatmentId
    }

    public fun getUserId(): String {
        return this.userId
    }

    public fun getDose(): Double {
        return this.dose
    }

    public fun getStatus(): Status {
        return this.status
    }

    public fun getMedicineName(): String {
        return this.medicineName
    }

    public fun getMovedTo(): String {
        return this.movedTo
    }

    public fun getTakenAt(): String {
        return this.takenAt
    }

    public fun getScheduleDate(): String {
        return this.scheduleDate
    }

    public fun setId(id: String) {
        this.id = id
    }

    public fun setTreatmentId(treatmentId: String) {
        this.treatmentId = treatmentId
    }

    public fun setUserId(userId: String) {
        this.userId = userId
    }

    public fun setDose(dose: Double) {
        this.dose = dose
    }

    public fun setStatus(status: Status) {
        this.status = status
    }

    public fun setMovedTo(movedTo: String) {
        this.movedTo = movedTo
    }

    public fun setTakenAt(takenAt: String) {
        this.takenAt = takenAt
    }

    public fun setScheduleDate(scheduleDate: String) {
        this.scheduleDate = scheduleDate
    }

    public fun setMedicineName(medicineName: String) {
        this.medicineName = medicineName
    }
}