package com.course_project.voronetskaya.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.course_project.voronetskaya.data.dao.*
import com.course_project.voronetskaya.data.model.*

@Database(
    entities = [FirstAidKit::class, KitUsers::class, Medicine::class, MedicineRefill::class,
        Treatment::class, TreatmentHistory::class, TreatmentTime::class, User::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    public abstract fun firstAidKitDao(): FirstAidKitDao
    public abstract fun kitUsersDao(): KitUsersDao
    public abstract fun medicineDao(): MedicineDao
    public abstract fun medicineRefillsDao(): MedicineRefillsDao
    public abstract fun treatmentDao(): TreatmentDao
    public abstract fun treatmentHistoryDao(): TreatmentHistoryDao
    public abstract fun treatmentTimeDao(): TreatmentTimeDao
    public abstract fun userDao(): UserDao
}