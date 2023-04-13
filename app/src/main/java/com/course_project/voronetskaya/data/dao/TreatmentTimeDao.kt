package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.course_project.voronetskaya.data.model.TreatmentTime

@Dao
interface TreatmentTimeDao {
    @Query("SELECT * FROM treatment_time")
    public fun getAll(): List<TreatmentTime>

    @Insert
    public fun addTreatmentTime(treatmentTime: TreatmentTime)

    @Query("SELECT * FROM treatment_time WHERE treatment_id = :treatmentId")
    public fun getTimeForTreatmentLive(treatmentId: String): LiveData<List<TreatmentTime>>

    @Query("SELECT * FROM treatment_time WHERE treatment_id = :treatmentId")
    public fun getTimeForTreatment(treatmentId: String): List<TreatmentTime>
}