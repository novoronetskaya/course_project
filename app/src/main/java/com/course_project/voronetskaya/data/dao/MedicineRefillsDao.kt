package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.course_project.voronetskaya.data.model.MedicineRefill

@Dao
interface MedicineRefillsDao {
    @Query("SELECT * FROM medicine_refills")
    public fun getAll(): List<MedicineRefill>

    @Insert
    public fun addMedicineRefill(medicineRefill: MedicineRefill)

    @Query("SELECT * FROM medicine_refills WHERE medicine_id = :medId AND date >= :fromDate AND date <= :toDate")
    public fun getBetweenDates(fromDate: String, toDate: String, medId: String): LiveData<List<MedicineRefill>>
}