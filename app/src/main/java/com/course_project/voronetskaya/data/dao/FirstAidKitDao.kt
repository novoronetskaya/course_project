package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.course_project.voronetskaya.data.model.FirstAidKit

@Dao
interface FirstAidKitDao {
    @Query("SELECT * FROM kits")
    public fun getAll(): List<FirstAidKit>

    @Query("SELECT * FROM kits")
    public fun getAllKits(): LiveData<List<FirstAidKit>>

    @Query("SELECT * FROM kits WHERE id = :kitId LIMIT 1")
    public fun getKitWithId(kitId: String): LiveData<FirstAidKit>

    @Insert
    public fun addKit(kit: FirstAidKit)

    @Delete
    public fun deleteKit(kit: FirstAidKit)
}