package com.course_project.voronetskaya.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.course_project.voronetskaya.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    public fun getAll(): List<User>

    @Query("SELECT * FROM users JOIN kit_users ON users.id = kit_users.user_id WHERE is_independent AND kit_id = :kitId")
    public fun getIndependentMembersFromKit(kitId: String): LiveData<List<User>>

    @Query("SELECT * FROM users JOIN kit_users ON users.id = kit_users.user_id WHERE NOT is_independent AND kit_id = :kitId")
    public fun getDependentMembersFromKit(kitId: String): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId")
    public fun getUserWithId(userId: String): LiveData<User>

    @Query("SELECT * FROM users WHERE is_independent LIMIT 1")
    public fun getIndependentUserLive(): LiveData<User>

    @Query("SELECT * FROM users WHERE is_independent LIMIT 1")
    public fun getIndependentUser(): User

    @Query("SELECT * FROM users")
    public fun getUsers(): LiveData<List<User>>

    @Update
    public fun updateUser(user: User)
    @Insert
    public fun addUser(user: User)

    @Delete
    public fun deleteUser(user: User)
}