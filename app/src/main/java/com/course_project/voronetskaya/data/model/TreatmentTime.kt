package com.course_project.voronetskaya.data.model

import androidx.room.*

@Entity(tableName = "treatment_time")
class TreatmentTime {
    @PrimaryKey
    private var id: String = ""

    @ColumnInfo(name = "treatment_id")
    private var treatmentId: String = ""

    private var hour: Int = 0

    private var minute: Int = 0

    private var dose: Double = 0.0

    public fun getId(): String {
        return this.id
    }

    public fun getTreatmentId(): String {
        return this.treatmentId
    }

    public fun getHour(): Int {
        return this.hour
    }

    public fun getMinute(): Int {
        return this.minute
    }

    public fun getDose(): Double {
        return this.dose
    }

    public fun setId(id: String) {
        this.id = id
    }

    public fun setTreatmentId(treatmentId: String) {
        this.treatmentId = treatmentId
    }

    public fun setHour(hour: Int) {
        this.hour = hour
    }

    public fun setMinute(minute: Int) {
        this.minute = minute
    }

    public fun setDose(dose: Double) {
        this.dose = dose
    }
}