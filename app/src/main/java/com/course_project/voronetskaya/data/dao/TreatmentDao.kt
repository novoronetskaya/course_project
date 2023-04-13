package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.course_project.voronetskaya.data.model.Treatment

@Dao
interface TreatmentDao {
    @Query("SELECT * FROM treatments")
    public fun getAll(): List<Treatment>

    @Query("SELECT * FROM treatments WHERE id = :id LIMIT 1")
    public fun getTreatmentWithIdLive(id: String): LiveData<Treatment>

    @Query("SELECT * FROM treatments WHERE id = :id LIMIT 1")
    public fun getTreatmentWithId(id: String): Treatment

    @Query("SELECT * FROM treatments WHERE user_id = :userId")
    public fun getUserTreatments(userId: String): LiveData<List<Treatment>>

    @Query("SELECT * FROM treatments WHERE user_id = :userId AND is_active")
    public fun getUserActiveTreatmentsLive(userId: String): LiveData<List<Treatment>>

    @Query("SELECT * FROM treatments WHERE user_id = :userId AND is_active")
    public fun getUserActiveTreatments(userId: String): List<Treatment>

    @Insert
    public fun insertTreatment(treatment: Treatment)

    @Update
    public fun updateTreatment(treatment: Treatment)

    @Delete
    public fun deleteTreatment(treatment: Treatment)
}