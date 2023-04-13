package com.course_project.voronetskaya.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_refills")
class MedicineRefill {
    @PrimaryKey
    private var id: String = ""

    @ColumnInfo(name = "user_id")
    private var userId: String = ""

    @ColumnInfo(name = "medicine_id")
    private var medicineId: String = ""

    private var date: String = ""

    private var amount: Int = 0

    public fun getId(): String {
        return this.id
    }

    public fun getUserId(): String {
        return this.userId
    }

    public fun getMedicineId(): String {
        return this.medicineId
    }

    public fun getAmount(): Int {
        return this.amount
    }

    public fun getDate(): String {
        return this.date
    }

    public fun setId(id: String) {
        this.id = id
    }

    public fun setUserId(userId: String) {
        this.userId = userId
    }

    public fun setMedicineId(medicineId: String) {
        this.medicineId = medicineId
    }

    public fun setAmount(amount: Int) {
        this.amount = amount
    }

    public fun setDate(date: String) {
        this.date = date
    }
}