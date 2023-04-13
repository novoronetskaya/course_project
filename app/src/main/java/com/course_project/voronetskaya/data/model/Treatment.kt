package com.course_project.voronetskaya.data.model

import androidx.room.*
import com.course_project.voronetskaya.data.util.ConsumptionType

@Entity(tableName = "treatments")
class Treatment {
    @PrimaryKey
    private var id: String = ""

    private var name: String = ""

    @ColumnInfo(name = "medicine_id")
    private var medicineId: String = ""

    @ColumnInfo(name = "medicine_name")
    private var medicineName: String = ""

    private var instruction: String = ""

    @ColumnInfo(name = "consumption_type")
    private var consumptionType: ConsumptionType = ConsumptionType.EVERY_DAY

    @ColumnInfo(name = "start_date")
    private var startDate: String = ""

    @ColumnInfo(name = "each_n_days")
    private var eachNDays: Int = 0

    @ColumnInfo(name = "days_of_week")
    private var daysOfWeek: Int = 0

    private var length: Int = 0

    @ColumnInfo(name = "consumption_period")
    private var consumptionPeriod: Int = 0

    @ColumnInfo(name = "halt_period")
    private var haltPeriod: Int = 0

    private var unit: String = ""

    @ColumnInfo(name = "user_id")
    private var userId: String = ""

    @ColumnInfo(name = "is_active")
    private var isActive: Boolean = true

    public fun getId(): String {
        return this.id
    }

    public fun getName(): String {
        return this.name
    }

    public fun getMedicineId(): String {
        return this.medicineId
    }

    public fun getMedicineName(): String {
        return this.medicineName
    }

    public fun getInstruction(): String {
        return this.instruction
    }

    public fun getDaysOfWeek(): Int {
        return this.daysOfWeek
    }

    public fun getConsumptionType(): ConsumptionType {
        return this.consumptionType
    }

    public fun getStartDate(): String {
        return this.startDate
    }

    public fun getEachNDays(): Int {
        return this.eachNDays
    }

    public fun getConsumptionPeriod(): Int {
        return this.consumptionPeriod
    }

    public fun getHaltPeriod(): Int {
        return this.haltPeriod
    }

    public fun getUnit(): String {
        return this.unit
    }

    public fun getUserId(): String {
        return this.userId
    }

    public fun getLength(): Int {
        return this.length
    }

    public fun getIsActive(): Boolean {
        return this.isActive
    }

    public fun setId(id: String) {
        this.id = id
    }

    public fun setLength(length: Int) {
        this.length = length
    }

    public fun setName(name: String) {
        this.name = name
    }

    public fun setMedicineId(medicineId: String) {
        this.medicineId = medicineId
    }

    public fun setMedicineName(medicineName: String) {
        this.medicineName = medicineName
    }

    public fun setInstruction(instruction: String) {
        this.instruction = instruction
    }

    public fun setConsumptionType(consumptionType: ConsumptionType) {
        this.consumptionType = consumptionType
    }

    public fun setStartDate(startDate: String) {
        this.startDate = startDate
    }

    public fun setEachNDays(eachNDays: Int) {
        this.eachNDays = eachNDays
    }

    public fun setConsumptionPeriod(consumptionPeriod: Int) {
        this.consumptionPeriod = consumptionPeriod
    }

    public fun setHaltPeriod(haltPeriod: Int) {
        this.haltPeriod = haltPeriod
    }

    public fun setUnit(unit: String) {
        this.unit = unit
    }

    public fun setUserId(userId: String) {
        this.userId = userId
    }

    public fun setDaysOfWeek(daysOfWeek: Int) {
        this.daysOfWeek = daysOfWeek
    }

    public fun setIsActive(isActive: Boolean) {
        this.isActive = isActive
    }
}