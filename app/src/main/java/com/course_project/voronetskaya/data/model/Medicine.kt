package com.course_project.voronetskaya.data.model

import androidx.room.*

@Entity(tableName = "medicine")
class Medicine {
    @PrimaryKey
    private var id: String = ""

    @ColumnInfo(name = "kit_id")
    private var kitId: String = ""

    private var name: String = ""

    @ColumnInfo(name = "expiration_date")
    private var expirationDate: String = ""

    private var producer: String = ""

    private var amount: Double = 0.0

    @ColumnInfo(name = "pharm_effect")
    private var pharmEffect: String = ""

    private var symptom: String = ""

    @ColumnInfo(name = "consumption_form")
    private var consumptionForm: String = ""

    private var unit: String = ""

    @ColumnInfo(name = "notification_remains")
    private var notificationRemains: Double = 0.0

    public fun getId(): String {
        return this.id
    }

    public fun getKitId(): String {
        return this.kitId
    }

    public fun getName(): String {
        return this.name
    }

    public fun getExpirationDate(): String {
        return this.expirationDate
    }

    public fun getProducer(): String {
        return this.producer
    }

    public fun getAmount(): Double {
        return this.amount
    }

    public fun getPharmEffect(): String {
        return this.pharmEffect
    }

    public fun getSymptom(): String {
        return this.symptom
    }

    public fun getConsumptionForm(): String {
        return this.consumptionForm
    }

    public fun getUnit(): String {
        return this.unit
    }

    public fun getNotificationRemains(): Double {
        return this.notificationRemains
    }

    public fun setId(id: String) {
        this.id = id
    }

    public fun setKitId(kitId: String) {
        this.kitId = kitId
    }

    public fun setName(name: String) {
        this.name = name
    }

    public fun setExpirationDate(expirationDate: String) {
        this.expirationDate = expirationDate
    }

    public fun setProducer(producer: String) {
        this.producer = producer
    }

    public fun setAmount(amount: Double) {
        this.amount = amount
    }

    public fun setPharmEffect(pharmEffect: String) {
        this.pharmEffect = pharmEffect
    }

    public fun setSymptom(symptom: String) {
        this.symptom = symptom
    }

    public fun setConsumptionForm(consumptionForm: String) {
        this.consumptionForm = consumptionForm
    }

    public fun setUnit(unit: String) {
        this.unit = unit
    }

    public fun setNotificationRemains(notificationRemains: Double) {
        this.notificationRemains = notificationRemains
    }

    public override fun toString(): String {
        return this.name
    }
}