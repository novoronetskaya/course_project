package com.course_project.voronetskaya.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "kit_users", primaryKeys = ["kit_id", "user_id"])
class KitUsers {
    @ColumnInfo(name = "kit_id")
    private var kitId: String = ""

    @ColumnInfo(name = "user_id")
    private var userId: String = ""

    public fun getUserId(): String {
        return this.userId
    }

    public fun getKitId(): String {
        return this.kitId
    }

    public fun setUserId(userId: String) {
        this.userId = userId
    }

    public fun setKitId(kitId: String) {
        this.kitId = kitId
    }
}