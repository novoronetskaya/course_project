package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.course_project.voronetskaya.data.model.Medicine

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicine")
    public fun getAll(): List<Medicine>

    @Query("SELECT * FROM medicine WHERE kit_id = :kitId")
    public fun getMedicineFromKit(kitId: String): LiveData<List<Medicine>>

    @Query("SELECT * FROM medicine WHERE id = :medicineId")
    public fun getMedicineByIdLive(medicineId: String): LiveData<Medicine>

    @Query("SELECT * FROM medicine WHERE id = :medicineId")
    public fun getMedicineById(medicineId: String): Medicine

    @Insert
    public fun addMedicine(medicine: Medicine)

    @Update
    public fun updateMedicine(medicine: Medicine)

    @Delete
    public fun deleteMedicine(medicine: Medicine)
}