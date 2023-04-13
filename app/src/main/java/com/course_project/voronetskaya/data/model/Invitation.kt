package com.course_project.voronetskaya.data.model

class Invitation {
    private var kitId: String = ""

    private var userId: String = ""

    private var invitedById: String = ""

    private var invitedByName: String = ""

    private var message: String = ""

    private var kitName: String = ""

    public fun getUserId(): String {
        return this.userId
    }

    public fun getKitId(): String {
        return this.kitId
    }

    public fun getInvitedById(): String {
        return this.invitedById
    }

    public fun getInvitedByName(): String {
        return this.invitedByName
    }

    public fun getKitName(): String {
        return this.kitName
    }

    public fun getMessage(): String {
        return this.message
    }

    public fun setUserId(userId: String) {
        this.userId = userId
    }

    public fun setKitId(kitId: String) {
        this.kitId = kitId
    }

    public fun setInvitedById(invitedById: String) {
        this.invitedById = invitedById
    }

    public fun setInvitedByName(invitedByName: String) {
        this.invitedByName = invitedByName
    }

    public fun setKitName(kitName: String) {
        this.kitName = kitName
    }

    public fun setMessage(message: String) {
        this.message = message
    }
}