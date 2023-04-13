package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.course_project.voronetskaya.data.model.TreatmentHistory

@Dao
interface TreatmentHistoryDao {
    @Query("SELECT * FROM treatment_history")
    public fun getAll(): List<TreatmentHistory>

    @Query("SELECT * FROM treatment_history WHERE moved_to LIKE :date AND status NOT IN ('TAKEN', 'CANCELLED') AND user_id = :uid")
    public fun getForDateLive(date: String, uid: String): LiveData<List<TreatmentHistory>>

    @Query("SELECT * FROM treatment_history WHERE moved_to LIKE :date AND status NOT IN ('TAKEN', 'CANCELLED') AND user_id = :uid")
    public fun getForDate(date: String, uid: String): List<TreatmentHistory>

    @Insert
    public fun addHistory(treatmentHistory: TreatmentHistory)

    @Update
    public fun updateHistory(treatmentHistory: TreatmentHistory)

    @Query("SELECT * FROM treatment_history WHERE id = :id LIMIT 1")
    public fun getHistoryWithId(id: String): TreatmentHistory

    @Query("SELECT * FROM treatment_history WHERE schedule_date >= :fromDate AND schedule_date <= :toDate AND user_id = :uid")
    public fun getBetweenDates(fromDate: String, toDate: String, uid: String): LiveData<List<TreatmentHistory>>
}