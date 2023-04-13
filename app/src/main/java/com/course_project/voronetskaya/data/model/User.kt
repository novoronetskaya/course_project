package com.course_project.voronetskaya.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User {
    @PrimaryKey
    private var id: String = ""

    @ColumnInfo(name = "first_name")
    private var firstName: String = ""

    private var surname: String = ""

    private var birthday: String = ""

    @ColumnInfo(name = "is_independent")
    private var isIndependent: Boolean = true

    public fun getId(): String {
        return this.id
    }

    public fun getFirstName(): String {
        return this.firstName
    }

    public fun getSurname(): String {
        return this.surname
    }

    public fun getBirthday(): String {
        return this.birthday
    }

    public fun getIsIndependent(): Boolean {
        return this.isIndependent
    }

    public fun setId(id: String) {
        this.id = id
    }

    public fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    public fun setSurname(surname: String) {
        this.surname = surname
    }

    public fun setBirthday(birthday: String) {
        this.birthday = birthday
    }

    public fun setIsIndependent(isIndependent: Boolean) {
        this.isIndependent = isIndependent
    }
}
