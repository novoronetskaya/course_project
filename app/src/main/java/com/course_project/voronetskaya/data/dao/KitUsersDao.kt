package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.course_project.voronetskaya.data.model.KitUsers

@Dao
interface KitUsersDao {
    @Query("SELECT * FROM kit_users")
    public fun getAll(): List<KitUsers>

    @Query("SELECT * FROM kit_users WHERE kit_id = :kitId")
    public fun getUsersOfKit(kitId: String): LiveData<List<KitUsers>>

    @Insert
    public fun addKitUser(kitUser: KitUsers)

    @Delete
    public fun deleteKitUser(kitUser: KitUsers)
}