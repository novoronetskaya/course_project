package com.course_project.voronetskaya.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kits")
class FirstAidKit {
    @PrimaryKey
    private var id: String = ""

    private var name: String = ""

    @ColumnInfo(name = "admin_id")
    private var adminId: String = ""

    public fun getName(): String {
        return this.name
    }

    public fun getAdminId(): String {
        return this.adminId
    }

    public fun getId(): String {
        return this.id
    }

    public fun setName(name: String) {
        this.name = name
    }

    public fun setAdminId(adminId: String) {
        this.adminId = adminId
    }

    public fun setId(id: String) {
        this.id = id
    }

    public override fun toString(): String {
        return this.name
    }
}
